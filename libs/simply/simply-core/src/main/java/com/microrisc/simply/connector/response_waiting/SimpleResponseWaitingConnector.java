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

package com.microrisc.simply.connector.response_waiting;

import com.microrisc.simply.AbstractMessage;
import static com.microrisc.simply.CallRequestProcessingState.ERROR;
import static com.microrisc.simply.CallRequestProcessingState.RESULT_ARRIVED;
import static com.microrisc.simply.CallRequestProcessingState.WAITING_FOR_PROCESSING;
import static com.microrisc.simply.CallRequestProcessingState.WAITING_FOR_RESULT;
import com.microrisc.simply.BaseCallResponse;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.CallRequestProcessingInfo;
import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.CallResult;
import com.microrisc.simply.ConnectedDeviceObject;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.ProtocolLayerService;
import com.microrisc.simply.connector.AbstractConnector;
import com.microrisc.simply.connector.CallResultsSender;
import com.microrisc.simply.errors.DispatchingRequestToProtocolLayerError;
import com.microrisc.simply.errors.CallRequestProcessingError;
import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of {ResponseWaitingConnector} interface.  
 * 
 * @author Michal Konopa
 */
public final class SimpleResponseWaitingConnector 
extends AbstractConnector implements ResponseWaitingConnector {
    /** Logger. */
    private static final Logger logger 
            = LoggerFactory.getLogger(SimpleResponseWaitingConnector.class);
    
    // multiplier for getting number of miliseconds from number of nanoseconds 
    private static final double NANOSEC_TO_MILISEC = 1.0/1000000;
    
    
    /**
     * Call request associated with information needed to process that call request.
     */
    private class CallRequestToProcess {
        CallRequest callRequest;
        long maxProcTime;
        
        public CallRequestToProcess(CallRequest callRequest, long maxProcTime) {
            this.callRequest = callRequest;
            this.maxProcTime = maxProcTime;
        }
    }
    
    private class IdleRequest {
        CallRequestToProcess reqToProc;
        long startIdleTime;
        
        public IdleRequest(CallRequestToProcess reqToProc, long startIdleTime) {
            this.reqToProc = reqToProc;
            this.startIdleTime = startIdleTime;
        }
    }
    
    private CallResultsSender callResultsSender = null;
    
    /**
     * Responsible for sending requests to underlaying network and receiving 
     * messages (responses) from that network.
     */
    private class WorkerThread extends Thread {
        /** Time, when last request was sended. */
        private long lastSendTime = 0;
        
        // call request, which was lastly sent to underlaying network
        private CallRequestToProcess lastRequestToProc =  null;
        
        // synchronization object for access to lastRequestToProc
        private final Object synchroLastRequestToProc = new Object();
        
        // indicates, wheather the last request was cancelled
        private volatile boolean isCancelledLastRequest = false;
        
        // synchronization for cancelling a request
        private final Object syncCancelRequest = new Object();
        
        /** 
         * Returns value of sleep time before sending next request to
         * protocol layer.
         * @param sendAttempt send attempt
         */
        private long getSleepTimeBeforeSend(int sendAttempt) {
            long waitedTime = System.currentTimeMillis() - lastSendTime;
            if ( sendAttempt > 0 ) {
                if ( attemptPause > betweenSendPause ) {
                    return ( attemptPause - waitedTime );
                } else {
                    return ( betweenSendPause - waitedTime );
                }
            }
            return ( betweenSendPause - waitedTime );
        } 
        
        /**
         * Sends specified DO method call request to protocol layer.
         * @param request DO method call-request
         */
        private void sendRequestToProtocolLayer(CallRequest request) 
                throws SimplyException, InterruptedException 
        {
            logger.debug("sendRequestToProtocolLayer - start: request={}", request);
            
            int sendAttempt = 0;
            boolean sentOK = false;
            while ( (sendAttempt < maxSendAttempts && ( sentOK == false )) ) { 
                long sleepTime = getSleepTimeBeforeSend(sendAttempt);
                
                // pause between sending request to protocol layer
                if ( sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
                
                protocolLayerService.sendRequest(request);
                sentOK = true;

                lastSendTime = System.currentTimeMillis();                
                sendAttempt++;
            }
            
            logger.debug("sendRequestToProtocolLayer - end:");
        }
        
        /**
         * Processes specified response from underlaying network.
         * @param response response to process
         */
        private CallRequestProcessingInfo createCallRequestProcessingInfo( 
                BaseCallResponse response 
        ) {
            logger.debug("createCallRequestProcessingInfo - start: {}", response);
            
            CallResult callResult = new CallResult( response.getMainData(), 
                    response.getAdditionalData()
            );
            
            CallRequestProcessingInfo procInfo = null;
            CallRequestProcessingError procError = response.getProcessingError();
            if  ( procError != null ) {
                procInfo = new CallRequestProcessingInfo(
                    currProcRequestInfo.getRequestId(), ERROR, callResult, procError
                );
            } else {
                procInfo = new CallRequestProcessingInfo(
                    currProcRequestInfo.getRequestId(), RESULT_ARRIVED, callResult
                );
            }
            
            logger.debug("createCallRequestProcessingInfo - end: {}", procInfo);
            return procInfo;
        }
        
        
        // indicates, wheather a response arrived for the last call request
        private boolean responseArrivedForLastRequest() {
            logger.debug("responseArrivedForLastRequest - start: ");
            
            if ( msgFromNetwork.isEmpty() ) {
                logger.debug("responseArrivedForLastRequest - end: false");
                return false;
            }
            
            AbstractMessage message = msgFromNetwork.element();
            if ( message instanceof BaseCallResponse ) {
                BaseCallResponse response = (BaseCallResponse) message;
                if ( response.getRequestId().equals(currProcRequestInfo.getRequestId()) ) {
                    logger.debug("responseArrivedForLastRequest - end: true");
                    return true;
                }
            }
            
            logger.debug("responseArrivedForLastRequest - end: false");
            return false;
        }
        
        // Processes next message in the input queue from protocol layer.
        private void processNextIncommingMessage() {
            logger.debug("processNextIncommingMessage - start:");
            
            AbstractMessage message = msgFromNetwork.poll();
            logger.info("Processed message: {}", message);
            
            if ( message instanceof BaseCallResponse ) {
                BaseCallResponse response = (BaseCallResponse) message;
                if ( response.getRequestId().equals(currProcRequestInfo.getRequestId()) ) {
                    logger.info("Response found");
                    CallRequestProcessingInfo procInfo = createCallRequestProcessingInfo( response );
                    callResultsSender.addCallRequestProcessingInfo(procInfo);
                } else {
                    logger.warn("Response not matching to the last request. "
                            + "Response will be discarded."
                    );
                }     
            } else {
                logger.info("Non response type of message found: {}", message);
            }
            
            logger.debug("processNextIncommingMessage - end");
        }
        
        // processes all incomming messages
        private void processAllIncommingMessages() {
            while ( !msgFromNetwork.isEmpty() ) {
                processNextIncommingMessage();
            }
        }
        
        
        // variable variant for CallRequestProcessingInfo
        private class VariableCallRequestProcessingInfo {
            private UUID requestId = null;
            private CallRequestProcessingState state = null;
            private CallResult callResult = null;
            private CallRequestProcessingError error = null;

            
            public VariableCallRequestProcessingInfo() {
            }
            
            public VariableCallRequestProcessingInfo( UUID requestId,
                    CallRequestProcessingState state, CallResult callResult, 
                    CallRequestProcessingError error 
            ) {
                this.requestId = requestId;
                this.state = state;
                this.callResult = callResult;
                this.error = error;
            }

            public synchronized void setRequestId(UUID requestId) {
                this.requestId = requestId;
            }
            
            public UUID getRequestId() {
                return requestId;
            }
            
            public synchronized void setState(CallRequestProcessingState state) {
                this.state = state;
            }
            
            public synchronized void setCallResult(CallResult callResult) {
                this.callResult = callResult;
            }

            public synchronized void setError(CallRequestProcessingError error) {
                this.error = error;
            }
            
            public synchronized void setAll( UUID requestId,
                    CallRequestProcessingState state, CallResult callResult, 
                    CallRequestProcessingError error 
            ) {
                this.requestId = requestId;
                this.state = state;
                this.callResult = callResult;
                this.error = error;
            }
            
            public synchronized CallRequestProcessingInfo getCallRequestProcessingInfo() {
                return new CallRequestProcessingInfo(requestId, state, callResult, error);
            }
        }
        
        // info about currently processed request 
        private VariableCallRequestProcessingInfo currProcRequestInfo 
                = new VariableCallRequestProcessingInfo();
        private final Object syncCurrProcRequestInfo = new Object(); 
        
        
        // sets maximal processing time for specified request 
        public void setCallRequestProcessingTime(UUID reqId, long maxProcTime) {
            synchronized ( syncRequestsToProcess ) {
                for ( CallRequestToProcess reqToProc : requestsToProcess ) {
                    if ( reqToProc.callRequest.getId().equals(reqId) ) {
                        reqToProc.maxProcTime = maxProcTime;
                        return;
                    }
                }
            }
            
            synchronized ( syncIdleRequests ) {
                boolean found = false;
                Iterator<IdleRequest> requestIt = idleRequests.iterator();
                while ( requestIt.hasNext() ) {
                    IdleRequest idleRequest = requestIt.next();
                    long idleTime = System.currentTimeMillis() - idleRequest.startIdleTime;
                    if ( idleTime > callRequestMaxIdleTime ) {
                        requestIt.remove();
                        continue;
                    }
                    
                    if ( found ) {
                        continue;
                    }
                    
                    if ( idleRequest.reqToProc.callRequest.getId().equals(reqId) ) {
                        idleRequest.startIdleTime = System.currentTimeMillis();
                        idleRequest.reqToProc.maxProcTime = maxProcTime;
                        found = true;
                    }
                }
            }
        } 
        
        /**
         * Returns processing info of specified request or null, if no such 
         * request was found.
         */
        public CallRequestProcessingInfo getCallRequestProcessingInfo(UUID reqId) {
            // if the request is waiting in input requests queue
            synchronized( syncRequestsToProcess ) {
                for ( CallRequestToProcess reqToProcess : requestsToProcess ) {
                    if ( reqToProcess.callRequest.getId().equals(reqId) ) {
                        return new CallRequestProcessingInfo( reqId, WAITING_FOR_PROCESSING );
                    }
                }
            }
            
            synchronized ( syncCurrProcRequestInfo ) {
                if ( currProcRequestInfo.getRequestId().equals(reqId) ) {
                    return currProcRequestInfo.getCallRequestProcessingInfo();
                }
            }
            
            CallRequestProcessingInfo procInfo = null; 
            
            synchronized ( syncIdleRequests ) {
                boolean found = false;
                Iterator<IdleRequest> requestIt = idleRequests.iterator();
                while ( requestIt.hasNext() ) {
                    IdleRequest idleRequest = requestIt.next();
                    long idleTime = System.currentTimeMillis() - idleRequest.startIdleTime;
                    if ( idleTime > callRequestMaxIdleTime ) {
                        requestIt.remove();
                        continue;
                    }
                    
                    if ( found ) {
                        continue;
                    }
                    
                    if ( idleRequest.reqToProc.callRequest.getId().equals(reqId) ) {
                        procInfo = new CallRequestProcessingInfo(reqId, WAITING_FOR_PROCESSING );
                        found = true;
                    }
                }
            }
            
            // could be NULL
            return procInfo;
        }
        
        /**
         * Cancels processing of specified request.
         * @param reqId ID of request to cancelCallRequest
         */
        public void cancelCallRequest(UUID reqId) {
            // must be tied together because the last request is polled from 
            // requests to process
            synchronized ( syncCancelRequest ) {
                // cancel the last request
                synchronized ( syncMsgfromNetwork ) {
                    if ( lastRequestToProc.callRequest.getId().equals(reqId) ) {
                        isCancelledLastRequest = true;
                        syncMsgfromNetwork.notifyAll();
                        return;
                    }
                }
                
                // cancel request to process
                synchronized ( syncRequestsToProcess ) {
                    Iterator<CallRequestToProcess> requestIt = requestsToProcess.iterator();
                    while ( requestIt.hasNext() ) {
                        CallRequestToProcess reqToProc = requestIt.next();
                        if ( reqToProc.callRequest.getId().equals(reqId) ) {
                            requestIt.remove();
                        }
                    }
                }
            }
            
            synchronized ( syncIdleRequests ) {
                boolean found = false;
                Iterator<IdleRequest> requestIt = idleRequests.iterator();
                
                while ( requestIt.hasNext() ) {
                    IdleRequest idleRequest = requestIt.next();
                    long idleTime = System.currentTimeMillis() - idleRequest.startIdleTime;
                    if ( idleTime > callRequestMaxIdleTime ) {
                        requestIt.remove();
                        continue;
                    }
                    
                    if ( found ) {
                        continue;
                    }
                    
                    if ( idleRequest.reqToProc.callRequest.getId().equals(reqId) ) {
                        requestIt.remove();
                        found = true;
                    }
                }
            }
        }
        
        // removes idle requests, which exceeded max idle time period
        private void removeMaxTimeExceededIdleRequests() {
            synchronized ( syncIdleRequests ) {
                Iterator<IdleRequest> requestIt = idleRequests.iterator();
                while ( requestIt.hasNext() ) {
                    IdleRequest idleRequest = requestIt.next();
                    long idleTime = System.currentTimeMillis() - idleRequest.startIdleTime;
                    if ( idleTime > callRequestMaxIdleTime ) {
                        requestIt.remove();
                    }
                }
            }
        }
        
        @Override
        public void run() {
            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("Worker thread end");
                    return;
                }
                
                // nothing to do - so wait for messages
                synchronized ( syncRequestsToProcess ) {
                    while ( requestsToProcess.isEmpty() ) {
                        try {
                            syncRequestsToProcess.wait();
                        } catch ( InterruptedException e ) {
                            logger.warn(
                                "Worker thread interrupted while waiting on messages", e
                            );
                            return;
                        }
                    }
                }
                
                synchronized ( syncCancelRequest ) {
                    synchronized ( syncRequestsToProcess ) {
                        lastRequestToProc = requestsToProcess.poll();
                        isCancelledLastRequest = false;
                    }
                }
                
                currProcRequestInfo.setAll( lastRequestToProc.callRequest.getId(), 
                        WAITING_FOR_PROCESSING, null, null
                );
                
                boolean respArrivedForLastRequest = false;
                boolean requestSentOk = false;
                
                try {
                    sendRequestToProtocolLayer( lastRequestToProc.callRequest );
                    requestSentOk = true;
                } catch ( Exception ex ) {
                    // dispatching error
                    logger.error("Send request to protocol layer error: ", ex);
                    currProcRequestInfo.setAll( lastRequestToProc.callRequest.getId(), ERROR, 
                            null, new DispatchingRequestToProtocolLayerError(ex)
                    );
                    callResultsSender.addCallRequestProcessingInfo(
                            currProcRequestInfo.getCallRequestProcessingInfo() 
                    );
                } 
                
                if ( !requestSentOk ) {
                    removeMaxTimeExceededIdleRequests();
                    continue;
                }
                
                currProcRequestInfo.setState( WAITING_FOR_RESULT );
                synchronized ( syncMsgfromNetwork ) {
                    // while cycle is needed because other responses may
                    // arrive to connector
                    long timeToWait = lastRequestToProc.maxProcTime;
                    respArrivedForLastRequest = responseArrivedForLastRequest();
                    
                    while ( !respArrivedForLastRequest && !isCancelledLastRequest ) {
                        try {
                            if ( lastRequestToProc.maxProcTime == UNLIMITED_MAXIMAL_PROCESSING_TIME ) {
                                syncMsgfromNetwork.wait();
                            } else {
                                long startTime = System.nanoTime();
                                syncMsgfromNetwork.wait( timeToWait );
                                double timeElapsed = (System.nanoTime() - startTime) * NANOSEC_TO_MILISEC;

                                if ( timeElapsed >= timeToWait ) {
                                    break;
                                }
                                
                                timeToWait -= timeElapsed; 
                            }
                            respArrivedForLastRequest = responseArrivedForLastRequest();
                        } catch ( InterruptedException e ) {
                            logger.warn(
                                    "Worker thread interrupted while waiting on response", e
                            );
                            return;
                        }
                    }
                }

                // process arrived messages
                if ( !msgFromNetwork.isEmpty() ) {
                    processAllIncommingMessages();
                    if ( !respArrivedForLastRequest ) {
                        // there wasn't a response for a last request
                        logger.warn("No messages arrived for the last request.");
                        if ( !isCancelledLastRequest ) {
                            idleRequests.add( new IdleRequest(lastRequestToProc, System.currentTimeMillis()) );
                        }
                    }
                } else {
                    // no messages arrived in the timeout
                    if ( !isCancelledLastRequest ) {
                        idleRequests.add( new IdleRequest(lastRequestToProc, System.currentTimeMillis()) );
                    }
                    logger.warn("No messages arrived at timeout");
                }
                
                // removes idle requests, which exceeded max idle time period
                removeMaxTimeExceededIdleRequests();
            }
        }
    }
    
    
    /** 
     * Worker thread: DO to underlaying network and underlaying network to 
     * connector directions. 
     */
    private WorkerThread workerThread = null;
    
    /** 
     * Queue of incomming call requests to process.
     */
    private Queue<CallRequestToProcess> requestsToProcess = new ConcurrentLinkedQueue<>();
    
    /**
     * Synchronization object for {@code requestsToProcess}. 
     */
    private final Object syncRequestsToProcess = new Object();
    
    
    /**
     * Idle requests.
     */
    private Queue<IdleRequest> idleRequests = new ConcurrentLinkedQueue<>();
    
    /**
     * Synchronization object for {@code idleRequests}. 
     */
    private final Object syncIdleRequests = new Object();
    
    
    
    /** 
     * Queue of messages received from protocol layer.  
     */  
    private Queue<AbstractMessage> msgFromNetwork = new ConcurrentLinkedQueue<>(); 
    
    /**
     * Synchronization object for messages incomming from protocol layer.
     */
    private final Object syncMsgfromNetwork = new Object();
    
    
    private static long MAX_CALL_REQUEST_IDLE_TIME_DEFAULT = 30000;
    private volatile long callRequestMaxIdleTime = MAX_CALL_REQUEST_IDLE_TIME_DEFAULT;
    
    
    /**
     * Default pause between subsequent attempts to send request to underlaying
     * network [in miliseconds].
     */
    private static long ATTEMPT_PAUSE_DEFAULT = 1000;
    
    /** Default minimal pause between sending requests [in miliseconds]. */
    private static long BETWEEN_SEND_PAUSE_DEFAULT = 1000;
    
    /**
     * Default number of maximal attempts of sending request to underlaying
     * network.
     */
    private static int MAX_SEND_ATTEMPTS_DEFAULT = 3;
    
    /**
     * Default timeout for waiting for a response from underlaying network
     * [in miliseconds].
     */
    private static long RESP_TIMEOUT_DEFAULT = 25000;
    
    
    /** Number of maximal attempts of sending request to underlaying network. */
    private volatile int maxSendAttempts = MAX_SEND_ATTEMPTS_DEFAULT;
    
    /** 
     * Pause between subsequent attempts to send request to underlaying network
     * [in miliseconds]. 
     */
    private volatile long attemptPause = ATTEMPT_PAUSE_DEFAULT;
    
    /** 
     * Timeout for waiting for a response from underlaying network 
     * [in miliseconds]. 
     */
    private volatile long responseTimeout = RESP_TIMEOUT_DEFAULT;
    
    /** Minimal pause between sending requests [in miliseconds]. */
    private volatile long betweenSendPause = BETWEEN_SEND_PAUSE_DEFAULT;
    
    
    private static ConnectedDeviceObject checkDeviceObject(ConnectedDeviceObject deviceObject) {
        if ( deviceObject == null ) {
            throw new IllegalArgumentException("Device Object cannot be null");
        }
        return deviceObject;
    }
    
    private static Class checkDeviceInterface(Class deviceIface) {
        if ( deviceIface == null ) {
            throw new IllegalArgumentException("Device Interface cannot be null");
        }
        return deviceIface;
    }
    
    private static String checkMethodId(String methodId) {
        if ( methodId == null ) {
            throw new IllegalArgumentException("Method ID cannot be null");
        }
        return methodId;
    }
    
    private static long checkMaxProcessingTime( long maxProcTime ) {
        if ( maxProcTime == UNLIMITED_MAXIMAL_PROCESSING_TIME ) {
            return maxProcTime;
        }
        
        if ( maxProcTime <= 0 ) {
            throw new IllegalArgumentException(
                "Maximal processing time must be positive or equal to " + UNLIMITED_MAXIMAL_PROCESSING_TIME
            );
        }
        
        return maxProcTime;
    }
    
    private static UUID checkRequestId(UUID requestId) {
        if ( requestId == null ) {
            throw new IllegalArgumentException("Call request ID cannot be null");
        }
        return requestId;
    }
    
    
    
    /**
     * Creates new response-waiting connector.
     * @param protocolLayerService protocol layer service to use
     * @throws IllegalArgumentException if {@code protocolLayerService} is {@code null}
     */
    public SimpleResponseWaitingConnector(ProtocolLayerService protocolLayerService) {
       super( protocolLayerService );
       callResultsSender = new CallResultsSender();
       workerThread = new WorkerThread();
    }
    
    /**
     * @throws IllegalArgumentException if {@code timeout} is less than or 
     *         equal to 0
     */
    @Override
    public UUID callMethod( ConnectedDeviceObject devObject, Class deviceIface, 
            String methodId, Object[] args, long maxProcTime
    ) {
        logger.debug(
                "callMethod - start: devObject={}, devIface={}, methodId={}, "
                + "args={}, timeout={}", 
                devObject, deviceIface, methodId, args, maxProcTime
        );
        
        checkDeviceObject(devObject);
        checkDeviceInterface(deviceIface);
        checkMethodId( methodId );
        checkMaxProcessingTime( maxProcTime );
        
        UUID callId = UUID.randomUUID();
        CallRequest request = new CallRequest(
                callId, devObject.getNetworkId(), devObject.getNodeId(), deviceIface, 
                methodId, args
        );
        
        // associate request ID with addressee of its result
        callResultsSender.associateCallRequestWithAddressee(callId, devObject);
        
        CallRequestToProcess requestToProcess = new CallRequestToProcess(request, maxProcTime);
        synchronized( syncRequestsToProcess ) {
            requestsToProcess.offer( requestToProcess );
            syncRequestsToProcess.notify();
        }
        
        logger.info("New call request created: {}", request);
        logger.debug("callMethod - end: {}", callId);
        return callId;
    }
    
    @Override
    public UUID callMethod(ConnectedDeviceObject deviceObject, Class deviceIface, 
            String methodId, Object[] args
    ) {
        return callMethod(deviceObject, deviceIface, methodId, args, responseTimeout);
    }
    
    @Override
    public void setCallRequestMaximalProcessingTime(UUID requestId, long maxProcTime) {
        workerThread.setCallRequestProcessingTime(
                checkRequestId(requestId), checkMaxProcessingTime(maxProcTime)
        );
    }
    
    @Override
    public CallRequestProcessingInfo getCallRequestProcessingInfo(UUID requestId) {
        CallRequestProcessingInfo procInfo 
                = workerThread.getCallRequestProcessingInfo(checkRequestId(requestId));
        if ( procInfo != null ) {
            return procInfo;
        }
        
        // if procInfo == null, then workerThread hasn't any information about
        // specified request - so it is neccessary to query listener thread
        procInfo = callResultsSender.getCallRequestProcessingInfo(requestId);
        return procInfo;
    }

    @Override
    public void cancelCallRequest(UUID requestId) {
        workerThread.cancelCallRequest(checkRequestId(requestId));
    }
    
    @Override
    public void start() throws SimplyException {
        logger.debug("startMessaging - start:");
        
        callResultsSender.start();
        workerThread.start();
        
        // register this connector as a listener of messages from protocol layer 
        this.protocolLayerService.registerListener(this);
        
        logger.info("Messaging started");
        logger.debug("startMessaging - end");
    }
    
    // timeout to wait for worker threads to join
    private static final long JOIN_WAIT_TIMEOUT = 2000;
    
    /**
     * Terminates worker thread.
     */
    private void terminateWorkerThread() {
        logger.debug("stopMessaging - start:");
        
        // termination signal to worker thread
        workerThread.interrupt();
        
        // indicates, wheather this thread is interrupted
        boolean isInterrupted = false;
         
        try {
            if ( workerThread.isAlive() ) {
                workerThread.join(JOIN_WAIT_TIMEOUT);
            }
        } catch ( InterruptedException e ) {
            isInterrupted = true;
            logger.warn("Stop messaging - connector interrupted");
        }
        
        if ( !workerThread.isAlive() ) {
            logger.info("Worker thread stopped.");
        }
        
        if ( isInterrupted ) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Messaging stopped.");
        logger.debug("stopMessaging - end");
    }
    
    /**
     * Destroys this connector and frees all used resources.
     */
    @Override
    public void destroy() {
        logger.debug("destroy - start:");
        
        protocolLayerService.unregisterListener();
        terminateWorkerThread();
        callResultsSender.destroy();
        protocolLayerService = null;
        
        logger.info("Destroyed.");
        logger.debug("destroy - end");
    }
    
    @Override
    public void onGetMessage(AbstractMessage message) {
        logger.debug("onGetMessage - start: data={}", message);
        
        synchronized( syncMsgfromNetwork ) {
            msgFromNetwork.offer(message);
            syncMsgfromNetwork.notify();
        }
        
        logger.info("New message from protocol layzer get: {}", message);
        logger.debug("onGetMessage - end");
     }
    
    
    private static long checkCallRequestMaximalIdleTime(long idleTime) {
        if ( idleTime < 0 ) {
            throw new IllegalArgumentException(
                    "Call request maximal idle time must be nonnegative"
            );
        }
        return idleTime;
    }
    
    /**
     * @param idleTime maximal time period, during which requests can be idle
     * @throws IllegalArgumentException if {@code idleTime} is less than or 
     *         equal to 0
     */
    @Override
    public void setCallRequestsMaximalIdleTime(long idleTime) {
        callRequestMaxIdleTime = checkCallRequestMaximalIdleTime(idleTime);
    }
    
    @Override
    public long getCallRequestsMaximalIdleTime() {
        return callRequestMaxIdleTime;
    }
    
    
    /**
     * Returns number of maximal attempts of sending request to underlaying 
     * network.
     * @return number of maximal attempts of sending request to underlaying network.
     */
    @Override
    public int getMaxSendAttempts() {
        return maxSendAttempts;
    }
    
    
    private static int checkMaxSendAttempts(int maxSendAttempts) {
        if ( maxSendAttempts <= 0 ) {
            throw new IllegalArgumentException(
                    "Number of maximal attempts of sending request to underlaying "
                            + "network must be greather then 0"
            );
        }
        return maxSendAttempts;
    }
    
    /**
     * Sets number of maximal attempts of sending request to underlaying network.
     * @param maxSendAttempts number of maximal attempts of sending request to 
     *                        underlaying network. Must be greater than 0.
     * @throws IllegalArgumentException if {@code maxSendAttempts} is less than or 
     *         equal to 0
     */
    @Override
    public void setMaxSendAttempts(int maxSendAttempts) {
        this.maxSendAttempts = checkMaxSendAttempts(maxSendAttempts);
    }

    /**
     * Returns pause between subsequent attempts to send request to underlaying 
     * network.
     * @return Pause between subsequent attempts to send request to underlaying 
     *         network [in miliseconds].
     */
    @Override
    public long getAttemptPause() {
        return attemptPause;
    }

    
    private static long checkAttemptPause(long attemptPause) {
        if ( attemptPause <= 0 ) {
            throw new IllegalArgumentException(
                    "Pause between subsequent attempts to send request to underlaying "
                            + "network must be greather then 0"
            );
        }
        return attemptPause;
    }
    
    /**
     * Sets pause between subsequent attempts to send request to underlaying 
     * network.
     * @param attemptPause pause between subsequent attempts to send request to 
     *                underlaying network [in miliseconds]. Must be greater than 0.
     * @throws IllegalArgumentException if {@code attemptPause} is less than or 
     *         equal to 0
     */
    @Override
    public void setAttemptPause(long attemptPause) {
        this.attemptPause = checkAttemptPause(attemptPause);
    }

    /**
     * Returns timeout for waiting for a response from underlaying network.
     * @return timeout for waiting for a response from underlaying network 
     *         [in miliseconds].
     */
    @Override
    public long getResponseTimeout() {
        return responseTimeout;
    }
    
    private static long checkResponseTimeout(long responseTimeout) {
        if ( responseTimeout <= 0 ) {
            throw new IllegalArgumentException("Reponse timeout must be greather then 0");
        }
        return responseTimeout;
    }
    
    /**
     * Sets timeout for waiting for a response from underlaying network.
     * @param responseTimeout timeout for waiting for a response from underlaying 
     *                        network [in miliseconds]. Must be greater than 0
     * @throws IllegalArgumentException if {@code responseTimeout} is less than or 
     *         equal to 0
     */
    @Override
    public void setResponseTimeout(long responseTimeout) {
        this.responseTimeout = checkResponseTimeout(responseTimeout);
    }
    
    /**
     * Returns minimal pause between sending requests.
     * @return Minimal pause between sending requests [in miliseconds].
     */
    @Override
    public long getBetweenSendPause() {
        return betweenSendPause;
    }
    
    
    private static long checkBetweenSendPause(long betweenSendPause) {
        if ( betweenSendPause <= 0 ) {
            throw new IllegalArgumentException(
                    "Minimal pause between sending requests to underlaying "
                            + "network must be greather then 0"
            );
        }
        return betweenSendPause;
    }
    
    /**
     * Sets minimal pause between sending requests.
     * @param betweenSendPause minimal pause between sending requests [in miliseconds].
     *        Must be greater than 0.
     * @throws IllegalArgumentException if {@code betweenSendPause} is less than or 
     *         equal to 0
     */
    @Override
    public void setBetweenSendPause(long betweenSendPause) {
        this.betweenSendPause = checkBetweenSendPause(betweenSendPause);
    }
}
