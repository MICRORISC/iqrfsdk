
package com.microrisc.simply.protocol.mapping;

/**
 * Describes mapping of constant Java value into packet of protocol layer.
 * 
 * @author Michal Konopa
 */
public final class ConstValueToPacketMapping {
    /** Starting position of converted value in protocol packet. */
    private int startingPosition = 0;
    
    /** Protocol packet representation of converted Java value. */
    private short[] convertedValue = null;
    
    
    /**
     * Creates new Constant Mapping object.
     * @param startingPosition starting position of converted value in protocol packet
     * @param convertedValue converted value
     */
    public ConstValueToPacketMapping(int startingPosition, short[] convertedValue) {
        this.startingPosition = startingPosition;
        this.convertedValue = convertedValue;
    }
    
    /**
     * @return the starting position of converted value in protocol packet
     */
    public int getStartingPosition() {
        return startingPosition;
    }
    
    /**
     * @return protocol packet representation of converted Java value
     */
    public short[] getConvertedValue() {
        return convertedValue;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "starting position=" + startingPosition + 
                ", converted value=" +  convertedValue + 
                " }");   
    }
}
