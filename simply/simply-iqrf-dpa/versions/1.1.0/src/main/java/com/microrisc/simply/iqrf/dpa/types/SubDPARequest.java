
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Encapsulates information about subDPA request of IQMesh network.
 * 
 * @author Rostislav Spinar
 */
public class SubDPARequest {
    
    /** 
     *  Network address of the device in the sub network controlled by the 
     *  IQMesh – Coordinator of the [NC] device to send the DPA request to. 
     */
    private final int subNAdr;
    
    /** 
     * Peripehral number to send the DPA request to.
     */
    private final int subPNum;
    
    /**
     * DPA request command.
     */
    private final int subPCmd;
    
    /**
     * Optional DPA request data depending on the actual subPCmd used.
     */
    private final short[] subPData;
    
    /**
     * Creates new object, which encapsules discovery parameters.
     * @param subNAdr Network address of the device in the sub network
     * @param subPNum Peripehral number to send the DPA request to
     * @param subPCmd DPA request command
     * @param subPData Optional DPA request data depending on the actual subPCmd 
     *        used.
     */
    public SubDPARequest(int subNAdr, int subPNum, int subPCmd, short[] subPData) {
        this.subNAdr = subNAdr;
        this.subPNum = subPNum;
        this.subPCmd = subPCmd;
        this.subPData = subPData;
    }

    /**
     * @return Network address of the device in the sub network
     */
    public int getSubNAdr() {
        return subNAdr ;
    }

    /**
     * @return Peripehral number to send the DPA request to
     */
    public int getSubPNum() {
        return subPNum;
    }
    
    /**
     * @return DPA request command
     */
    public int getSubPCmd() {
        return subPCmd;
    }
    
    /**
     * @return Optional DPA request data depending on the actual subPCmd used
     */
    public short[] getSubPData() {
        return subPData;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Sub NAddr: " + subNAdr + NEW_LINE);
        strBuilder.append(" Sub PNum: " + subPNum + NEW_LINE);
        strBuilder.append(" Sub PCmd: " + subPCmd + NEW_LINE);
        strBuilder.append(" Sub PData: " + subPData + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
