
package com.microrisc.simply.iqrf.dpa.types;

import java.io.Serializable;

/**
 * Encapsulates information about Thermometer result.
 * 
 * @author Michal Konopa
 */
public final class Thermometer_values implements Serializable {
    /** Value */
    private final short value;
        
    /** Fractial value */
    private final byte fractialValue; 
    
    
    /**
     * Creates new {@code Thermometer_values} object.
     * @param value 1byte value
     * @param fractialValue 2byte value
     */
    public Thermometer_values(short value, byte fractialValue) {
        this.value = value;
        this.fractialValue = fractialValue;
    }

    /**
     * @return value
     */
    public short getValue() {
        return value;
    }

    /**
     * @return fractial value
     */
    public byte getFractialValue() {
        return fractialValue;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Value: " + value + NEW_LINE);
        strBuilder.append(" Fractial value: " + fractialValue + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormattedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("Value: " + value + NEW_LINE);
        strBuilder.append("Fractial value: " + fractialValue + NEW_LINE);
        
        return strBuilder.toString();
    }
}
