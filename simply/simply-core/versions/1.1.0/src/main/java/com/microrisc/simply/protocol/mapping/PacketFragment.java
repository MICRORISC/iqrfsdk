
package com.microrisc.simply.protocol.mapping;

/**
 * Describes fragment of packet data. 
 * 
 * @author Michal Konopa
 */
public final class PacketFragment {
    /** Position inside packet, where the data starts. */
    private int startingPosition = 0;
    
    /** Data. */
    private short[] data = null;
    
    
    /**
     * Constructor.
     * @param startingPosition position inside packet, where the data starts
     * @param data data
     */
    public PacketFragment(int startingPosition, short[] data) {
        this.startingPosition = startingPosition;
        this.data = data;
    }

    /**
     * @return the starting position
     */
    public int getStartingPosition() {
        return startingPosition;
    }

    /**
     * @return the data
     */
    public short[] getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "starting position=" + startingPosition + 
                ", data=" + data + 
                " }");
    }
}
