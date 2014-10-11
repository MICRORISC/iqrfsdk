
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Extended Peripheral Characteristic.
 * 
 * @author Michal Konopa
 */
public enum ExtPerCharacteristic {
    DEFAULT         (0x00),
    READ            (0x01),
    WRITE           (0x02),
    READ_WRITE      (0x03);
    
    // characteristic
    private final int characteristic;
    
    
    private ExtPerCharacteristic(int characteristic) {
        this.characteristic = characteristic;
    }
    
    public int getCharacteristicValue() {
        return characteristic;
    }
}
