
package com.microrisc.simply;

import java.util.Map;

/**
 * Encapsulates access to underlaying network node. Each node has devices, which are 
 * accessed via DeviceObject objects - each {@code DeviceObject} instance provides access
 * to concrete device on the node.
 * 
 * @author Michal Konopa
 */
public interface Node {
    /**
     * Returns node identifier.
     * @return node identifier.
     */
    String getId();
    
    /**
     * Returns identifier of network, which this node belongs to.
     * @return identifier of network, which this node belongs to.
     */
    String getNetworkId();
    
    /**
     * Returns DO, which implements specified device interface.
     * @param <T> type of device interface
     * @param deviceInterface device interface, which is implemented by returned 
     *                        device object
     * @return Device Object, which implements specified Device Interface.
     */
    <T> T getDeviceObject(Class<T> deviceInterface);
    
    /**
     * Returns mapping of device interfaces into its implementing device objects.
     * @return mapping of device interfaces into its implementing device objects.
     */
    Map<Class, DeviceObject> getDeviceObjectsMap();
       
}
