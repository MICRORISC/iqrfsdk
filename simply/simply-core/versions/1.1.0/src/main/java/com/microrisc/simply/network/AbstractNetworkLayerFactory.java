
package com.microrisc.simply.network;

/**
 * Base class for network layer factories.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractNetworkLayerFactory<T extends Object, U extends NetworkLayer> {
    /**
     * Returns network layer implementation.
     * @param connectionStorage network connection storage
     * @param T configuration of network layer to create
     * @return network layer implementation
     * @throws Exception if an error has occured
     */
    public abstract U getNetworkLayer(
            NetworkConnectionStorage connectionStorage, 
            T configuration
    ) throws Exception;
}
