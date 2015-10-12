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

package com.microrisc.simply.iqrf.dpa.v22x.broadcasting;

import com.microrisc.simply.CallRequestProcessingInfo;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.CallResult;
import com.microrisc.simply.ConnectorListener;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.errors.DispatchingRequestToConnectorError;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastingConnectorService;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.StandardMethodIdTransformers;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of broadcast services.
 * 
 * @author Michal Konopa
 */
public final class BroadcastServicesDefaultImpl
implements ConnectorListener, BroadcastServices 
{
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BroadcastServicesDefaultImpl.class);
    
    
    /** Connector Service to the underlaying network. */
    private final BroadcastingConnectorService broadcastingConnService;
    
    /** Incomming results of performed broadcasts. */
    private final CallRequestProcessingInfoContainer results;
    
    /** Last call id. */
    private UUID lastCallId = null;
    
    /** Dispatch error of lastly issued dispatching. */
    private Exception lastDispatchError = null;
    
    
    /** Initial default waiting timeout. */
    public static long INITIAL_DEFAULT_WAITING_TIMEOUT = 5000;
    
    /** Actual default waiting timeout. */
    private long defaultWaitingTimeout = INITIAL_DEFAULT_WAITING_TIMEOUT;
    
    
    private static UUID checkCallId(UUID callId) {
        if ( callId == null ) {
            throw new IllegalArgumentException("Call ID cannot be null");
        }
        return callId;
    }
    
    private static long checkDefaultWaitingTimeout(long timeout) {
        if ( timeout == UNLIMITED_WAITING_TIMEOUT ) {
            return timeout;
        }
        
        if ( timeout < 0 ) {
            throw new IllegalArgumentException(
                    "Waiting timeout must be nonnegative or equal to " + UNLIMITED_WAITING_TIMEOUT
            );
        }
        
        return timeout;
    }
    
    private static String checkNetworkId(String networkId) {
        if ( networkId == null ) {
            throw new IllegalArgumentException("Network ID cannot be null");
        }
        return networkId;
    }
    
    private static Class checkDeviceInterface(Class deviceInterface) {
        if ( deviceInterface == null ) {
            throw new IllegalArgumentException("Device Interface cannot be null");
        }
        return deviceInterface;
    }
    
    private static Object checkMethodId(Object methodId) {
        if ( methodId == null ) {
            throw new IllegalArgumentException("Method ID cannot be null");
        }
        return methodId;
    }
    
    private static BroadcastingConnectorService checkBroadcastingConnectorService(
            BroadcastingConnectorService broadcastingConnService
    ) {
        if ( broadcastingConnService == null ) {
            throw new IllegalArgumentException("Broadcasting connecttor service cannot be null");
        }
        return broadcastingConnService;
    }
    
    private static CallRequestProcessingInfoContainer checkResultsContainer(
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        if ( resultsContainer == null ) {
            throw new IllegalArgumentException("Results container cannot be null");
        }
        return resultsContainer;
    }
    
    
    // returns processing info for specified broadcast request
    private CallRequestProcessingInfo getCallRequestProcessingInfo(UUID requestId) 
    {
        CallRequestProcessingInfo procInfo = results.get(requestId);
        
        // if processing info is not present at device object, get it from connector
        if ( procInfo == null ) {
            procInfo = broadcastingConnService.getCallRequestProcessingInfo(requestId);
        }
        
        // if proc info is still null, something get wrong in the connector
        if ( procInfo == null ) {
            throw new IllegalStateException(
                "Could not get broadcast request processing info from connector for "
                + "request: " + requestId 
            );
        }
        return procInfo;
    }
    
    /** Default request HW profile. */
    public static int DEFAULT_REQUEST_HW_PROFILE = 0xFFFF;
    
    /** Request HW profile. */
    private int requestHwProfile = DEFAULT_REQUEST_HW_PROFILE;
    
    
    private int checkRequestHwProfile( int requestHwProfile ) {
        if ( (requestHwProfile < 0x0000) || (requestHwProfile > 0xFFFF) ) {
            throw new IllegalArgumentException("Invalid value of request HW profile: " + requestHwProfile);
        }
        return requestHwProfile;
    }
    
    
    
    /**
     * Creates new simple broadcaster.
     * @param broadcastingConnService broadcasting connector service to use 
     * @param resultsContainer container, where to store incomming results of 
     *                         performed broadcast calls.
     * @throws IllegalArgumentException if {@code broadcastingConnService} or
     *         {@code resultsContainer} is {@code null}
     */
    public BroadcastServicesDefaultImpl(
            BroadcastingConnectorService broadcastingConnService,
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        this.broadcastingConnService = checkBroadcastingConnectorService(broadcastingConnService);
        this.results = checkResultsContainer(resultsContainer);
    }
    
    @Override
    public void setRequestHwProfile(int requestHwProfile) {
        this.requestHwProfile = checkRequestHwProfile( requestHwProfile );
    }
    
    @Override
    public int getRequestHwProfile() {
        return requestHwProfile;
    }
    
    @Override
    public void onCallRequestProcessingInfo(
            CallRequestProcessingInfo procInfo, UUID callId
    ) {
        logger.debug("onCallRequestProcessingInfo - start: procInfo={}, callId={}", 
                procInfo, callId
        );
        
        synchronized( results ) {
            results.put(callId, procInfo);
            results.notify();
        }
        
        logger.debug("onCallRequestProcessingInfo - end");
        logger.info("New result from connector, id={}", callId);
    }
    
    @Override
    public UUID sendRequest(
            String networkId, Class deviceInterface, Object methodId, Object[] args, 
            MethodIdTransformer methodIdTransformer
    ) {
        logger.debug("sendRequest - start: networkId={}, deviceInterface={}, "
                + "methodId={}, args={}, methodIdTransformer={}", 
                networkId, deviceInterface, methodId, args, methodIdTransformer
        );
        
        checkNetworkId(networkId);
        checkDeviceInterface(deviceInterface);
        checkMethodId(methodId);
        
        if ( methodIdTransformer == null ) {
            methodIdTransformer = StandardMethodIdTransformers.getInstance()
                    .getTransformer(deviceInterface);
            if ( methodIdTransformer == null ) {
                throw new IllegalStateException("Method transformer has not been found");
            }
        }
        
        String methodIdStr = methodIdTransformer.transform(methodId);
        if ( methodIdStr == null ) {
            throw new IllegalStateException("Transformation of method object key returned null value");
        }
        
        Object[] argsWithHwProfile = null;
        if ( args == null ) {
            argsWithHwProfile = new Object[] { getRequestHwProfile() } ; 
        } else {
            argsWithHwProfile = new Object[ args.length + 1 ];
            argsWithHwProfile[0] = getRequestHwProfile();
            System.arraycopy( args, 0, argsWithHwProfile, 1, args.length );
        }
        
        UUID requestId = null;
        try {
            requestId = broadcastingConnService.broadcastCallMethod(
                    this, networkId, deviceInterface, methodIdStr, argsWithHwProfile
            );
        } catch ( Exception e ) {
            lastCallId = null;
            lastDispatchError = e;
            logger.error("Error while dispatching request: ", e);
            return null;
        }
        
        lastCallId = requestId;
        
        logger.debug("sendRequest - end: {}", requestId);
        logger.info("Request dispatched, id={}", requestId);
        return requestId;
    }
    
    @Override
    public UUID sendRequest(
            String networkId, Class deviceInterface, Object methodId, Object[] args
    ) {
        return sendRequest(networkId, deviceInterface, methodId, args, null);
    }
    
    @Override
    public BroadcastResult getBroadcastResult(UUID requestId, long timeout) {
        return getCallResult(requestId, BroadcastResult.class, timeout);
    }

    @Override
    public BroadcastResult getBroadcastResultInDefaultWaitingTimeout(UUID requestId) {
        return getCallResultInDefaultWaitingTimeout(requestId, BroadcastResult.class);
    }

    @Override
    public BroadcastResult getBroadcastResultImmediately(UUID requestId) {
        return getCallResultImmediately(requestId, BroadcastResult.class);
    }
    
    /**
     * @return {@code null} if result for specified request ID doesn't exist. 
     */
    @Override
    public BroadcastResult broadcast(
            String networkId, Class deviceInterface, Object methodId, Object[] args, 
            MethodIdTransformer methodIdTransformer
    ) {
        logger.debug("broadcast - start: networkId={}, deviceInterface={}, "
                + "methodId={}, args={}, methodIdTransformer={}", 
                networkId, deviceInterface, methodId, args, methodIdTransformer
        );
        
        checkNetworkId(networkId);
        checkDeviceInterface(deviceInterface);
        checkMethodId(methodId);
        
        if ( methodIdTransformer == null ) {
            methodIdTransformer = StandardMethodIdTransformers.
                    getInstance().getTransformer(deviceInterface);
            if ( methodIdTransformer == null ) {
                throw new IllegalStateException("Method transformer has not been found");
            }
        }
        
        String methodIdStr = methodIdTransformer.transform(methodId);
        if ( methodIdStr == null ) {
            throw new IllegalStateException("Transformation of method object key returned null value");
        }
        
        Object[] argsWithHwProfile = null;
        if ( args == null ) {
            argsWithHwProfile = new Object[] { getRequestHwProfile() } ; 
        } else {
            argsWithHwProfile = new Object[ args.length + 1 ];
            argsWithHwProfile[0] = getRequestHwProfile();
            System.arraycopy( args, 0, argsWithHwProfile, 1, args.length );
        }
        
        UUID requestId = null;
        try {
            requestId = broadcastingConnService.broadcastCallMethod(
                    this, networkId, deviceInterface, methodIdStr, argsWithHwProfile, defaultWaitingTimeout
            );
        } catch ( Exception e ) {
            lastCallId = null;
            lastDispatchError = e;
            logger.error("Error while dispatching request: ", e);
            return null;
        }
        
        lastCallId = requestId;
        
        logger.debug("broadcast - end: {}", requestId);
        logger.info("Request dispatched, id={}", requestId);
        
        return getBroadcastResultInDefaultWaitingTimeout(requestId);
    }
    
    @Override
    public BroadcastResult broadcast(
            String networkId, Class deviceInterface, Object methodId, Object[] args
    ) {
        return broadcast(networkId, deviceInterface, methodId, args, null);
    }
    
    /**
     * @return {@code null} if result for specified method call ID doesn't exist. 
     */
    @Override
    public <T> T getCallResult(UUID callId, Class<T> resultClass, long timeout) {
        logger.debug("getCallResult - start: callId={}, timeout={}", 
                callId, timeout
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
        
        // setting processing timeout
        broadcastingConnService.setCallRequestMaximalProcessingTime(callId, timeout);
        
        long startTime = System.currentTimeMillis();
        synchronized( results ) {
            while ( results.get(callId) == null ) {
                try {
                    results.wait( timeout );
                } catch ( InterruptedException e ) {
                    logger.warn("Get call result - interrupted");
                    break;
                }
                
                long timeElapsed = System.currentTimeMillis() - startTime;
                if ( timeElapsed >= timeout ) {
                    logger.info("Get call result - time elapsed");
                    break;
                }
            }
        }
        
        T callResult = getCallResultImmediately(callId, resultClass);
        
        logger.debug("getCallResult - end: {}", callResult);
        return callResult;
    }
    
    @Override
    public <T> T getCallResultInDefaultWaitingTimeout(UUID callId, Class<T> resultClass) {
        return getCallResult(callId, resultClass, defaultWaitingTimeout);
    }
    
    /**
     * @return {@code null} if result for specified method call ID doesn't
     *                      exist. 
     */
    @Override
    public <T> T getCallResultImmediately(UUID callId, Class<T> resultClass) {
        logger.debug("getCallResultImmediately - start: callId={}", callId);
        
        checkCallId(callId);
        
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(callId);
        Object methodCallResult = null;
        CallResult callResult = procInfo.getCallResult();
        if ( callResult != null ) {
            methodCallResult = callResult.getMethodCallResult();
            logger.debug("getCallResultImmediately - end: {}", methodCallResult);
            logger.info("Get call result: {}", methodCallResult);
        } else {
            logger.warn("Get call result - result not found");
            logger.debug("getCallResultImmediately - end: null");
        }
        
        return (T)methodCallResult;
    }
    
    @Override
    public <T> T getCallResultInUnlimitedWaitingTimeout(UUID callId, Class<T> resultClass) {
        logger.debug("getCallResultInUnlimitedWaitingTimeout - start: callId={}", 
                callId
        );
        
        checkCallId(callId);
        
        // setting of processing timeout
        broadcastingConnService.setCallRequestMaximalProcessingTime(callId, UNLIMITED_WAITING_TIMEOUT);
        
        synchronized( results ) {
            while ( results.get(callId) == null ) {
                try {
                    results.wait( 0 );
                } catch ( InterruptedException e ) {
                    logger.warn("Get call result - interrupted");
                    break;
                }
            }
        }
        
        T callResult = getCallResultImmediately(callId, resultClass);
        
        logger.debug("getCallResultInUnlimitedWaitingTimeout - end: {}", callResult);
        return callResult;
    }
    
    /**
     * Timeout must be nonnegative.
     * @param timeout timeout to set
     * @throws IllegalArgumentException if specified timeout is not nonnegative 
     *         or is not equal to the {@code UNLIMITED_WAITING_TIMEOUT}
     */
    @Override
    public void setDefaultWaitingTimeout(long timeout) {
        this.defaultWaitingTimeout = checkDefaultWaitingTimeout(timeout);
    }
    
    @Override
    public long getDefaultWaitingTimeout() {
        return defaultWaitingTimeout;
    }
    
    @Override
    public void cancelCallRequest(UUID callId) {
        checkCallId(callId);
        broadcastingConnService.cancelCallRequest(callId);
    }
    
    @Override
    public CallRequestProcessingState getCallRequestProcessingState(UUID callId) {
        checkCallId(callId);
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(callId);
        return procInfo.getState();
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
            broadcastingConnService.cancelCallRequest(lastCallId);
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
        logger.debug("getError - start: callId={}", callId);
        
        checkCallId(callId);
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(callId);
        CallRequestProcessingError error = procInfo.getError();
        if ( error != null) {
            logger.debug("getError - end: {}", error);
            logger.info("Get error info: {}", error);
        } else {
            logger.warn("Get error info - error info not exists");
            logger.debug("getError - end: {}", error);
        }
        
        return error;
    }
    
    @Override
    public CallRequestProcessingError getCallRequestProcessingErrorOfLastCall() {
        logger.debug("getErrorOfLastCall - start: ");
        
        if ( lastCallId == null ) {
            if ( lastDispatchError != null ) {
                return new DispatchingRequestToConnectorError( lastDispatchError );
            }
            return null;
        }
        
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(lastCallId);
        CallRequestProcessingError error = procInfo.getError();
        if ( error != null ) {
            logger.debug("getErrorOfLastCall - end: {}", error);
            logger.info("Get error info: {}", error);
        } else {
            logger.warn("Get error info - error info not exists");
            logger.debug("getErrorOfLastCall - end: {}", error);
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
        logger.debug("getAdditionalInfo - start: callId={}", callId);
        
        checkCallId(callId);
        
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(callId);
        Object additionalInfo = null;
        CallResult callResult = procInfo.getCallResult();
        if ( callResult != null ) {
            additionalInfo = callResult.getAdditionalInfo();
            logger.debug("getAdditionalInfo - end: {}", additionalInfo);
            logger.info("Get additional info: {}", additionalInfo);
        } else {
            logger.warn("Get additional info - result not found");
            logger.debug("getAdditionalInfo - end: {}", additionalInfo);
        }
        
        return additionalInfo;
    }

    @Override
    public Object getCallResultAdditionalInfoOfLastCall() {
        logger.debug("getAdditionalInfoOfLastCall - start:");
        
        if ( lastCallId == null ) {
            logger.warn("Get additional info of last call - last call ID is null");
            logger.debug("getAdditionalInfoOfLastCall - end: null");
        }
        
        CallRequestProcessingInfo procInfo = getCallRequestProcessingInfo(lastCallId);
        Object additionalInfo = null;
        CallResult callResult = procInfo.getCallResult();
        if ( callResult != null ) {
            additionalInfo = callResult.getAdditionalInfo();
            logger.debug("getAdditionalInfoOfLastCall - end: {}", additionalInfo);
            logger.info("Get additional info of last call: {}", additionalInfo);
        } else {
            logger.warn("Get additional info of last call - result not found");
            logger.debug("getAdditionalInfoOfLastCall - end: {}", additionalInfo);
        }
        
        return additionalInfo;
    }

}
