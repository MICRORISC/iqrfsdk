/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microrisc.simply.iqrf.dpa.v210.protocol;

import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.BaseCallResponse;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.NetworkLayerService;
import com.microrisc.simply.SimpleMessageSource;
import com.microrisc.simply.SimpleMethodMessageSource;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.asynchrony.BaseAsynchronousMessage;
import com.microrisc.simply.errors.NetworkInternalError;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.SimpleDPA_AsynchronousMessageSource;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastRequest;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;
import com.microrisc.simply.iqrf.dpa.v210.DPA_ResponseCode;
import com.microrisc.simply.iqrf.dpa.v210.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers.CoordinatorStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v210.typeconvertors.DPA_ConfirmationConvertor;
import com.microrisc.simply.iqrf.dpa.v210.types.DPA_Confirmation;
import com.microrisc.simply.network.BaseNetworkData;
import com.microrisc.simply.protocol.AbstractProtocolLayer;
import com.microrisc.simply.protocol.CallRequestComparator;
import com.microrisc.simply.protocol.MessageConvertor;
import com.microrisc.simply.protocol.SimpleRequestToResponseMatcher;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protocol layer based on DPA_ProtocolProperties of IQRF.
 * 
 * @author Michal Konopa
 */
public final class DPA_ProtocolLayer 
extends AbstractProtocolLayer
implements ProtocolStateMachineListener
{
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_ProtocolLayer.class);

    
    /**
     * Binds sent requests with theirs time of sending.
     */
    private class TimeRequest {
        // sent request
        CallRequest request;
        
        // time, at which the request was sent
        long sentTime;
        
        TimeRequest(CallRequest request, long sentTime) {
            this.request = request;
            this.sentTime = sentTime;
        }
    }
    
    // information about potentionally time unlimited request - i.e. request,
    // whose response time is not inherently bounded, i.e. discovery
    private static class TimeUnlimitedRequestInfo {
        Class devIface;
        String methodId;
        
        public TimeUnlimitedRequestInfo(Class devIface, String methodId) {
            this.devIface = devIface;
            this.methodId = methodId;
        }
        
        @Override
        public boolean equals(Object o) {
            if ( !(o instanceof TimeUnlimitedRequestInfo) ) {
                return false;
            }
            
            TimeUnlimitedRequestInfo requestInfo = (TimeUnlimitedRequestInfo)o;
            return ( 
                    requestInfo.devIface.equals(this.devIface) 
                    && requestInfo.methodId.equals(this.methodId)  
            );
        }
    }
    
    // set of time unlimited requests
    private Set<TimeUnlimitedRequestInfo> timeUnlimitedRequests = new HashSet<>();
    
    // initialize set of time limited requests
    private void initTimeUnlimitedRequests() {
        timeUnlimitedRequests.add(
                new TimeUnlimitedRequestInfo(
                        Coordinator.class,
                        CoordinatorStandardTransformer
                            .getInstance()
                            .transform(Coordinator.MethodID.RUN_DISCOVERY)
                )
        );
        timeUnlimitedRequests.add(
                new TimeUnlimitedRequestInfo(
                        Coordinator.class,
                        CoordinatorStandardTransformer
                            .getInstance()
                            .transform(Coordinator.MethodID.BOND_NODE)
                )
        );
    }
    
    // indicates, wheather the specified request is time unlimited
    private static boolean isTimeUnlimitedRequest(CallRequest request) {
        if ( request.getDeviceInterface() != Coordinator.class ) {
            return false;
        }
        
        // checking of transformed method ID
        String discoMethodConvId = CoordinatorStandardTransformer
                .getInstance()
                .transform(Coordinator.MethodID.RUN_DISCOVERY
        ); 
        
        return ( request.getMethodId().equals(discoMethodConvId) ); 
    }
    
    // indicates, wheather a time unlimited request is in process
    private volatile boolean isTimeUnlimitedRequestInProcess = false;
    
    
    /** Last sent request. */
    private TimeRequest lastRequest = null;
    
    /** List of all requests, which was sent to network layer. */
    private List<TimeRequest> sentRequests = new LinkedList<>();
    
    /** Synchronization object for {@code sentRequest} data structure. */
    private final Object synchroSentRequest = new Object();
   
    
    /**
     * For ensuring that sending a request to connected network together with 
     * performing of all needed settings including manipulation with Protocol Machine
     * will be executed all at once - without interruption of any other threads.
     */
    private final Object synchroSendOrReceive = new Object();
    
    
    /** Default maximal time duration [in ms] of sent requests in the protocol layer. */
    public static final long MAX_REQUEST_DURATION_DEFAULT = 10000;
    
    /** Maximal time duration [in ms] of sent requests in the protocol layer. */
    private long maxRequestDuration = MAX_REQUEST_DURATION_DEFAULT;
    
    
    /** State machine supporting DPA protocol communication. */
    private ProtocolStateMachine protoMachine = null;
    
    // state changed in protocol machine
    private final Object protoMachineStateChangeSignal = new Object();
    
    // type of errors encontered during communication with network layer
    private static enum COMMUNICATION_ERROR_TYPE {
        CONFIRMATION_TIMEOUTED,
        RESPONSE_TIMEOUTED
    }
    
    // waits before sending next request 
    private void doWaitBeforeSendRequest() throws InterruptedException {
        ProtocolStateMachine.State machineState = null;
        
        synchronized ( protoMachineStateChangeSignal ) {
            machineState = protoMachine.getState();
            while ( (machineState != ProtocolStateMachine.State.FREE_FOR_SEND)  
                    && (machineState != ProtocolStateMachine.State.WAITING_FOR_CONFIRMATION_ERROR)
                    && (machineState != ProtocolStateMachine.State.WAITING_FOR_RESPONSE_ERROR)
                  ) {
                protoMachineStateChangeSignal.wait();
                machineState = protoMachine.getState();
            }
        }
        
        // checking if it is possible to send new request
        switch ( machineState ) {
            case WAITING_FOR_CONFIRMATION_ERROR:
            case WAITING_FOR_RESPONSE_ERROR:
                // reseting machine after error
                protoMachine.resetAfterError();
                break;
            case FREE_FOR_SEND:
                break;
            default:
                throw new IllegalStateException("State not expected: " + machineState);
        }
    }
    
    
    /** 
     * Deletes invalid requests. Request is invalid, if: <br> 
     * - its presence in the list exceeds maximal time duration limit
     * - is equal to specified new request
     */
    private void deleteInvalidRequests(CallRequest newRequest) {
        logger.debug("deleteInvalidRequests - start: newRequest={}", newRequest);
        
        synchronized ( synchroSentRequest ) {
            Iterator<TimeRequest> requestIt = sentRequests.iterator();
            while ( requestIt.hasNext() ) {
                TimeRequest sentRequest = requestIt.next();
                long requestDuration = System.currentTimeMillis() - sentRequest.sentTime;

                if ( requestDuration > maxRequestDuration ) {
                    logger.debug("removed request ( time exceed ): {}", sentRequest);
                    requestIt.remove();
                    continue;
                }

                if ( CallRequestComparator.areEqual(sentRequest.request, newRequest) ) {
                    logger.debug("removed request ( equality ) : {}", sentRequest);
                    requestIt.remove();
                }
            }
        }
        
        logger.debug("deleteInvalidRequests - end");
    }
    
    /**
     * Maintenace of sent request list.
     * @param callRequest new incomming request
     */
    private void maintainSentRequest(CallRequest callRequest) {
        deleteInvalidRequests(callRequest);
    }
    
    /**
     * Returns {@code true} if the specified response can be a response for 
     * specified request. Otherwise returns {@code false}.
     * @param request request to check
     * @param callResponse response to check
     * @return {@code true} if the specified response can be a reponse for 
     *                      specified request <br>
     *         {@code false}, otherwise
     */
    private boolean match(CallRequest request, BaseCallResponse callResponse) {
        return SimpleRequestToResponseMatcher.match(request, callResponse);
    }
    
    /**
     * Returns call request, which is the specified response the response on
     * that request. If no such request exists, returns {@code null}.
     * @param response
     * @return call request, which is the specified response the response on
     *         that request
     */
    private TimeRequest getCauseRequest(BaseCallResponse response) {
        logger.debug("getCauseRequest - start: response={}", response);
        
        for ( TimeRequest timeRequest : sentRequests) {
            if ( match(timeRequest.request, response) ) {
                logger.debug("getCauseRequest - end: {}", timeRequest.request);
                return timeRequest;
            }
        }
        
        logger.debug("getCauseRequest - end: null");
        return null;
    }
    
    /**
     * Processes specified message.
     * @param message message to process
     * @paeram msgPacket message source protocol packet
     */
    private void processMessage(AbstractMessage message, short[] msgPacket) {
        logger.debug("processMessage - start: message={}", message);
        
        if ( !(message instanceof BaseCallResponse) ) {
            if ( message instanceof BaseAsynchronousMessage ) {
                // call a listener - must be synchronized because of broadcast responder thread
                synchronized ( synchroListener ) {
                    listener.onGetMessage(message);
                }
            } else {
                logger.warn("Uknown type of the message={}, discarded", message);
                logger.debug("processMessage - end");
            }
            return;
        }
        
        BaseCallResponse response = ( BaseCallResponse) message;
        boolean causeRequestFound = false;
        
        synchronized ( synchroSentRequest ) {
            TimeRequest causeRequest = getCauseRequest(response);
            if ( causeRequest != null ) {
                response.setRequestId(causeRequest.request.getId());
                sentRequests.remove(causeRequest);
                causeRequestFound = true;
            } 
        }
        
        if ( causeRequestFound ) {
            synchronized ( synchroListener ) {
                listener.onGetMessage(message);
            }
        } else {
            logger.info("No cause request found, message={} handled as asynchronous", message); 
            BaseAsynchronousMessage asyncMsg = new DPA_AsynchronousMessage(
                    response.getMainData(), response.getAdditionalData(), 
                    new SimpleDPA_AsynchronousMessageSource(
                            response.getMessageSource(), 
                            DPA_ProtocolProperties.getPeripheralNumber(msgPacket)
                    )
            );
            synchronized ( synchroListener ) {
                listener.onGetMessage(asyncMsg);
            }
        }
        
        logger.debug("processResponse - end");
    }
    
    // sends information about encountered error to the registered listener
    private void sendErrorMessage(COMMUNICATION_ERROR_TYPE errorType, TimeRequest causeRequest) {
        logger.debug("sendErrorMessage - start: causeRequest={}", causeRequest);
        
        String errorMsg = null;
        switch ( errorType ) {
            case CONFIRMATION_TIMEOUTED:
                errorMsg = "Confirmation timeouted";
                break;
            case RESPONSE_TIMEOUTED:
                errorMsg = "Response timeouted";
                break;
            default:
                throw new IllegalStateException("Error " + errorType + " not expected.");
        }
        
        BaseCallResponse errorResponse = new BaseCallResponse(
                new SimpleMethodMessageSource(
                        new SimpleMessageSource(
                                causeRequest.request.getNetworkId(), 
                                causeRequest.request.getNodeId()
                        ), 
                        causeRequest.request.getDeviceInterface(), 
                        causeRequest.request.getMethodId()
                       ), 
                new NetworkInternalError(errorMsg)
        );
        
        synchronized ( synchroSentRequest ) {
            errorResponse.setRequestId(causeRequest.request.getId());
            sentRequests.remove(causeRequest);
        }
        
        synchronized ( synchroListener ) {
            listener.onGetMessage(errorResponse);
        }
        
        logger.debug("sendErrorMessage - end");
    }
    
    /** List of broadcast requests, which was sent to network layer. */
    private Queue<TimeRequest> sentBroadcastRequests = new ConcurrentLinkedQueue<>();
    
    /** Synchronization object for {@code sentBroadcastRequest} data structure. */
    private final Object synchroSentBroadcastRequest = new Object();
    
    
    /**
     * Calling listener callback method to send broadcast responses.
     */
    private class BroadcastResponder extends Thread {
        
        @Override
        public void run() {
            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("Broadcast responder thread interrupted");
                    return;
                }

                TimeRequest tmRequest = null;
                
                synchronized ( synchroSentBroadcastRequest ) {
                    while ( sentBroadcastRequests.isEmpty() ) {
                        try {
                            synchroSentBroadcastRequest.wait();
                        } catch ( InterruptedException e ) {
                            logger.warn("Broadcast responder thread interrupted while waiting", e);
                            return;
                        }
                    }
                    tmRequest = sentBroadcastRequests.poll(); 
                }
                
                BroadcastRequest request = (BroadcastRequest) tmRequest.request;
                BaseCallResponse response = new BaseCallResponse(
                        BroadcastResult.OK, 
                        null,
                        new SimpleMethodMessageSource( 
                                new SimpleMessageSource(request.getNetworkId(), request.getNodeId()), 
                                request.getDeviceInterface(), 
                                request.getMethodId()
                        ),
                        null
                );
                response.setRequestId(request.getId());
                
                synchronized ( synchroListener ) {
                    listener.onGetMessage(response);
                }
            }
        }
    }
    
    // broadcast responder thread
    private Thread broadcastResponder = null;
    
    // timeout to wait for worker threads to join
    private static final long JOIN_WAIT_TIMEOUT = 2000;
    
    /** 
     * Synchronization object for listener. 
     * Listener will be called from incomming network thread and from broadcast
     * responder thread concurently.
     */
    private final Object synchroListener = new Object();
    
    /**
     * Terminates broadcast responder thread.
     */
    private void terminateBroadcastResponderThread() {
        logger.debug("terminateBroadcastResponderThread - start:");
        
        // termination signal to broadcast responder thread
        broadcastResponder.interrupt();
        
        // indicates, wheather this thread is interrupted
        boolean isInterrupted = false;
        
        try {
            if ( broadcastResponder.isAlive( )) {
                broadcastResponder.join(JOIN_WAIT_TIMEOUT);
            }
        } catch ( InterruptedException e ) {
            isInterrupted = true;
            logger.warn("broadcast responder terminating - thread interrupted");
        }
        
        if ( !broadcastResponder.isAlive() ) {
            logger.info("Broadcast responder stopped.");
        }
        
        if ( isInterrupted ) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("broadcast responding stopped.");
        logger.debug("terminateBroadcastResponderThread - end");
    }
    
    
    /**
     * Creates new protocol layer object with specified network layer to use.
     * @param networkLayerService network layer service object to use
     * @param msgConvertor message convertor to use
     */
    public DPA_ProtocolLayer( NetworkLayerService networkLayerService, 
            MessageConvertor msgConvertor
    ) {
        super(networkLayerService, msgConvertor);
        broadcastResponder = new BroadcastResponder();
        protoMachine = new ProtocolStateMachine();
        initTimeUnlimitedRequests();
    }
    
    @Override
    public void onFreeForSend() {
        synchronized ( protoMachineStateChangeSignal ) {
            protoMachineStateChangeSignal.notifyAll();
        }
    }
    
    @Override
    public void onConfirmationTimeouted() {
        sendErrorMessage(COMMUNICATION_ERROR_TYPE.CONFIRMATION_TIMEOUTED, lastRequest);
        synchronized ( protoMachineStateChangeSignal ) {
            protoMachineStateChangeSignal.notifyAll();
        }
    }
    
    @Override
    public void onResponseTimeouted() {
        sendErrorMessage(COMMUNICATION_ERROR_TYPE.RESPONSE_TIMEOUTED, lastRequest);
        synchronized ( protoMachineStateChangeSignal ) {
            protoMachineStateChangeSignal.notifyAll();
        }
    }
    
    /**
     * This method works as follows: <br>
     * 1. Converts specified request into sequence of bytes. If an error has
     *    occured during the conversion, error of type {@code ProcessingRequestAtProtocolLayerError} 
     *    is created, error response is created and {@code processErrorResponse} 
     *    method is called.
     * 2. New {@code NetworkData} object is created and send into the network layer. 
     * 3. New {@code TimeRequest} object is created and stored into {@code sentRequest}.
     */
    @Override
    public void sendRequest(CallRequest request) throws SimplyException {
        logger.debug("sendRequest - start: request={}", request);
        
        // conversion to format used by application protocol
        short[] protoMsg = msgConvertor.convertToProtoFormat(request);
        
        // waiting until it is possible to send new request
        try {
            doWaitBeforeSendRequest();
        } catch ( InterruptedException ex ) {
            logger.error(
                "Thread interrupted while waiting for sending next request."
                + "Request will not be sent", ex
            );
            return;
        }
        
        lastRequest = new TimeRequest(request, System.currentTimeMillis());
        
        if ( request instanceof BroadcastRequest ) {
            synchronized ( synchroSentBroadcastRequest ) {
                networkLayerService.sendData( new BaseNetworkData(protoMsg, request.getNetworkId()) );
                sentBroadcastRequests.add( lastRequest );
                protoMachine.newRequest(request);
                synchroSentBroadcastRequest.notify();
            }
        } else {
            // must be performed altogether to eliminating the case, when 
            // response comes to early
            synchronized ( synchroSendOrReceive ) {
                // maintenance of already sent requests
                maintainSentRequest(request);
                networkLayerService.sendData( new BaseNetworkData(protoMsg, request.getNetworkId()) );
                synchronized ( synchroSentRequest ) {
                    sentRequests.add( lastRequest );
                }
                if ( isTimeUnlimitedRequest(request) ) {
                    isTimeUnlimitedRequestInProcess = true;
                } else {
                    isTimeUnlimitedRequestInProcess = false;
                    protoMachine.newRequest(request);
                }
            }
        }
        
        logger.debug("sendRequest - end");
    }
    
    @Override
    public void start() throws SimplyException {
        logger.debug("start - start:");
        
        super.start();
        broadcastResponder.start();
        protoMachine.start();
        protoMachine.registerListener(this);
        
        logger.info("Started");
        logger.debug("start - end");
    }
    
    @Override
    public void destroy() {
        logger.debug("destroy - start:");
        
        super.destroy();
        
        sentRequests.clear();
        sentRequests = null;
        
        terminateBroadcastResponderThread();
        broadcastResponder = null;
        
        sentBroadcastRequests.clear();
        sentBroadcastRequests = null;
        
        protoMachine.unregisterListener();
        protoMachine.destroy();
        protoMachine = null;
        
        logger.info("Destroyed");
        logger.debug("destroy - end");
    }
    
    @Override
    public void onGetData(NetworkData networkData) {
        logger.debug("onGetData - start: {}", networkData);
        
        // no listener - nothing to do
        if ( listener == null ) {
            logger.debug("onGetData - end: no listener connected");
            return;
        }
        
        DPA_ResponseCode responseCode = null;
        try {
            responseCode = DPA_ProtocolProperties.getResponseCode(networkData.getData());
        } catch ( ValueConversionException ex ) {
            logger.error("Error in determining response code. Network data={}", networkData);
            return;
        }
        
        // all confirmations are filtered out
        if ( responseCode == DPA_ResponseCode.CONFIRMATION ) {
            DPA_Confirmation confirmation = null;
            
            try {
                short[] confirmData = new short[
                            networkData.getData().length - DPA_ProtocolProperties.PDATA_START
                        ];
                System.arraycopy(networkData.getData(), DPA_ProtocolProperties.PDATA_START, 
                        confirmData, 0, confirmData.length
                );
                confirmation = (DPA_Confirmation)DPA_ConfirmationConvertor.
                        getInstance().toObject(confirmData);
            } catch ( ValueConversionException ex ) {
                logger.error("Error in conversion of confirmation. Network data={}", networkData);
                return;
            }
            
            if ( !isTimeUnlimitedRequestInProcess ) {
                synchronized ( synchroSendOrReceive ) {
                    try {
                        protoMachine.confirmationReceived(confirmation);
                    } catch ( StateTimeoutedException ex ) {
                        logger.error("Confirmation reception too late. Waiting timeouted.");
                        return;
                    }
                }
            }
            
            logger.debug("onGetData - confirmation arrived: {}", networkData);
            return;
        }
        
        // creating message by conversion data comming from connected network 
        AbstractMessage message = null;
        try {
            message = msgConvertor.convertToDOFormat(networkData);
        } catch ( SimplyException e ) {
            logger.error("Conversion error on incomming data", e);
            return;
        }
        
        synchronized ( synchroSendOrReceive ) {
            if ( !isTimeUnlimitedRequestInProcess ) {
                try {
                    protoMachine.responseReceived(networkData.getData());
                } catch ( StateTimeoutedException ex ) {
                    logger.error("Response reception too late. Waiting timeouted.");
                    return;
                }
            }
            
            // processing the message incomming from network
            processMessage(message, networkData.getData());
        }
        
        logger.debug("onGetData - end");
    }
    
    /** Checks specified maximal time duration. */
    private long checkMaxRequestDuration(long maxRequestDuration) {
        if ( maxRequestDuration < 0 ) {
            throw new IllegalArgumentException("Maximal duration of sent request"
                    + "must be nonnegative");
        }
        return maxRequestDuration;
    }
    
    /**
     * Sets maximal time duration [in ms] of sent requests in this protocol layer.
     * @param maxRequestDuration duration [in ms] to set
     * @throws IllegalArgumentException if the value is less then {@code 0}
     */
    public void setMaxRequestDuration(long maxRequestDuration) {
        this.maxRequestDuration = checkMaxRequestDuration(maxRequestDuration);
    }
}
