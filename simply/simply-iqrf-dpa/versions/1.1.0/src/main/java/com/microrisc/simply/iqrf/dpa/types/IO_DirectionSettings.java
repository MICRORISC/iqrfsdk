
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Encapsulates settings of IO information.
 * 
 * @author Michal Konopa
 */
public class IO_DirectionSettings {
    /** Port to setup a direction to. 0=TRISA, 1=TRISB, … */
    private final int port;
    
    /** Masks pins of the port. */
    private final int mask;
    
    /** Actual direction bits for the masked pins. 0=output, 1=input. */
    private final int value;
    
    
    /**
     * Creates new objects encapsulating IO settings.
     * @param port Port to setup a direction to.
     * @param mask Masks pins of the port.
     * @param value Actual direction bits for the masked pins. 0=output, 1=input.
     */
    public IO_DirectionSettings(int port, int mask, int value) {
        this.port = port;
        this.mask = mask;
        this.value = value;
    }

    /**
     * @return port to setup a direction to. 0=TRISA, 1=TRISB, …
     */
    public int getPort() {
        return port;
    }

    /**
     * @return masks of pins of the port
     */
    public int getMask() {
        return mask;
    }

    /**
     * @return actual direction bits for the masked pins. 0=output, 1=input.
     */
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Port: " + port + NEW_LINE);
        strBuilder.append(" Mask: " + mask + NEW_LINE);
        strBuilder.append(" Value: " + value + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
