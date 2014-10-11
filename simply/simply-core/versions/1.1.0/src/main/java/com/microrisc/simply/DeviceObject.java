
package com.microrisc.simply;

/**
 * Base interface for access to Device Objects.
 * 
 * @author Michal Konopa
 */
public interface DeviceObject {
    /**
     * Returns identifier of network, which this device object belongs to.
     * @return identifier of network, which this device object belongs to.
     */
    String getNetworkId();
    
    /**
     * Returns identifier of node, which this device object belongs to.
     * @return identifier of node, which this device object belongs to.
     */
    String getNodeId();
    
    /**
     * Returns device interface, which this object implements or {@code null}, 
     * if this object doesn't implement any device interface.
     * @return implemented device interface <br>
     *         {@code null} if this device object doesn't implement any device interface
     */
    Class getImplementedDeviceInterface();
}
