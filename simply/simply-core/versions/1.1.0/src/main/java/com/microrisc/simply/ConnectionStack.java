
package com.microrisc.simply;

import com.microrisc.simply.connector.Connector;
import com.microrisc.simply.protocol.ProtocolLayer;
import com.microrisc.simply.network.NetworkLayer;

/**
 * Access to connection stack of underlaying networks.
 * 
 * @author Michal Konopa
 */
public interface ConnectionStack {

    /**
     * Returns connector.
     * @return connector
     */
    Connector getConnector();

    /**
     * Returns network layer.
     * @return network layer
     */
    NetworkLayer getNetworkLayer();

    /**
     * Returns protocol layer.
     * @return protocol layer
     */
    ProtocolLayer getProtocolLayer();
    
    /**
     * Starts connection stack.
     * @throws SimplyException if an error has occured during starting process
     */
    void start() throws SimplyException;
    
    /**
     * Destroy connection stack and frees up used resources.
     */
    void destroy();
}
