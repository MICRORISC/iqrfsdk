
package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * Information about Peripheral.
 * 
 * @author Michal Konopa
 */
public final class PeripheralInfo {
    // peripheral type
    private final PeripheralType peripheralType;
    
    // extended peripheral characteristic
    private final ExtPerCharacteristic extPerCharacteristic;
    
    // optional peripheral specific information
    private final short par1;
    
    // optional peripheral specific information
    private final short par2;
    
    
    /**
     * Creates new {@code PeripheralInfo} object.
     * @param perType peripheral type
     * @param extPerCharacteristic extended peripheral characteristic
     * @param par1 optional peripheral specific information
     * @param par2 optional peripheral specific information
     */
    public PeripheralInfo(
            PeripheralType perType, ExtPerCharacteristic extPerCharacteristic, 
            short par1, short par2
    ) {
        this.peripheralType = perType;
        this.extPerCharacteristic = extPerCharacteristic;
        this.par1 = par1;
        this.par2 = par2;
    }

    /**
     * @return peripheral type
     */
    public PeripheralType getPeripheralType() {
        return peripheralType;
    }

    /**
     * @return extended peripheral characteristic
     */
    public ExtPerCharacteristic getExtPerCharacteristic() {
        return extPerCharacteristic;
    }

    /**
     * @return Par1 - optional peripheral specific information
     */
    public short getPar1() {
        return par1;
    }

    /**
     * @return Par2 - optional peripheral specific information
     */
    public short getPar2() {
        return par2;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Peripheral type: " + peripheralType + NEW_LINE);
        strBuilder.append(" Extended per. characteristic: " + extPerCharacteristic + NEW_LINE);
        strBuilder.append(" Parameter 1: " + par1 + NEW_LINE);
        strBuilder.append(" Parameter 2: " + par2 + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
