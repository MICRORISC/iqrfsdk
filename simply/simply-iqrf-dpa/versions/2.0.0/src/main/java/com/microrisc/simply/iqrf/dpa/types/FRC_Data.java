

package com.microrisc.simply.iqrf.dpa.types;

import java.util.Arrays;

/**
 * FRC data collected from nodes.
 * 
 * @author Michal Konopa
 */
public final class FRC_Data {
    /** 
     * Return code of the sendFRC() IQRF OS function. See IQRF OS documentation
     * for more information.
     */
    private final int status;
    
    /**
     * Data collected from the nodes.
     */
    private final short[] data;
    
    
    /**
     * Creates new FRC data object. 
     * @param status Return code of the sendFRC() IQRF OS function.
     * @param data Data collected from the nodes.
     */
    public FRC_Data(int status, short[] data) {
        this.status = status;
        this.data = new short[ data.length ];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     * @return Return code of the sendFRC() IQRF OS function
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return Data collected from the nodes
     */
    public short[] getData() {
        short[] dataToReturn = new short[ data.length ];
        System.arraycopy(data, 0, dataToReturn, 0, data.length);
        return dataToReturn;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Status: " + status + NEW_LINE);
        strBuilder.append(" Data: " + Arrays.toString(data) + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    /**
     * Returns pretty formated string information. 
     * @return pretty formated string information.
     */
    public String toPrettyFormatedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("Status: " + status + NEW_LINE);
        strBuilder.append("Data: " + Arrays.toString(data) + NEW_LINE);
        
        return strBuilder.toString();
    }
    
}
