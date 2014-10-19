

package com.microrisc.simply.iqrf.dpa.v210.devices.impl;

/**
 * Helper class to check if input values conforms to various data types.
 * <p>
 * Main usage is for checking Device Interfaces method arguments.
 * 
 * @author Michal Konopa
 */
class DataTypesChecker {
    /** Lower value of Byte. */
    public static final int BYTE_LOWER_BOUND = 0x00;
    
    /** Upper value of Byte. */
    public static final int BYTE_UPPER_BOUND = 0xFF;
    
    public static boolean isByteValue(int value) {
        return (value >= BYTE_LOWER_BOUND) && (value <= BYTE_UPPER_BOUND);
    }
    
}
