
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Encapsulates information about subDPA request of IQMesh network.
 * 
 * @author Rostislav Spinar
 */
public final class SubDPARequest {
    /** 
     *  Network address of the device in the sub network controlled by the 
     *  IQMesh - Coordinator of the [NC] device to send the DPA request to. 
     */
    private final int subNADR;
    
    /** 
     * Peripehral number to send the DPA request to.
     */
    private final int subPNUM;
    
    /**
     * DPA request command.
     */
    private final int subPCMD;
    
    /**
     * Optional DPA request data depending on the actual subPCMD used.
     */
    private final short[] subPData;
    
    /**
     * Creates new object, which encapsules discovery parameters.
     * @param subNADR Network address of the device in the sub network
     * @param subPNUM Peripehral number to send the DPA request to
     * @param subPCMD DPA request command
     * @param subPData Optional DPA request data depending on the actual subPCMD 
        used.
     */
    public SubDPARequest(int subNADR, int subPNUM, int subPCMD, short[] subPData) {
        this.subNADR = subNADR;
        this.subPNUM = subPNUM;
        this.subPCMD = subPCMD;
        this.subPData = subPData;
    }

    /**
     * @return Network address of the device in the sub network
     */
    public int getSubNADR() {
        return subNADR ;
    }

    /**
     * @return Peripehral number to send the DPA request to
     */
    public int getSubPNUM() {
        return subPNUM;
    }
    
    /**
     * @return DPA request command
     */
    public int getSubPCMD() {
        return subPCMD;
    }
    
    /**
     * @return Optional DPA request data depending on the actual subPCMD used
     */
    public short[] getSubPData() {
        return subPData;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Sub NADR: " + subNADR + NEW_LINE);
        strBuilder.append(" Sub PNUM: " + subPNUM + NEW_LINE);
        strBuilder.append(" Sub PCMD: " + subPCMD + NEW_LINE);
        strBuilder.append(" Sub PData: " + subPData + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
