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

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for device objects connected to connector.
 * 
 * @author Michal Konopa
 */
public class ConnectedDeviceObject 
extends BaseDeviceObject 
implements CallRequestDispatcher, ConnectorListener {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ConnectedDeviceObject.class);
    
    
    /** Connector Service to underlaying network. */
    protected final ConnectorService connector;
    
    /** Incomming results of performed method calls. */
    protected final CallRequestProcessingInfoContainer results;
    
    /** Last method call id. */
    protected UUID lastCallId = null;
    
    /** Dispatch error of lastly issued dispatching. */
    protected Exception lastDispatchError = null;
    
    
    /** Prefix of logged data. */
    protected final String logPrefix;
    
    /** Creates log prefix. */
    private String createLogPrefix() {
        StringBuilder sb = new StringBuilder();
        sb.append(networkId);
        sb.append(":");
        sb.append(nodeId);
        sb.append("-");
        sb.append(getImplementedDeviceInterface().getSimpleName());
        sb.append(": ");
        return sb.toString();
    }
    
    
    private static ConnectorService checkConnector(ConnectorService connector) {
        if ( connector == null ) {
            throw new IllegalArgumentException("Connector cannot be null");
        }
        return connector;
    }
    
    private static CallRequestProcessingInfoContainer checkResultsContainer(
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        if ( resultsContainer == null ) {
            throw new IllegalArgumentException("Results container cannot be null");
        }
        return resultsContainer;
    }
    
    
    /**
     * Creates new device object connected to specified connector with defined 
     * network ID and node ID.
     * @param networkId identifier of network, which this device object belongs to.
     * @param nodeId identifier of node, which this device object belongs to.
     * @param connector connector to underlaying network.
     * @param resultsContainer container, where to store incomming results of 
     *                         performed method calls.
     * @throws IllegalArgumentException if any one of {@code networkId}, {@code nodeId},
     *         {@code connector} or {@code resultsContainer} is {@code null}
     */
    public ConnectedDeviceObject(
            String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId);
        this.connector = checkConnector(connector);
        this.results = checkResultsContainer(resultsContainer);
        this.logPrefix = createLogPrefix();
    }
    
    @Override
    public UUID dispatchCall(String methodId, Object[] args) {
        logger.debug("{}dispatchCall - start: methodId={}, arguments={}", 
                logPrefix, methodId, args
        );
        
        lastDispatchError = null;
        UUID callId = null;
        try {
            callId = connector.callMethod(
                    this, implementedDeviceInterface, methodId, args
            ); 
        } catch ( Exception e ) {
            lastCallId = null;
            lastDispatchError = e;
            logger.error("{}Error while dispatching call: {}", logPrefix, e);
            return null;
        }
        
        lastCallId = callId;
        
        logger.debug("{}dispatchCall - end: {}", logPrefix, callId);
        logger.info("{}Method {} call dispatched, id={}", logPrefix, methodId, callId);
        return callId;
    }
    
    
    @Override
    public UUID dispatchCall(String methodId, Object[] args, long timeout) {
        logger.debug("{}dispatchCall - start: methodId={}, arguments={}, timeout={}", 
                logPrefix, methodId, args, timeout
        );
        
        lastDispatchError = null;
        UUID callId = null;
        try {
            callId = connector.callMethod(
                    this, implementedDeviceInterface, methodId, args, timeout
            ); 
        } catch ( Exception e ) {
            lastCallId = null;
            lastDispatchError = e;
            logger.error("{}Error while dispatching call: {}", logPrefix, e);
            return null;
        }
        
        lastCallId = callId;
        
        logger.debug("{}dispatchCall - end: {}", logPrefix, callId);
        logger.info("{}Method {} call dispatched, id={}", logPrefix, methodId, callId);
        return callId;
    }
    
    @Override
    public void onCallRequestProcessingInfo(
            CallRequestProcessingInfo procInfo, UUID callId
    ) {
        logger.debug("{}onCallRequestProcessingInfo - start: procInfo={}, callId={}", 
                logPrefix, procInfo, callId
        );
        
        synchronized( results ) {
            results.put(callId, procInfo);
            results.notify();
        }
        
        logger.debug("{}onCallRequestProcessingInfo - end", logPrefix);
        logger.info("{}New result from connector, id={}", logPrefix, callId);
    }
    
}
