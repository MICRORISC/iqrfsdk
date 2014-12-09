/*
 * Copyright 2014 MICRORISC s.r.o..
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

import com.microrisc.simply.CallRequest;
import com.microrisc.simply.ManageableObject;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastRequest;
import com.microrisc.simply.iqrf.dpa.v210.types.DPA_Confirmation;
import java.util.Arrays;
import org.slf4j.LoggerFactory;

/**
 * State machine for better handling individual states within the process of 
 * DPA protocol's message exchange. 
 * 
 * @author Michal Konopa
 */
public final class ProtocolStateMachine implements ManageableObject {
    /** Logger. */
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProtocolStateMachine.class);

    
    /**
     * States of the machine.
     */
    public static enum State {
        FREE_FOR_SEND,
        WAITING_FOR_CONFIRMATION,
        WAITING_FOR_CONFIRMATION_ERROR,
        WAITING_AFTER_CONFIRMATION,
        WAITING_FOR_RESPONSE,
        WAITING_FOR_RESPONSE_ERROR,
        WAITING_AFTER_RESPONSE
    } 
    
    // actual state
    private State actualState = State.FREE_FOR_SEND;
    
    
    /** Default time to wait for confirmation [ in ms ]. */
    public static final long TIME_TO_WAIT_FOR_CONFIRMATION_DEFAULT = 2000;
    
    // actual time to wait for confirmation
    private long timeToWaitForConfirmation = TIME_TO_WAIT_FOR_CONFIRMATION_DEFAULT;
    
    
    /** Default base time to wait for response [ in ms ]. */
    public static final long BASE_TIME_TO_WAIT_FOR_RESPONSE_DEFAULT = 2000;
    
    // actual base time to wait for response
    private long baseTimeToWaitForResponse = BASE_TIME_TO_WAIT_FOR_RESPONSE_DEFAULT;
    
    
    // object for synchronized access to entities
    private final Object synchroObjects = new Object();
    
    private final Object synchroWaiting = new Object();
    
    
    // counts timeslot length in 10 ms units
    private static long countTimeslotLength(int responseDataLength) {
        if ( responseDataLength < 19 ) {
            return 8;
        }
        if ( responseDataLength < 41 ) {
            return 9;
        }
        return 10;
    }
    
    private long countWaitingTimeForConfirmation() {
        return timeToWaitForConfirmation;
    }
    
    private long countWaitingTimeForResponse() {
        long requestRoutingTime = 0;
        if ( countWithConfirmation ) {
            requestRoutingTime = (confirmation.getHops() + 1) * confirmation.getTimeslotLength() * 10;
            return baseTimeToWaitForResponse + requestRoutingTime + 100;
        }
        
        return baseTimeToWaitForResponse + 100;
    }
    
    private long countWaitingTimeAfterResponse() {
        long actualRespTimeslotLength = countTimeslotLength(responseDataLength);
        
        if ( countWithConfirmation ) {
            if ( confirmation == null ) {
                throw new IllegalStateException(
                        "Confirmation needed for calculation of waiting time after response "
                                + "but not present."
                );
            }
            return ( confirmation.getHops() + 1 ) * confirmation.getTimeslotLength() * 10
                + ( confirmation.getHopsResponse() + 1 ) * actualRespTimeslotLength  * 10
                - (System.currentTimeMillis() - responseRecvTime);
        }
        
        return ( actualRespTimeslotLength * 10 ) - (System.currentTimeMillis() - responseRecvTime);
    }
    
    private long countWaitingTimeAfterConfirmation() {
        return ( confirmation.getHops() + 1 ) * confirmation.getTimeslotLength() * 10
                - (System.currentTimeMillis() - responseRecvTime);
    }
    
    private long countWaitingTime(ProtocolStateMachine.State state) {
        long waitingTime = 0;
        switch ( state ) {
            case FREE_FOR_SEND:
                waitingTime = 0;
                break;
            case WAITING_FOR_CONFIRMATION:
                waitingTime = countWaitingTimeForConfirmation();
                break;
            case WAITING_FOR_RESPONSE:
                waitingTime = countWaitingTimeForResponse();
                break;
            case WAITING_AFTER_CONFIRMATION:
                waitingTime = countWaitingTimeAfterConfirmation();
                break;
            case WAITING_AFTER_RESPONSE:
                waitingTime = countWaitingTimeAfterResponse();
                break;
            default:
                throw new IllegalStateException("Incorrect state to start waiting from: " + state);
        }

        if ( waitingTime < 0 ) {
            waitingTime = 0;
        }
        
        return waitingTime;
    }
    
    
    private class WaitingTimeCounter extends Thread {
        
        private void doWaitForConfirmation(long waitingTime) {
            logger.info("Time to wait for confirmation: {}", waitingTime);
            synchronized ( synchroWaiting ) {
                try {
                    synchroWaiting.wait(waitingTime);
                } catch ( InterruptedException ex ) {
                    logger.warn(
                        "Waiting time counter interrupted while waiting on confirmation", ex
                    );
                    return;
                }
            }

            synchronized ( synchroObjects ) {
                // test if confirmation has come in
                if ( actualState != ProtocolStateMachine.State.WAITING_FOR_RESPONSE ) {
                    actualState = ProtocolStateMachine.State.WAITING_FOR_CONFIRMATION_ERROR;
                    if ( listener != null ) {
                        listener.onConfirmationTimeouted();
                    }
                }
            }
        }
        
        private void doWaitForResponse(long waitingTime) {
            logger.info("Time to wait for response: {}", waitingTime);
            synchronized ( synchroWaiting ) {
                try {
                    synchroWaiting.wait(waitingTime);
                } catch ( InterruptedException ex ) {
                    logger.warn(
                        "Waiting time counter interrupted while waiting on confirmation", ex
                    );
                    return;
                }
            }

            synchronized ( synchroObjects ) {
                // test if response has come in
                if ( actualState != ProtocolStateMachine.State.WAITING_AFTER_RESPONSE ) {
                    actualState = ProtocolStateMachine.State.WAITING_FOR_RESPONSE_ERROR;
                    if ( listener != null ) {
                        listener.onResponseTimeouted();
                    }
                }
            }
        }
        
        // waiting after confirmation or response arrival
        private void doWaitAfter(long waitingTime) {
            logger.info("Time to wait for sending new request: {}", waitingTime);
                
            try {
                Thread.sleep(waitingTime);
            } catch ( InterruptedException ex ) {
                logger.warn(
                    "Waiting time counter interrupted while waiting on routing end", ex
                );
                return;
            }

            // updating actual state and listener notification
            synchronized ( synchroObjects ) {
                actualState = ProtocolStateMachine.State.FREE_FOR_SEND;
                if ( listener != null ) {
                    listener.onFreeForSend();
                }
            }

            logger.info("Free for send");
        }
        
        private void doWait( ProtocolStateMachine.State state, long waitingTime ) {
            switch ( state ) {
                case WAITING_FOR_CONFIRMATION:
                    doWaitForConfirmation(waitingTime);
                    break;
                case WAITING_FOR_RESPONSE:
                    doWaitForResponse(waitingTime);
                    break;
                case WAITING_AFTER_CONFIRMATION:
                case WAITING_AFTER_RESPONSE:
                    doWaitAfter(waitingTime);
                    break;
                default:
                    throw new IllegalStateException("Incorrect state to wait in: " + state);
            }
        }
        
        @Override
        public void run() {
            // it is needed because of automatic execution of next iteration
            // without waiting for outside signal in some Macine states
            boolean notToWait = false;
            
            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("Waiting time counter end");
                    return;
                }
                
                // waiting for signal for waiting
                synchronized ( synchroWaiting ) {
                    if ( !notToWait ) { 
                        try {
                            synchroWaiting.wait();
                        } catch ( InterruptedException ex ) {
                            logger.warn("Waiting time counter interrupted while waiting", ex);
                            return;
                        }
                    }
                    
                    // for next iteration
                    notToWait = false;
                    
                    ProtocolStateMachine.State tempState = null;
                    long waitingTime = 0;
                    
                    synchronized ( synchroObjects ) {
                        tempState = actualState;
                        waitingTime = countWaitingTime(tempState);
                        
                        // countWithConfirmation already used
                        if ( actualState == ProtocolStateMachine.State.WAITING_AFTER_RESPONSE ) {
                            countWithConfirmation = false;
                        }
                    }
                    
                    // waiting
                    doWait( tempState, waitingTime );
                    
                    synchronized ( synchroObjects ) {
                        if ( actualState == ProtocolStateMachine.State.WAITING_AFTER_CONFIRMATION
                            || actualState == ProtocolStateMachine.State.WAITING_AFTER_RESPONSE
                        ) {
                            notToWait = true;
                        }
                    }
                }
            }
        }
    }
    
    /** Waiting time counter thread. */
    private Thread waitingTimeCounter = null;
    
    // timeout to wait for worker threads to join
    private static final long JOIN_WAIT_TIMEOUT = 2000;
    
    /**
     * Terminates waiting time counter thread.
     */
    private void terminateWaitingTimeCounter() {
        logger.debug("terminateWaitingTimeCounter - start:");
        
        // termination signal
        waitingTimeCounter.interrupt();
        
        // indicates, wheather this thread is interrupted
        boolean isInterrupted = false;
        
        try {
            if ( waitingTimeCounter.isAlive( )) {
                waitingTimeCounter.join(JOIN_WAIT_TIMEOUT);
            }
        } catch ( InterruptedException e ) {
            isInterrupted = true;
            logger.warn("waiting time counter terminating - thread interrupted");
        }
        
        if ( !waitingTimeCounter.isAlive() ) {
            logger.info("Waiting time counter stopped.");
        }
        
        if ( isInterrupted ) {
            Thread.currentThread().interrupt();
        }
        
        logger.debug("terminateWaitingTimeCounter - end");
    }
    
    
    // time of reception of a confirmation
    private long confirmRecvTime = -1;
    
    // received confirmation
    private DPA_Confirmation confirmation = null;
    
    // indicates, wheather to count with confirmation in calculation of 
    // waiting time
    private boolean countWithConfirmation = false;
    
    // time of reception of a response
    private long responseRecvTime = -1;
    
    // response data
    private int responseDataLength = -1;
    
    // listener
    private ProtocolStateMachineListener listener = null;
    
    private boolean isRequestForCoordinator(CallRequest request) {
        return request.getNodeId().equals("0");
    }
    
    private static long checkTimeToWaitForConfirmation(long time) {
        if ( time < 0 ) {
            throw new IllegalArgumentException(
                    "Time to wait for confirmation cannot be less then 0"
            );
        }
        return time;
    }
    
    private static long checkBaseTimeToWaitForResponse(long time) {
        if ( time < 0 ) {
            throw new IllegalArgumentException(
                    "Base time to wait for response cannot be less then 0"
            );
        }
        return time;
    }
    
    
    public ProtocolStateMachine() {
        waitingTimeCounter = new WaitingTimeCounter();
        logger.info("Protocol machine successfully created.");
    }
    
    /**
     * Returns actual value of time to wait for confirmation arrival [ in ms ].
     * @return actual value of time to wait for confirmation arrival
     */
    public long getTimeToWaitForConfirmation() {
        synchronized ( synchroObjects ) {
            return timeToWaitForConfirmation;
        }
    }
    
    /**
     * Sets time to wait for confirmation arrival.
     * @param time new value of time [ in ms ] to wait for confirmation, cannot be less then 0
     * @throws IllegalArgumentException if specified time is less then 0
     */
    public void setTimeToWaitForConfirmation(long time) {
        synchronized ( synchroObjects ) {
            this.timeToWaitForConfirmation = checkTimeToWaitForConfirmation(time);
        }
    }
    
    /**
     * Returns actual value of base time to wait for response arrival [ in ms ].
     * @return actual value of base time to wait for response arrival
     */
    public long getBaseTimeToWaitForResponse() {
        synchronized ( synchroObjects ) {
            return baseTimeToWaitForResponse;
        }
    }
    
    /**
     * Sets base time to wait for response arrival.
     * @param time new value of base time [ in ms ] to wait for response, cannot be less then 0
     * @throws IllegalArgumentException if specified time is less then 0
     */
    public void setBaseTimeToWaitForResponse(long time) {
        synchronized ( synchroObjects ) {
            this.baseTimeToWaitForResponse = checkBaseTimeToWaitForResponse(time);
        }
    }
    
    
    @Override
    public void start() throws SimplyException {
        logger.debug("start - start:");
        
        waitingTimeCounter.start();
        
        logger.info("Protocol Machine started");
        logger.debug("start - end");
    }
    
    /**
     * Registers specified listener.
     * @param listener listener to register
     */
    public void registerListener(ProtocolStateMachineListener listener) {
        logger.debug("registerListener - start: listener={}", listener);
        
        synchronized ( synchroObjects ) {
            this.listener = listener;
        }
        
        logger.info("Listener registered.");
        logger.debug("registerListener - end");
    } 
    
    /**
     * Unregister previously registered listener.
     * Does nothing, if there is no registered listener.
     */
    public void unregisterListener() {
        logger.debug("unregisterListener - start: ");
        
        synchronized ( synchroObjects ) {
            this.listener = null;
        }
        
        logger.info("Listener unregistered.");
        logger.debug("unregisterListener - end");
    }
    
    /**
     * Returns the actual state of the machine.
     * @return the actual state of the machine
     */
    public State getActualState() {
        logger.debug("getActualState - start: ");
        
        State state = null;
        synchronized ( synchroObjects ) {
            state = actualState;
        }
        
        logger.debug("getActualState - end: {}", state);
        return state;
    }
    
    /**
     * Indicates, wheather it is possible to send next request.
     * @return {@code true} if it is possible to send next request
     *         {@code false} otherwise
     */
    public boolean isFreeForSend() {
        logger.debug("isFreeForSend - start: ");
        
        boolean isFreeForSend = false;
        synchronized ( synchroObjects ) {
            isFreeForSend = ( actualState == State.FREE_FOR_SEND ); 
        }
        
        logger.debug("isFreeForSend - end: {}", isFreeForSend);
        return isFreeForSend;
    }
    
    /**
     * Informs the machine, that new request has been sent.
     * @param request sent request
     */
    public void newRequest(CallRequest request) {
        logger.debug("newRequest - start: request={}", request);
        
        synchronized ( synchroObjects ) {
            if ( actualState != State.FREE_FOR_SEND ) {
                throw new IllegalArgumentException(
                    "Cannot send new request because in the " + actualState + " state."
                );
            }

            if ( request instanceof BroadcastRequest ) {
                actualState = State.WAITING_FOR_CONFIRMATION;
                return;
            }

            if ( isRequestForCoordinator(request) ) {
                actualState = State.WAITING_FOR_RESPONSE;
                countWithConfirmation = false;
            } else {
                actualState = State.WAITING_FOR_CONFIRMATION;
                confirmation = null;
                countWithConfirmation = true;
            }
        }
        
        synchronized ( synchroWaiting ) {
            synchroWaiting.notifyAll();
        }
        
        logger.debug("newRequest - end");
    }
    
    /**
     * Informs the machine, that confirmation has been received.
     * @param recvTime time of confirmation reception
     * @param confirmation received confirmation
     */
    public void confirmationReceived(long recvTime, DPA_Confirmation confirmation) {
        logger.debug("confirmationReceived - start: recvTime={}, confirmation={}",
                recvTime, confirmation
        );
        
        synchronized ( synchroObjects ) { 
            if ( actualState != State.WAITING_FOR_CONFIRMATION ) {
                throw new IllegalArgumentException(
                    "Unexpected reception of confirmation. Actual state: " + actualState
                );
            }

            // broadcast - no response
            if ( confirmation.getHopsResponse() == 0 ) {
                actualState = State.WAITING_AFTER_CONFIRMATION;
            } else {
                actualState = State.WAITING_FOR_RESPONSE;
            }
            
            this.confirmation = confirmation;
            this.confirmRecvTime = recvTime;
        }
        
        synchronized ( synchroWaiting ) {
            synchroWaiting.notifyAll();
        }
        
        logger.debug("confirmationReceived - end");
    }
    
    /**
     * Informs the machine, that confirmation has been received. Time of calling 
     * of this method will be used as the time of the confirmation reception.
     * @param confirmation received confirmation
     */
    public void confirmationReceived(DPA_Confirmation confirmation) {
        confirmationReceived(System.currentTimeMillis(), confirmation);
    }
    
    /**
     * Informs the machine, that response has been received.
     * @param recvTime time of response reception
     * @param responseData data of the received response
     */
    public void responseReceived(long recvTime, short[] responseData) {
        logger.debug("responseReceived - start: recvTime={}, responseData={}",
                recvTime, Arrays.toString(responseData)
        );
        
        synchronized ( synchroObjects ) {
            if ( actualState != State.WAITING_FOR_RESPONSE ) {
                throw new IllegalArgumentException(
                    "Unexpected reception of the response. Actual state: " + actualState
                );
            }
            
            this.responseRecvTime = recvTime;
            this.responseDataLength = responseData.length;
            this.actualState = State.WAITING_AFTER_RESPONSE;
        }
        
        synchronized ( synchroWaiting ) {
            synchroWaiting.notifyAll();
        }
        
        logger.debug("responseReceived - end");
    }
    
    /**
     * Informs the machine, that response has been received. Time of calling of
     * this method will be used as the time of the response reception.
     * @param responseData data of the received response
     */
    public void responseReceived(short[] responseData) {
        responseReceived(System.currentTimeMillis(), responseData);
    }
    
    /**
     * Reseting the machine. 
     * Its main usage is to get the machine out of one of the error states.
     */
    public void reset() {
        logger.debug("reset - start:");
        
        synchronized ( synchroObjects ) {
            this.actualState = State.FREE_FOR_SEND;
            
            this.confirmation = null;
            this.confirmRecvTime = -1;
            this.countWithConfirmation = false;
            
            this.responseRecvTime = -1;
            this.responseDataLength = -1;
        }
        
        logger.info("Reseted.");
        logger.debug("reset - end");
    }
    
    @Override
    public void destroy() {
        logger.debug("destroy - start:");
        
        terminateWaitingTimeCounter();
        
        logger.info("Destroyed.");
        logger.debug("destroy - end");
    }
}
