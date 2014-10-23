package com.microrisc.simply.connector;

import com.microrisc.simply.ProtocoLayerListener;
import com.microrisc.simply.ProtocolLayerService;

/**
 * Base abstract class for all connector services. 
 * 
 * @author Michal Konopa
 */
public abstract class AbstractConnector
implements 
        Connector,
        ProtocoLayerListener 
{
    
    /** Reference to protocol layer service. */
    protected ProtocolLayerService protocolLayerService = null;
    
    /**
     * Checks specified protocol layer for validity.
     * @param protocolLayerService
     * @return {@code protocolLayerService}
     */
    private ProtocolLayerService checkProtocolLayer(ProtocolLayerService protocolLayerService) {
        if ( protocolLayerService == null ) {
            throw new IllegalArgumentException("Protocol layer service cannot be null");
        }
        return protocolLayerService;
    }
    
    /**
     * Sets protocol layer service communication object to the specified one.
     * @param protocolLayerService protocol layer service object to set, cannot be {@code null}
     */
    protected AbstractConnector(ProtocolLayerService protocolLayerService) {
        this.protocolLayerService = checkProtocolLayer(protocolLayerService);
    }
}
