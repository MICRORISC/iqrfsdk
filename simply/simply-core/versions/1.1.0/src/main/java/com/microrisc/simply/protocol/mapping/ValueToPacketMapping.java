
package com.microrisc.simply.protocol.mapping;

import com.microrisc.simply.types.AbstractConvertor;

/**
 * Base class of classes, which descibe mapping from Java values to
 * protocol layer packets.
 * 
 * @author Michal Konopa
 */
public class ValueToPacketMapping {
    /** Starting position of converted value in protocol packet. */
    private int startingPosition = 0;
    
    /** Convertor to use for conversion of Java value to sequence of bytes. */
    private AbstractConvertor convertor = null;
    
    
    /**
     * Creates new value - to - protocol mapping.
     * @param startingPosition starting position of converted value in the protocol packet
     * @param convertor Convertor to use for conversion of Java value
     */
    public ValueToPacketMapping(int startingPosition, AbstractConvertor convertor) {
        this.startingPosition = startingPosition;
        this.convertor = convertor;
    }
    
    /**
     * @return starting position of converted value in protocol packet
     */
    public int getStartingPosition() {
        return startingPosition;
    }

    /**
     * @return the convertor
     */
    public AbstractConvertor getConvertor() {
        return convertor;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "starting position =" + startingPosition + 
                ", convertor=" + convertor + 
                " }");
    }
}
