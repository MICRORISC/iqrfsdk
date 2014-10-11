
package com.microrisc.simply.network;

/**
 * Abstract base class for network layer.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractNetworkLayer implements NetworkLayer {
    /** Connection storage to use. */
    protected NetworkConnectionStorage connectionStorage;
    
    /**
     * Protected constructor. Sets used connection storage to the specified one. 
     * @param connectionStorage storage of information about connections to networks 
     */
    protected AbstractNetworkLayer(NetworkConnectionStorage connectionStorage) {
        this.connectionStorage = connectionStorage;
    }
}
