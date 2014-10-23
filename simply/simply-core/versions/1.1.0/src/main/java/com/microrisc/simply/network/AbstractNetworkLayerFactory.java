
package com.microrisc.simply.network;

/**
 * Base class for network layer factories.
 * 
 * @author Michal Konopa
 * @param <T> type of configuration to use
 * @param <U> type of network layer implementation to return
 */
public abstract class AbstractNetworkLayerFactory<T extends Object, U extends NetworkLayer> {
    /**
     * Returns network layer implementation.
     * @param connectionStorage network connection storage
     * @param configuration configuration to use
     * @return network layer implementation
     * @throws Exception if an error has occured
     */
    public abstract U getNetworkLayer(
            NetworkConnectionStorage connectionStorage, 
            T configuration
    ) throws Exception;
}
