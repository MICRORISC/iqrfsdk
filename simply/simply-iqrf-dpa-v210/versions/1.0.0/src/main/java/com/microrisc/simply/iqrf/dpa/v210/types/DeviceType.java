
package com.microrisc.simply.iqrf.dpa.v210.types;

/**
 * Device types.
 * 
 * @author Michal Konopa
 */
public enum DeviceType {
    GATEWAY         (0x01),
    COORDINATOR     (0x02),
    NODE            (0x03);
  
    /** device type */
    private final int type;
    
    
    private DeviceType(int type) {
        this.type = type;
    }
    
    /**
     * Returns integer value of device type.
     * @return integer value of device type.
     */
    int getTypeValue() {
        return type;
    }
}
