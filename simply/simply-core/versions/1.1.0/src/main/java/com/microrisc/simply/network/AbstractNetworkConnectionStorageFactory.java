
package com.microrisc.simply.network;

/**
 * Base class for network connection storages.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractNetworkConnectionStorageFactory
<T extends Object, U extends NetworkConnectionStorage> {
    /**
     * Returns network connection storage - according to configuration.
     * @param configuration configuration for getting connection storage
     * @return network connection storage
     * @throws Exception if some error has occured
     */
    public abstract U getNetworkConnectionStorage(T configuration) throws Exception;
}
