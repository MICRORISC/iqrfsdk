
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
    
    
    /** 
     * Checks connector for validity.
     * @param connector connector to check
     */
    private static ConnectorService checkConnector(ConnectorService connector) {
        if (connector == null) {
            throw new NullPointerException("Connector cannot be null");
        }
        return connector;
    }
    
    /** 
     * Checks results container for validity.
     * @param resultsContainer results container to check
     */
    private static CallRequestProcessingInfoContainer checkResultsContainer(
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        if ( resultsContainer == null ) {
            throw new NullPointerException("Results container cannot be null");
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
        logger.debug(logPrefix + "dispatchCall - start: methodId={}, arguments={}", 
                methodId, args
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
            logger.error(logPrefix + "Error while dispatching call: ", e);
            return null;
        }
        
        lastCallId = callId;
        
        logger.debug("{}dispatchCall - end: {}", logPrefix, callId);
        logger.info(logPrefix + "Method {} call dispatched, id={}", methodId, callId);
        return callId;
    }
    
    
    @Override
    public UUID dispatchCall(String methodId, Object[] args, long timeout) {
        Object[] logArgs = new Object[4];
        logArgs[0] = logPrefix;
        logArgs[1] = methodId;
        logArgs[2] = args;
        logArgs[3] = timeout;
        
        logger.debug("{}dispatchCall - start: methodId={}, arguments={}, timeout={}", logArgs);
        
        lastDispatchError = null;
        UUID callId = null;
        try {
            callId = connector.callMethod(
                    this, implementedDeviceInterface, methodId, args, timeout
            ); 
        } catch ( Exception e ) {
            lastCallId = null;
            lastDispatchError = e;
            logger.error(logPrefix + "Error while dispatching call: ", e);
            return null;
        }
        
        lastCallId = callId;
        
        logger.debug("{}dispatchCall - end: {}", logPrefix, callId);
        logger.info(logPrefix + "Method {} call dispatched, id={}", methodId, callId);
        return callId;
    }
    
    @Override
    public void onCallRequestProcessingInfo(
            CallRequestProcessingInfo procInfo, UUID callId
    ) {
        logger.debug(logPrefix + "onCallRequestProcessingInfo - start: procInfo={}, callId={}", 
                procInfo, callId
        );
        
        synchronized( results ) {
            results.put(callId, procInfo);
            results.notify();
        }
        
        logger.debug("{}onCallRequestProcessingInfo - end", logPrefix);
        logger.info("{}New result from connector, id={}", logPrefix, callId);
    }
    
}
