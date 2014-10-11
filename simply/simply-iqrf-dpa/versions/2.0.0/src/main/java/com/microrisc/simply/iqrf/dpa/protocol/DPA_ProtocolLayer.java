

package com.microrisc.simply.iqrf.dpa.protocol;

import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.BaseCallResponse;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.NetworkLayerService;
import com.microrisc.simply.SimpleMessageSource;
import com.microrisc.simply.SimpleMethodMessageSource;
import com.microrisc.simply.asynchrony.BaseAsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.DPA_ResponseCode;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.SimpleDPA_AsynchronousMessageSource;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastRequest;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;
import com.microrisc.simply.network.BaseNetworkData;
import com.microrisc.simply.protocol.AbstractProtocolLayer;
import com.microrisc.simply.protocol.CallRequestComparator;
import com.microrisc.simply.protocol.MessageConvertor;
import com.microrisc.simply.protocol.SimpleRequestToResponseMatcher;
import com.microrisc.simply.types.ValueConversionException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protocol layer based on DPA_ProtocolProperties of IQRF.
 * 
 * @author Michal Konopa
 */
public final class DPA_ProtocolLayer extends AbstractProtocolLayer {
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
    
    
    /** List of all requests, which was sent to network layer. */
    private List<TimeRequest> sentRequests = new LinkedList<TimeRequest>();
    
    /** Synchronization object for {@code sentRequest} data structure. */
    private final Object synchroSentRequest = new Object();
   
    
    /** Default maximal time duration [in ms] of sent requests in the protocol layer. */
    public static final long MAX_REQUEST_DURATION_DEFAULT = 10000;
    
    /** Maximal time duration [in ms] of sent requests in the protocol layer. */
    private long maxRequestDuration = MAX_REQUEST_DURATION_DEFAULT;
    
    /** 
     * Deletes invalid requests. Request is invalid, if: <br> 
     * - its presence in the list exceeds maximal time duration limit
     * - is equal to specified new request
     */
    private void deleteInvalidRequests(CallRequest newRequest) {
        logger.debug("deleteInvalidRequests - start: newRequest={}", newRequest);
        
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
        
        // Waiting for broadcast responder thread to terminate. 
        // Cancelling of broadcast responder thread has higher priority than main 
        // thread interruption. 
        while ( broadcastResponder.isAlive() ) {
            try {
                if ( broadcastResponder.isAlive( )) {
                    broadcastResponder.join();
                }
            } catch (InterruptedException e) {
                // restoring interrupt status
                Thread.currentThread().interrupt();
                logger.warn("broadcast responder terminating - thread interrupted");
            }
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
    }
    
    /**
     * This method works as follows: <br>
     * 1. Converts specified request into sequence of bytes. If some error has
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
        
        if ( request instanceof BroadcastRequest ) {
            synchronized ( synchroSentBroadcastRequest ) {
                networkLayerService.sendData( new BaseNetworkData(protoMsg, request.getNetworkId()) );
                sentBroadcastRequests.add( new TimeRequest(request, System.currentTimeMillis()) );
                synchroSentBroadcastRequest.notify();
            }
        } else {
            // must be performed altogether
            // if response comes before sentRequest.add(), error encounters
            synchronized ( synchroSentRequest ) {
                // maintenance of already sent requests
                maintainSentRequest(request);
                networkLayerService.sendData( new BaseNetworkData(protoMsg, request.getNetworkId()) );
                sentRequests.add( new TimeRequest(request, System.currentTimeMillis()) );
            }
        }
        
        logger.debug("sendRequest - end");
    }
    
    @Override
    public void start() throws SimplyException {
        logger.debug("start - start:");
        
        super.start();
        broadcastResponder.start();
        
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
            logger.error("Error in determining response code: response={}", networkData);
            return;
        }
        
        // all confirmations are filtered out
        if ( responseCode == DPA_ResponseCode.CONFIRMATION ) {
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
        
        // processing the message
        processMessage(message, networkData.getData());
        
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
