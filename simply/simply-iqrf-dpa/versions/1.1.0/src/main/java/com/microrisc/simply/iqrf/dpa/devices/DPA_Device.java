

package com.microrisc.simply.iqrf.dpa.devices;

/**
 * Base Device Interface for DPA devices.
 * 
 * @author Michal Konopa
 */
public interface DPA_Device {
    /**
     * Sets HW profile on specified value
     * @param hwProfile new HW profile value
     */
    void setHwProfile(int hwProfile);
    
    /**
     * Returns actual HW profile value.
     * @return actual HW profile value.
     */
    int getHwProfile();
}
