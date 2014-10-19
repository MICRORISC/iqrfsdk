
package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * Baud rate constants.
 * 
 * @author Michal Konopa
 */
public enum BaudRate {
    BR1200      (0x00),
    BR2400      (0x01),
    BR4800      (0x02),
    BR9600      (0x03),
    BR19200     (0x04),
    BR38400     (0x05),
    BR57600     (0x06),
    BR115200    (0x07);
    
    private final int baudRateConstant;
    
    
    private BaudRate(int baudRateConstant) {
        this.baudRateConstant = baudRateConstant;
    }
    
    /**
     * Returns value of constant. 
     * @return value of constant.
     */
    public int getBaudRateConstant() {
        return baudRateConstant;
    }
}
