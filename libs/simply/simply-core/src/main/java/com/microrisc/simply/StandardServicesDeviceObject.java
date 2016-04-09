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

package com.microrisc.simply;

import com.microrisc.simply.di_services.StandardServices;
import com.microrisc.simply.errors.DispatchingRequestToConnectorError;
import com.microrisc.simply.errors.CallRequestProcessingError;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Device object implementing standard services.
 * 
 * @author Michal Konopa
 */
public class StandardServicesDeviceObject 
extends ConnectedDeviceObject
implements StandardServices
{
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(StandardServicesDeviceObject.class);
    
    // multiplier for getting number of miliseconds from number of nanoseconds 
    private static final double NANOSEC_TO_MILISEC = 1.0/1000000;
    
    
    /** Initial value of timeout to wait for result from DO method call. */
    public static final long INITIAL_WAITING_TIMEOUT = 10000;
    
    /** Timeout to wait for result from DO method call. */
    protected long waitingTimeout = INITIAL_WAITING_TIMEOUT;
    
    
    
    
    private static long checkWaitingTimeout(long waitingTimeout) {
        if ( waitingTimeout == UNLIMITED_WAITING_TIMEOUT ) {
            return waitingTimeout;
        }
        
        if ( waitingTimeout < 0 ) {
            throw new IllegalArgumentException(
                    "Waiting timeout must be nonnegative or equal to " + UNLIMITED_WAITING_TIMEOUT
            );
        }
        
        return waitingTimeout;
    }
    
    private static UUID checkCallId(UUID callId) {
        if ( callId == null ) {
            throw new IllegalArgumentException("Call ID cannot be null");
        }
        return callId;
    }
    
    // returns processing info for specified call request
    private CallRequestProcessingInfo getCallRequestProcessingInfo(UUID callId) {
        CallRequestProcessingInfo procInfo = results.get(callId);
        
        // if processing info is not present at device object, get it from connector
        if ( procInfo == null ) {
            procInfo = connector.getCallRequestProcessingInfo(callId);
        }
        
        // if proc info is still null, something get wrong in the connector
        if ( procInfo == null ) {

            logger.error("{}getCallRequestProcessingInfo is null, check connector: callId={}", 
                logPrefix, callId
            );
            
            return null;

        }
        return procInfo;
    }
    
    
    /**
     * Creates new device object connected to specified connector with defined 
     * network ID and node ID, on which is possible to call DI methods asynchronously. 
     * @param networkId identifier of network, which this device object belongs to.
     * @param nodeId identifier of node, which this device object belongs to.
     * @param connector connector to underlaying network.
     * @param resultsContainer container, where to store incomming results of 
     *                         performed method calls.
     */
    public StandardServicesDeviceObject(String networkId, String nodeId, 
            ConnectorService connector, CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    /**
     * @return {@code null} if result for specified method call ID doesn't exist. 
     */
    @Override
    public <T> T getCallResult(UUID callId, Class<T> resultClass, long timeout) {
        logger.debug("{}getCallResult - start: callId={}, timeout={}", 
                logPrefix, callId, timeout
        );
        
        checkCallId(callId);
        
        if ( timeout == UNLIMITED_WAITING_TIMEOUT ) {
            return getCallResultInUnlimitedWaitingTimeout(callId, resultClass);
        }
        
        if ( timeout < 0 ) {
            throw new IllegalArgumentException("Bad value of timeout: " + timeout);
        }
        
        if ( timeout == 0 ) {
            return getCallResultImmediately(callId, resultClass);
        }
        
        // setting of processing timeout
        connector.setCallRequestMaximalProcessingTime(callId, timeout);
        
        synchronized( results ) {
            while ( results.get(callId) == null ) {
                long startTime;
                double timeElapsed;
                try {
                    startTime = System.nanoTime();
                    results.wait( timeout );
                    timeElapsed = (System.nanoTime() - startTime) * NANOSEC_TO_MILISEC;
                } catch ( InterruptedException e ) {
                    logger.warn("{}Get call result - interrupted", logPrefix);
                    break;
                }
                
                if ( timeElapsed >= timeout ) {
                    logger.info("{}Get call result - time elapsed", logPrefix);
                    break;
                }
                
                timeout -= timeElapsed;
            }
        }
        
        T callResult = getCallResultImmediately(callId, resultClass);
        
        logger.debug("{}getCallResult - end: {}", logPrefix, callResult);
        return callResult;
    }
    
    @Override
    public <T> T getCallResultInDefaultWaitingTimeout(UUID callId, Class<T> resultClass) {
        return getCallResult(callId, resultClass, waitingTimeout);
    }
    
    @Override
    public <T> T getCallResultInUnlimitedWaitingTimeout(UUID callId, Class<T> resultClass) {
        logger.debug("{}getCallResultInUnlimitedWaitingTimeout - start: callId={}", 
                logPrefix, callId
        );
        
        checkCallId(callId);
        
        // setting of processing timeout
        connector.setCallRequestMaximalProcessingTime(callId, UNLIMITED_WAITING_TIMEOUT);
        
        synchronized( results ) {
            while ( results.get(callId) == null ) {
                try {
                    results.wait( 0 );
                } catch ( InterruptedException e ) {
                    logger.warn("{}Get call result - interrupted", logPrefix);
                    break;
                }
            }
        }
        
        T callResult = getCallResultImmediately(callId, resultClass);
        
        logger.debug("{}getCallResultInUnlimitedWaitingTimeout - end: {}", logPrefix, callResult);
        return callResult;
    }
    
    /**
     * @return {@code null} if result for specified method call ID doesn't
     *                      exist. 
     */
    @Override
    public <T> T getCallResultImmediately(UUID callId, Class<T> resultClass) {
        logger.debug("{}getCallResultImmediately - start: callId={}", logPrefix, callId);
        
        checkCallId(callId);
        
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(callId);
        Object methodCallResult = null;
        CallResult callResult = procInfo.getCallResult();
        if ( callResult != null ) {
            methodCallResult = callResult.getMethodCallResult();
            logger.debug("{}getCallResultImmediately - end: {}", logPrefix, methodCallResult);
            logger.info("{}Get call result: {}", logPrefix, methodCallResult);
        } else {
            logger.warn("{}Get call result - result not found", logPrefix);
            logger.debug("{}getCallResultImmediately - end: null", logPrefix);
        }
        
        return (T)methodCallResult;
    }
    
    /**
     * Timeout must be nonnegative.
     * @param timeout timeout to set
     * @throws IllegalArgumentException if specified timeout is not nonnegative 
     *         or is not equal to the {@code UNLIMITED_WAITING_TIMEOUT}
     */
    @Override
    public void setDefaultWaitingTimeout(long timeout) {
        this.waitingTimeout = checkWaitingTimeout(timeout);
    }
    
    @Override
    public long getDefaultWaitingTimeout() {
        return waitingTimeout;
    }
    
    @Override
    public void cancelCallRequest(UUID callId) {
        checkCallId(callId);
        connector.cancelCallRequest(callId);
    }
    
    @Override
    public CallRequestProcessingState getCallRequestProcessingState(UUID callId) {
        checkCallId(callId);
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(callId);
        
        if(procInfo != null) {
            return procInfo.getState();
        }
        
        return null;
    }
    
    @Override
    public CallRequestProcessingState getCallRequestProcessingStateOfLastCall() {
        if ( lastCallId != null ) {
            return getCallRequestProcessingState(lastCallId);
        }
        return null;
    }
    
    @Override
    public void cancelCallRequestOfLastCall() {
        if ( lastCallId != null ) {
            connector.cancelCallRequest(lastCallId);
        }
    }
    
    @Override
    public UUID getIdOfLastExexutedCallRequest() {
        return lastCallId;
    }

    
    
    /**
     * Returns error info about processing of specified method call.
     * @param callId DO method call about which processing to get error info
     * @return error info about processing of specified method call.
     */
    @Override
    public CallRequestProcessingError getCallRequestProcessingError(UUID callId) {
        logger.debug("{}getError - start: callId={}", logPrefix, callId);
        
        checkCallId(callId);
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(callId);
        CallRequestProcessingError error = procInfo.getError();
        if ( error != null) {
            logger.debug("{}getError - end: {}", logPrefix, error);
            logger.info("{}Get error info: {}", logPrefix, error);
        } else {
            logger.warn("{}Get error info - error info not exists", logPrefix);
            logger.debug("{}getError - end: {}", logPrefix, error);
        }
        
        return error;
    }
    
    @Override
    public CallRequestProcessingError getCallRequestProcessingErrorOfLastCall() {
        logger.debug("{}getErrorOfLastCall - start: ", logPrefix);
        
        if ( lastCallId == null ) {
            if ( lastDispatchError != null ) {
                return new DispatchingRequestToConnectorError( lastDispatchError );
            }
            return null;
        }
        
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(lastCallId);
        CallRequestProcessingError error = procInfo.getError();
        if ( error != null ) {
            logger.debug("{}getErrorOfLastCall - end: {}", logPrefix, error);
            logger.info("{}Get error info: {}", logPrefix, error);
        } else {
            logger.warn("{}Get error info - error info not exists", logPrefix);
            logger.debug("{}getErrorOfLastCall - end: {}", logPrefix, error);
        }
        
        return error;
    }
    
    /**
     * Returns additional information relating to specified method call.
     * @param callId DO method call about which to get additional information
     * @return additional information relating to specified method call
     */
    @Override
    public Object getCallResultAdditionalInfo(UUID callId) {
        logger.debug("{}getAdditionalInfo - start: callId={}", logPrefix, callId);
        
        checkCallId(callId);
        
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(callId);
        Object additionalInfo = null;
        CallResult callResult = procInfo.getCallResult();
        if ( callResult != null ) {
            additionalInfo = callResult.getAdditionalInfo();
            logger.debug("{}getAdditionalInfo - end: {}", logPrefix, additionalInfo);
            logger.info("{}Get additional info: {}", logPrefix, additionalInfo);
        } else {
            logger.warn("{}Get additional info - result not found", logPrefix);
            logger.debug("{}getAdditionalInfo - end: {}", logPrefix, additionalInfo);
        }
        
        return additionalInfo;
    }

    @Override
    public Object getCallResultAdditionalInfoOfLastCall() {
        logger.debug("{}getAdditionalInfoOfLastCall - start:", logPrefix);
        
        if ( lastCallId == null ) {
            logger.warn("{}Get additional info of last call - last call ID is null", logPrefix);
            logger.debug("{}getAdditionalInfoOfLastCall - end: null", logPrefix);
        }
        
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(lastCallId);
        Object additionalInfo = null;
        CallResult callResult = procInfo.getCallResult();
        if ( callResult != null ) {
            additionalInfo = callResult.getAdditionalInfo();
            logger.debug("{}getAdditionalInfoOfLastCall - end: {}", logPrefix, additionalInfo);
            logger.info("{}Get additional info of last call: {}", logPrefix, additionalInfo);
        } else {
            logger.warn("{}Get additional info of last call - result not found", logPrefix);
            logger.debug("{}getAdditionalInfoOfLastCall - end: {}", logPrefix, additionalInfo);
        }
        
        return additionalInfo;
    }
    
}
