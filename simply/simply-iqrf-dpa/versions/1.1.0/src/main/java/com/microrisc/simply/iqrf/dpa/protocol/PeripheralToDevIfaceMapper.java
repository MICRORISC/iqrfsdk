
package com.microrisc.simply.iqrf.dpa.protocol;

import java.util.Set;

/**
 * Holds mapping between DPA Peripherals and corresponding Device Interfaces.
 * 
 * @author Michal Konopa
 */
public interface PeripheralToDevIfaceMapper {
    /**
     * Returns set of Device Interfaces, each of which there exists concrete mapping for.
     * @return set of Device Interfaces, each of which there exists concrete mapping for.
     */
    Set<Class> getMappedDeviceInterfaces();
    
    /**
     * Returns Device Interface, which corresponds to specified peripheral.
     * @param perId peripheral DI
     * @return Device Interface, which corresponds to specified peripheral <br>
     *         {@code null}, if DI was not found
     */
    Class getDeviceInterface(String perId);

    /**
     * Returns identifier of peripheral, which corresponds to specified DI.
     * @param devInterface Device Interface
     * @return identifier of peripheral, which corresponds to specified DI <br>
     *         {@code null}, if peripheral was not found
     */
    String getPeripheralId(Class devInterface);
    
}
