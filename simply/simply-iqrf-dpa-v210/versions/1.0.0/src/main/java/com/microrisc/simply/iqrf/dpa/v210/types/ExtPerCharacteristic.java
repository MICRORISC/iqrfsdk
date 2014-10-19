
package com.microrisc.simply.iqrf.dpa.v210.types;

/**
 * Extended Peripheral Characteristic.
 * 
 * @author Michal Konopa
 */
public enum ExtPerCharacteristic {
    DEFAULT         (0b00),
    READ            (0b01),
    WRITE           (0b10),
    READ_WRITE      (0b11);
    
    // characteristic
    private final int characteristic;
    
    
    private ExtPerCharacteristic(int characteristic) {
        this.characteristic = characteristic;
    }
    
    public int getCharacteristicValue() {
        return characteristic;
    }
}
