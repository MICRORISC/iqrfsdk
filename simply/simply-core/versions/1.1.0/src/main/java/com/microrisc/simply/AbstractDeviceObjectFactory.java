
package com.microrisc.simply;

/**
 * Base class of device object factories.
 * 
 * @author Michal Konopa
 * @param <T> type of connector service
 * @param <U> type of configuration settings
 * @param <V> type of Device Object to create
 */
public abstract class AbstractDeviceObjectFactory
<T extends ConnectorService, U extends Object, V extends DeviceObject> {
    /**
     * Returns device object.
     * @param networkId ID of network, which returned device object belongs to.
     * @param nodeId ID of node, which returned device object belongs to.
     * @param implClass Device interface implementation class
     * @param connector connector to use
     * @param configuration configuration settings
     * @return device object
     * @throws Exception if an error has occured
     */
    public abstract V getDeviceObject(
            String networkId, 
            String nodeId, 
            T connector, 
            Class implClass, 
            U configuration
    ) throws Exception;
}
