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

package com.microrisc.simply.iqrf.dpa.v22x.protocol;

import com.microrisc.simply.CallRequest;
import com.microrisc.simply.ManageableObject;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastRequest;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Confirmation;
import com.microrisc.simply.iqrf.RF_Mode;
import com.microrisc.simply.iqrf.dpa.v22x.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.FRCStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.init.DeterminetedNetworkConfig;
import com.microrisc.simply.iqrf.dpa.v22x.protocol.timing.FRC_TimingParams;
import com.microrisc.simply.iqrf.dpa.v22x.protocol.timing.TimingParams;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Command;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Configuration;
import com.microrisc.simply.iqrf.dpa.v22x.types.OsInfo.TR_Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 * State machine for better handling of individual states within the process of 
 * DPA protocol's message exchange. 
 * 
 * @author Michal Konopa
 * @author Martin Strouhal
 */
//JULY-2015 - fixed timeslots for new modules (TR7x)
//JUNE-2015 - improved determing and using RF mode
final class ProtocolStateMachine implements ManageableObject {
    /** Logger. */
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProtocolStateMachine.class);
    
    
    // currently used RF mode
    private RF_Mode rfMode = RF_Mode.STD;

    //currently used TR type series
    private TR_Type.TR_TypeSeries trTypeSeries = TR_Type.TR_TypeSeries.UNKNOWN;
    
    //map of configuration for specific networks
    private Map<String, DeterminetedNetworkConfig> networkConfigMap;
    
    /**
     * Events, which occur during DPA protocol running, 
     * e.g confirmation arrival, response arrival etc. 
     */
    private static class Event {}
    
    private static class NewRequestEvent extends Event {
        CallRequest request;
        TimingParams timingParams;
        boolean countWithConfirmation = false;
        
        NewRequestEvent( CallRequest request, TimingParams timingParams) {
            this.request = request;
            this.timingParams = timingParams;
        }
    }
    
    private static class ConfirmationReceivedEvent extends Event {
        long recvTime;
        DPA_Confirmation confirmation;
        
        ConfirmationReceivedEvent(long recvTime, DPA_Confirmation confirmation) {
            this.recvTime = recvTime;
            this.confirmation = confirmation;
        }
    }
    
    private static class ResponseReceivedEvent extends Event {
        long recvTime;
        short[] responseData;

        ResponseReceivedEvent(long recvTime, short[] responseData) {
            this.recvTime = recvTime;
            this.responseData = responseData;
        }
    }
    
    private static class ResetEvent extends Event {}
    
    // new event
    private Event newEvent = null;
    
    // synchronization object for access to newEvent
    private final Object synchroNewEvent = new Object();
    
    
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
    
    // synchronization object for actualState
    private final Object synchroActualState = new Object();
    
    
    // indication, that state has been changed  
    private volatile boolean stateChanged = false;
    
    // signal of state change
    private final Object stateChangeSignal = new Object();
    
    // event triggered state change timeout [in ms]
    private static final long STATE_CHANGE_TIMEOUT = 1000;
    
    // waits for state change
    private void waitForStateChangeSignal() {
        synchronized ( stateChangeSignal ) {
            while ( !stateChanged ) {
                try {
                    long startTime = System.currentTimeMillis();
                    stateChangeSignal.wait(STATE_CHANGE_TIMEOUT);
                    //logger.info("waitForStateChangeSignal - after waiting.");
                    long endTime = System.currentTimeMillis();
                    if ( (endTime - startTime) >= STATE_CHANGE_TIMEOUT ) {
                        throw new IllegalStateException ("Waiting for state timeouted.");
                    }
                } catch ( InterruptedException ex ) {
                    logger.warn("Waiting for next exptected state interrupted.");
                }
            }
        }
    }
    
    
    /** Default time to wait for confirmation [ in ms ]. */
    public static final long TIME_TO_WAIT_FOR_CONFIRMATION_DEFAULT = 2000;
    
    // actual time to wait for confirmation
    private volatile long timeToWaitForConfirmation = TIME_TO_WAIT_FOR_CONFIRMATION_DEFAULT;
    
    
    /** Default base time to wait for response [ in ms ]. */
    public static final long BASE_TIME_TO_WAIT_FOR_RESPONSE_DEFAULT = 2000;
    
    // actual base time to wait for response
    private volatile long baseTimeToWaitForResponse = BASE_TIME_TO_WAIT_FOR_RESPONSE_DEFAULT;
    
    
    
    private static long countTimeslotLengthForSTD_Mode(
            TR_Type.TR_TypeSeries trSeries, int responseDataLength) {
        if(trSeries == TR_Type.TR_TypeSeries.TR72x){
            if(responseDataLength < 19){
                return 3;
            }
            if(responseDataLength < 42){
                return 4;
            }
            return 5;
        } else {                
            //for TR52x series and unknown series
            if(responseDataLength < 12){
                return 3;
            }
            if(responseDataLength < 33){
                return 4;
            }
            if(responseDataLength < 54){
                return 5;
            }
            return 6;            
        }
    }
    
    private static long countTimeslotLengthForLP_Mode(
            TR_Type.TR_TypeSeries trSeries, int responseDataLength) {
        if(trSeries == TR_Type.TR_TypeSeries.TR72x){            
            if(responseDataLength < 9){
                return 8;
            }
            if(responseDataLength < 32){
                return 9;
            }
            return 10;
        }else{    
            //for TR52x series and unknown series
            if(responseDataLength < 14){
                return 8;
            }
            if(responseDataLength < 36){
                return 9;
            }
            return 10;
        }
    }
    
    // counts timeslot length in 10 ms units
    private static long countTimeslotLength(TR_Type.TR_TypeSeries trSer, 
            RF_Mode rfMode, int responseDataLength) {
        switch ( rfMode ) {
            case STD:
                return countTimeslotLengthForSTD_Mode(trSer, responseDataLength);
            case LP:
                return countTimeslotLengthForLP_Mode(trSer, responseDataLength);
            default:
                throw new IllegalStateException("Unknown RF mode used: " + rfMode);
        }
    }
    
    private long countWaitingTimeForConfirmation() {
        return timeToWaitForConfirmation;
    }
    
    
    // base class for all waiting time for response counters 
    private static abstract class WaitingTimeForResponseCounter {
        
       // counts and returns waiting time 
       public abstract long count(CallRequest request, TimingParams timingParams);
    } 
    
    // computes waiting time for FRC requests 
    private static class FRC_WaitingTimeForResponseCounter extends WaitingTimeForResponseCounter {
        
        // FRC mode
        static enum FRC_Mode {
            STANDARD,
            ADVANCED
        }
        
        // maximal lenth of data for sending in standard FRC mode
        private static final int STANDARD_FRC_MAX_DATA_LENGTH = 2;
        
        // determines the called method from FRC Device Interface
        private static FRC.MethodID getCalledFRCMethod(String methodId) {
            for ( FRC.MethodID method : FRC.MethodID.values() ) {
                if ( FRCStandardTransformer.getInstance().transform(method).equals(methodId)) {
                    return method;
                }
            }
            return null;
        }
        
        // determines and returns FRC Mode
        private static FRC_Mode getFRC_Mode(FRC_Command command, FRC.MethodID method) {
            switch ( method ) {
                case SEND:
                case SEND_SELECTIVE:
                    return (command.getUserData().length <= STANDARD_FRC_MAX_DATA_LENGTH)? 
                            FRC_Mode.STANDARD : FRC_Mode.ADVANCED;
                default:
                    return FRC_Mode.STANDARD;
            }
        }
        
        // returns FRC_Command argument 
        private static FRC_Command getFRC_Command(Object[] args) {
            for ( Object arg : args ) {
                if ( arg instanceof FRC_Command ) {
                    return (FRC_Command)arg;
                }
            }
            return null;
        }
          
        // counts waiting time according to specified parameters
        private static long countWaitingTime(
            FRC_Mode frcMode, FRC_TimingParams timingParams
        ) {
            FRC_Configuration.FRC_RESPONSE_TIME coordWaitingTime = timingParams.getResponseTime();
            int coordWaitingTimeInInt = coordWaitingTime.getRepsonseTimeInInt();
            
            switch ( frcMode ) {
                case STANDARD:
                    return timingParams.getBondedNodesNum() * 130 + coordWaitingTimeInInt + 250;
                case ADVANCED:
                    if ( timingParams.getRfMode() == null ) {
                        throw new IllegalStateException("RF mode uknown.");
                    }
                    
                    switch ( timingParams.getRfMode() ) {
                        case STD:
                            return timingParams.getBondedNodesNum() * 150 
                                    + coordWaitingTimeInInt + 290;
                        case LP:
                            return timingParams.getBondedNodesNum() * 200 
                                    + coordWaitingTimeInInt + 390;
                        default:
                            throw new IllegalStateException("RF mode not supported: " + timingParams.getRfMode());
                    }
                default:
                    throw new IllegalStateException("FRC Mode not supported: " + frcMode); 
            }
        }
        
        @Override
        public long count(CallRequest request, TimingParams timingParams) {
            if ( !(timingParams instanceof FRC_TimingParams) ) {
                throw new IllegalArgumentException(
                    "Timing parameters has not correct type. "
                    + "Expected: " + FRC_TimingParams.class
                    + "found: " + timingParams.getClass()
                );
            }
            
            FRC.MethodID calledMethod = getCalledFRCMethod(request.getMethodId());
            if ( calledMethod == null ) {
                throw new IllegalStateException("FRC method not found.");
            }
            
            FRC_Command command = getFRC_Command(request.getArgs());
            if ( command == null ) {
                throw new IllegalStateException("FRC command not found.");
            }
            
            FRC_Mode frcMode = getFRC_Mode(command, calledMethod);
            
            return countWaitingTime(frcMode, (FRC_TimingParams)timingParams);
        }
    }
    
    private long countWaitingTimeForResponse() {
         
        // counting for special cases
        WaitingTimeForResponseCounter waitingTimeForRespCounter 
            = waitingTimeForResponseCounters.get(request.getDeviceInterface());
        if ( waitingTimeForRespCounter != null ) {
            long waitingTime = 0;
            try {
                waitingTime = waitingTimeForRespCounter.count(request, timingParams);
                return waitingTime;
            } catch ( Exception e ) {
                logger.error(
                    "Error during counting of waiting time for a response: {}. "
                    + "Waiting time will be counted as in usual case.", e.toString());
            }
        }
        
        // usual case
        long requestRoutingTime = 0;
        if ( countWithConfirmation ) {
            requestRoutingTime = (confirmation.getHops() + 1) * confirmation.getTimeslotLength() * 10;
            return baseTimeToWaitForResponse + requestRoutingTime + 100;
        }
        
        return baseTimeToWaitForResponse + 100;
    }
    
    private long countWaitingTimeAfterResponse() {
        long actualRespTimeslotLength = countTimeslotLength(trTypeSeries, 
                rfMode, responseDataLength);                              
               
        if ( countWithConfirmation ) {
            if ( confirmation == null ) {
                throw new IllegalStateException(
                        "Confirmation needed for calculation of waiting time after response "
                        + "but not present."
                );
            }
            return ( confirmation.getHops() + 1 ) * confirmation.getTimeslotLength() * 10
                + ( confirmation.getHopsResponse() + 1 ) * actualRespTimeslotLength  * 10
                - (System.currentTimeMillis() - confirmRecvTime);
        }
        
        return ( actualRespTimeslotLength * 10 ) - (System.currentTimeMillis() - responseRecvTime);
    }
    
    private long countWaitingTimeAfterConfirmation() {
        return ( confirmation.getHops() + 1 ) * confirmation.getTimeslotLength() * 10
                - (System.currentTimeMillis() - responseRecvTime);
    }
    
    private long countWaitingTime() {
        long waitingTime = 0;
        switch ( actualState ) {
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
                throw new IllegalStateException("Incorrect state to start waiting from: " + actualState);
        }

        if ( waitingTime < 0 ) {
            waitingTime = 0;
        }
        
        return waitingTime;
    }
    
    
    private class WaitingTimeCounter extends Thread {
        
        private void doTransitionForNewRequest() {
            synchronized ( synchroActualState ) {
                if ( request instanceof BroadcastRequest ) {
                    actualState = ProtocolStateMachine.State.WAITING_FOR_CONFIRMATION;
                } else {
                    if ( isRequestForCoordinator(request) ) {
                        actualState = ProtocolStateMachine.State.WAITING_FOR_RESPONSE;
                    } else {
                        actualState = ProtocolStateMachine.State.WAITING_FOR_CONFIRMATION;
                    }
                }
            }
        }
        
        // will next state be: waiting after confirmation or waiting for response?
        private void doTransitionForWaitingForConfirmation() {
            synchronized ( synchroActualState ) {
                if ( willWaitForResponse ) {
                    actualState = ProtocolStateMachine.State.WAITING_FOR_RESPONSE;
                } else {
                    actualState = ProtocolStateMachine.State.WAITING_AFTER_CONFIRMATION;
                }
            }
        }
        
        // do transition from current state to next state
        private void doTransition() {
            switch ( actualState ) {
                case FREE_FOR_SEND:
                    doTransitionForNewRequest();
                    break;
                case WAITING_FOR_CONFIRMATION:
                    doTransitionForWaitingForConfirmation();
                    break;
                case WAITING_FOR_RESPONSE:
                    actualState = ProtocolStateMachine.State.WAITING_AFTER_RESPONSE;
                    break;
                case WAITING_AFTER_CONFIRMATION:
                case WAITING_AFTER_RESPONSE:
                    actualState = ProtocolStateMachine.State.FREE_FOR_SEND;
                    synchronized ( synchroListener ) {
                        if ( listener != null ) {
                            listener.onFreeForSend();
                        }
                    }
                    break;
                case WAITING_FOR_CONFIRMATION_ERROR:
                    actualState = ProtocolStateMachine.State.FREE_FOR_SEND;
                    synchronized ( synchroListener ) {
                        if ( listener != null ) {
                            listener.onFreeForSend();
                        }
                    }
                    break;
                case WAITING_FOR_RESPONSE_ERROR:
                    actualState = ProtocolStateMachine.State.FREE_FOR_SEND;
                    synchronized ( synchroListener ) {
                        if ( listener != null ) {
                            listener.onFreeForSend();
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Incorrect state to wait in: " + actualState);
            }
        }
        
        // do error transition - required event doesn't come in timeout
        private void doErrorTransition() {
            switch ( actualState ) {
                case WAITING_FOR_CONFIRMATION:
                    actualState = ProtocolStateMachine.State.WAITING_FOR_CONFIRMATION_ERROR;
                    synchronized ( synchroListener ) {
                        if ( listener != null ) {
                            listener.onConfirmationTimeouted();
                        }
                    }
                    break;
                case WAITING_FOR_RESPONSE:
                    actualState = ProtocolStateMachine.State.WAITING_FOR_RESPONSE_ERROR;
                    synchronized ( synchroListener ) {
                        if ( listener != null ) {
                            listener.onResponseTimeouted();
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Cannot do error transition for state: " + actualState);
            }
        }
        
        // consumes new event
        private void consumeNewEvent() {
            synchronized ( synchroNewEvent ) {
                if ( newEvent instanceof NewRequestEvent ) {
                    request = ((NewRequestEvent)newEvent).request;
                    timingParams = ((NewRequestEvent)newEvent).timingParams; 
                    countWithConfirmation = ((NewRequestEvent)newEvent).countWithConfirmation;
                    if ( ((NewRequestEvent)newEvent).request instanceof BroadcastRequest ) {
                        willWaitForResponse = false;
                    } else {
                        willWaitForResponse = true;
                    }
                } else if ( newEvent instanceof ConfirmationReceivedEvent ) {
                    confirmation = ((ConfirmationReceivedEvent)newEvent).confirmation;
                    confirmRecvTime = ((ConfirmationReceivedEvent)newEvent).recvTime; 
                } else if ( newEvent instanceof ResponseReceivedEvent ) {
                    responseDataLength = ((ResponseReceivedEvent)newEvent).responseData.length;
                    responseRecvTime =((ResponseReceivedEvent)newEvent).recvTime;
                } else {
                }
                
                newEvent = null;
            }
        }
        
        @Override
        public void run() {
            // time to wait in some states ( waiting states )
            long waitingTime = 0;
            
            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("Waiting time counter end");
                    return;
                }
                
                // indicates, wheather waiting for new event timeouted 
                // more precisely: waiting on some type of events 
                boolean timeouted = false;
                
                // waiting on new event
                synchronized ( synchroNewEvent ) {
                    while ( newEvent == null ) {
                        // states, where it is possible to wait in for potentionaly
                        // unlimited amount of time
                        if ( actualState == ProtocolStateMachine.State.FREE_FOR_SEND
                            || actualState == ProtocolStateMachine.State.WAITING_FOR_CONFIRMATION_ERROR
                            || actualState == ProtocolStateMachine.State.WAITING_FOR_RESPONSE_ERROR    
                        ) {
                            try {
                                synchroNewEvent.wait();
                            } catch ( InterruptedException ex ) {
                                logger.warn("Waiting time counter interrupted while waiting on new event");
                                return;
                            }
                        }
                        
                        // states, where new event must come in in a limited amount of time
                        else if (
                                actualState == ProtocolStateMachine.State.WAITING_FOR_CONFIRMATION
                                || actualState == ProtocolStateMachine.State.WAITING_FOR_RESPONSE
                          ) {
                            try {
                                //logger.info("run - waiting for confirmation - before waiting");
                                long startTime = System.currentTimeMillis();
                                synchroNewEvent.wait(waitingTime);
                                long endTime = System.currentTimeMillis();
                                //logger.info("run - waiting for confirmation - after waiting");
                                if ( (endTime - startTime) >= waitingTime ) {
                                    timeouted = true;
                                    // IMPORTANT !!! Go out of a while-cycle.
                                    break;
                                }
                            } catch ( InterruptedException ex ) {
                                logger.warn("Waiting time counter interrupted while waiting on new event");
                                return;
                            }
                        }
                        
                        // AFTER states: it is mandatory to wait for a minimal
                        // amount of time
                        else {
                            try {
                                Thread.sleep(waitingTime);
                            } catch ( InterruptedException ex ) {
                                logger.warn("Waiting time counter interrupted while sleeping in 'AFTER' state");
                                return;
                            }
                           
                            // IMPORTANT !!! Go out of a while-cycle.
                            break;
                        }
                    }
                    
                    if ( newEvent != null ) {
                        // consume new event icluding update of variables by
                        // information present in the event
                        consumeNewEvent();
                        //logger.info("run - new event received.");
                    }
                }
                
                // if waiting for new event timeouted, do error transition
                if ( timeouted ) {
                    doErrorTransition();
                    continue;
                }
                
                // do transition to next state
                doTransition();
                
                // count waiting time - can be 0 if there is no need for mandatory waiting, 
                // for example for FREE_FOR_SEND state
                waitingTime = countWaitingTime();
                
                //logger.info("run - before state change.");
                // notify waiting clients about the state change
                synchronized ( stateChangeSignal ) {
                    stateChanged = true;
                    stateChangeSignal.notifyAll();
                }
                //logger.info("run - after state change.");
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
    
    private void initWaitingTimeForResponseCounters() {
        waitingTimeForResponseCounters = new HashMap<>();
        waitingTimeForResponseCounters.put(FRC.class, new FRC_WaitingTimeForResponseCounter());
    }
    
    
    // request
    private CallRequest request = null;
    
    // timing parameters for actual request
    private TimingParams timingParams = null;
    
    // waiting time for response counters
    private Map<Class, WaitingTimeForResponseCounter> waitingTimeForResponseCounters;
    
    // time of reception of a confirmation
    private long confirmRecvTime = -1;
    
    // received confirmation
    private DPA_Confirmation confirmation = null;
    
    // indicates, wheather to count with confirmation in calculation of 
    // waiting time
    private boolean countWithConfirmation = false;
    
    // if machine will wait for a reponse, or will wait for end of confirmation routing
    // this applies only to broadcast as there are not any responses for broadcast requests
    private boolean willWaitForResponse = false;
    
    // time of reception of a response
    private long responseRecvTime = -1;
    
    // response data
    private int responseDataLength = -1;
   
    
    // listener
    private ProtocolStateMachineListener listener = null;
    
    // synchronization object for listener
    private final Object synchroListener = new Object();
    
    
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
    
    /**
     * Creates new object of Protocol Machine.
     * RF mode will be set to STD.
     */
    public ProtocolStateMachine() {
        waitingTimeCounter = new WaitingTimeCounter();
        logger.info("Protocol machine successfully created.");
        this.networkConfigMap = new HashMap<>();
        initWaitingTimeForResponseCounters();
    }
    
    /**
     * Returns actual value of time to wait for confirmation arrival [ in ms ].
     * @return actual value of time to wait for confirmation arrival
     */
    synchronized public long getTimeToWaitForConfirmation() {
        return timeToWaitForConfirmation;
    }
    
    /**
     * Sets time to wait for confirmation arrival.
     * @param time new value of time [ in ms ] to wait for confirmation, cannot be less then 0
     * @throws IllegalArgumentException if specified time is less then 0
     */
    synchronized public void setTimeToWaitForConfirmation(long time) {
        this.timeToWaitForConfirmation = checkTimeToWaitForConfirmation(time);
    }
    
    /**
     * Returns actual value of base time to wait for response arrival [ in ms ].
     * @return actual value of base time to wait for response arrival
     */
    synchronized public long getBaseTimeToWaitForResponse() {
        return baseTimeToWaitForResponse;
    }
    
    /**
     * Sets base time to wait for response arrival.
     * @param time new value of base time [ in ms ] to wait for response, cannot be less then 0
     * @throws IllegalArgumentException if specified time is less then 0
     */
    synchronized public void setBaseTimeToWaitForResponse(long time) {
        this.baseTimeToWaitForResponse = checkBaseTimeToWaitForResponse(time);
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
        
        synchronized ( synchroListener ) {
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
        
        synchronized ( synchroListener ) {
            this.listener = null;
        }
        
        logger.info("Listener unregistered.");
        logger.debug("unregisterListener - end");
    }
    
    /**
     * Add configuration to protocol state machine, which was determinted while 
     * init and is depending on specific network.
     * 
     * @param networkId network name
     * @param config determineted network configuration
     */
    public void addNetworkConfig(String networkId, DeterminetedNetworkConfig config){
        logger.debug("addNetworkConfig - start: networkId={}, config={}", 
                networkId, config);
        networkConfigMap.put(networkId, config);
        logger.debug("addNetworkConfig - end");
    }
    
    /**
     * Returns the actual state of the machine.
     * @return the actual state of the machine
     */
    synchronized public State getState() {
        logger.debug("getState - start: ");
        
        State state = null;
        synchronized ( synchroActualState ) {
            state = actualState;
        }
        
        logger.debug("getState - end: {}", state);
        return state;
    }
    
    /**
     * Indicates, wheather it is possible to send next request.
     * @return {@code true} if it is possible to send next request
     *         {@code false} otherwise
     */
    synchronized public boolean isFreeForSend() {
        logger.debug("isFreeForSend - start: ");
        
        boolean isFreeForSend = false;
        synchronized ( synchroActualState ) {
            isFreeForSend = ( actualState == State.FREE_FOR_SEND ); 
        }
        
        logger.debug("isFreeForSend - end: {}", isFreeForSend);
        return isFreeForSend;
    }
    
    /**
     * Informs the machine, that new request has been sent.
     * @param request sent request
     * @param timingParams timing parameters
     */
    synchronized public void newRequest(CallRequest request, TimingParams timingParams) 
    {
        logger.debug("newRequest - start: request={}, timingParams={}", request, timingParams);
        
        // actual state must be FREE FOR SEND
        synchronized ( synchroActualState ) {
            if ( actualState != State.FREE_FOR_SEND ) {
                throw new IllegalArgumentException(
                    "Cannot send new request because in the " + actualState + " state."
                );
            }
        }
        
        // signaling that new event has come in and what is the next expected state
        synchronized ( synchroNewEvent ) {
            newEvent = new NewRequestEvent(request, timingParams);
            if ( !(request instanceof BroadcastRequest) ) {
                if ( isRequestForCoordinator(request) ) {
                    ((NewRequestEvent)newEvent).countWithConfirmation = false;
                } else {
                    ((NewRequestEvent)newEvent).countWithConfirmation = true;
                }
            }
            
            stateChanged = false;
            synchroNewEvent.notifyAll();
        }
        
        // waiting till actual state changes
        waitForStateChangeSignal();
        
        logger.debug("newRequest - end");
    }
    
    /**
     * Informs the machine, that confirmation has been received.
     * @param recvTime time of confirmation reception
     * @param confirmation received confirmation
     * @throws IllegalArgumentException if the machine is not in {@code WAITING_FOR_CONFIRMATION} state
     * @throws StateTimeoutedException if {@code WAITING_FOR_CONFIRMATION} state was timeouted
     *         during processing of the specified confirmation
     */
    synchronized public void confirmationReceived(long recvTime, DPA_Confirmation confirmation)
        throws StateTimeoutedException 
    {
        logger.debug("confirmationReceived - start: recvTime={}, confirmation={}",
                recvTime, confirmation
        );
        
        synchronized ( synchroActualState ) {
            if ( actualState != State.WAITING_FOR_CONFIRMATION ) {
                throw new IllegalArgumentException(
                    "Unexpected reception of confirmation. Actual state: " + actualState
                );
            }
        }
        
        // signaling that confirmation has come in
        synchronized ( synchroNewEvent ) {
            newEvent = new ConfirmationReceivedEvent(recvTime, confirmation);
            stateChanged = false;
            synchroNewEvent.notifyAll();
        }
        
        //logger.info("confirmationReceived - before waiting for signal");
        try {
            waitForStateChangeSignal();
        } catch ( IllegalStateException e ) {
            State actualStateCopy = null;
            synchronized ( synchroActualState ) {
                actualStateCopy = actualState;
            }
            if ( actualStateCopy == State.WAITING_FOR_CONFIRMATION_ERROR ) {
                throw new StateTimeoutedException("Waiting on confirmation timeouted.");
            } else {
                throw e;
            }
        }
        
        logger.debug("confirmationReceived - end");
    }
    
    /**
     * Informs the machine, that confirmation has been received. Time of calling 
     * of this method will be used as the time of the confirmation reception.
     * @param confirmation received confirmation
     * @throws IllegalArgumentException if the machine is not in {@code WAITING_FOR_CONFIRMATION} state
     * @throws StateTimeoutedException if {@code WAITING_FOR_CONFIRMATION} state was timeouted
     *         during processing of the specified confirmation
     */
    synchronized public void confirmationReceived(DPA_Confirmation confirmation) 
            throws StateTimeoutedException 
    {
        confirmationReceived(System.currentTimeMillis(), confirmation);
    }
    
    /**
     * Informs the machine, that response has been received.
     * @param recvTime time of response reception
     * @param responseData data of the received response
     * @throws IllegalArgumentException if the machine is not in {@code WAITING_FOR_RESPONSE} state
     * @throws StateTimeoutedException if {@code WAITING_FOR_RESPONSE} state was
     *         timeouted during processing of the specified response data
     */
    synchronized public void responseReceived(long recvTime, short[] responseData) 
        throws StateTimeoutedException 
    {
        logger.debug("responseReceived - start: recvTime={}, responseData={}",
                recvTime, Arrays.toString(responseData)
        );
        
        synchronized ( synchroActualState ) {
            if ( actualState != State.WAITING_FOR_RESPONSE ) {
                throw new IllegalArgumentException(
                    "Unexpected reception of the response. Actual state: " + actualState
                );
            }
        }
        
        synchronized ( synchroNewEvent ) {
            newEvent = new ResponseReceivedEvent(recvTime, responseData);
            stateChanged = false;
            synchroNewEvent.notifyAll();
        }
        
        try {
            waitForStateChangeSignal();
        } catch ( IllegalStateException e ) {
            State actualStateCopy = null;
            synchronized ( synchroActualState ) {
                actualStateCopy = actualState;
            }
            if ( actualStateCopy == State.WAITING_FOR_RESPONSE_ERROR ) {
                throw new StateTimeoutedException("Waiting on response timeouted.");
            } else {
                throw e;
            }
        }
        
        logger.debug("responseReceived - end");
    }
    
    /**
     * Informs the machine, that response has been received. Time of calling of
     * this method will be used as the time of the response reception.
     * @param responseData data of the received response
     * @throws StateTimeoutedException if {@code WAITING_FOR_RESPONSE} state was
     *         timeouted during processing of the specified response data
     */
    synchronized public void responseReceived(short[] responseData) 
            throws StateTimeoutedException  
    {
        responseReceived(System.currentTimeMillis(), responseData);
    }
    
    /**
     * Reseting the machine after some of error states has occured. 
     */
    synchronized public void resetAfterError() {
        logger.debug("resetAfterError - start:");
        
        synchronized ( synchroActualState ) {
            if ( 
                (actualState != State.WAITING_FOR_CONFIRMATION_ERROR)
                && (actualState != State.WAITING_FOR_RESPONSE_ERROR)     
            ) {
                throw new IllegalArgumentException(
                    "Reseting can be performed only in error states. Actual state: " + actualState
                );
            }
        }
        
        synchronized ( synchroNewEvent ) {
            newEvent = new ResetEvent();
            stateChanged = false;
            synchroNewEvent.notifyAll();
        }
        
        waitForStateChangeSignal();
        
        logger.info("Reseted.");
        logger.debug("resetAfterError - end");
    }
    
    @Override
    public void destroy() {
        logger.debug("destroy - start:");
        
        terminateWaitingTimeCounter();
        
        logger.info("Destroyed.");
        logger.debug("destroy - end");
    }
}
