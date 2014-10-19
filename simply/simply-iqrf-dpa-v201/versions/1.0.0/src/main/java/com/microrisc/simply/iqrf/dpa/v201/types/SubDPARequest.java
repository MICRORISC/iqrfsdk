
package com.microrisc.simply.iqrf.dpa.v201.types;

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
    private final int subNAdr;
    
    /** 
     * Peripehral number to send the DPA request to.
     */
    private final int subPNum;
    
    /**
     * DPA request command.
     */
    private final int subPCmd;
    
    /** DPA request HW profile. */
    private final int subHwProfile;
    
    /**
     * Optional DPA request data depending on the actual subPCmd used.
     */
    private final short[] subPData;
    
    private static int checkSubNadr(int subNADR) {
        if ( (subNADR < 0) || (subNADR > 255) ) {
            throw new IllegalArgumentException(
                    "subNADR must be within the interval of [0..255]"
            );
        }
        return subNADR;
    }
    
    private static int checkSubPnum(int subPNUM) {
        if ( (subPNUM < 0) || (subPNUM > 255) ) {
            throw new IllegalArgumentException(
                    "subPNUM must be within the interval of [0..255]"
            );
        }
        return subPNUM;
    }
    
    private static int checkSubPcmd(int subPCMD) {
        if ( (subPCMD < 0) || (subPCMD > 255) ) {
            throw new IllegalArgumentException(
                    "subPCMD must be within the interval of [0..255]"
            );
        }
        return subPCMD;
    }
    
    private static int checkSubHwProfile(int subHWPID) {
        if ( (subHWPID < 0) || (subHWPID > 255) ) {
            throw new IllegalArgumentException(
                    "subHWPID must be within the interval of [0..255]"
            );
        }
        return subHWPID;
    }
    
    
    /**
     * Creates new object, which encapsules discovery parameters.
     * @param subNAdr Network address of the device in the sub network
     * @param subPNum Peripehral number to send the DPA request to
     * @param subPCmd DPA request command
     * @param subHwProfile DPA request HW profile
     * @param subPData Optional DPA request data depending on the actual subPCmd 
     *        used.
     */
    public SubDPARequest(
            int subNAdr, int subPNum, int subPCmd, int subHwProfile, short[] subPData
    ) {
        this.subNAdr = checkSubNadr(subNAdr);
        this.subPNum = checkSubPnum(subPNum);
        this.subPCmd = checkSubPcmd(subPCmd);
        this.subHwProfile = checkSubHwProfile(subHwProfile);
        this.subPData = ( subPData == null )? new short[] {} : subPData;
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
     * @return DPA request HW profile
     */
    public int getSubHwProfile() {
        return subHwProfile;
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
        strBuilder.append(" Sub HW Profile: " + subHwProfile + NEW_LINE);
        strBuilder.append(" Sub PData: " + subPData + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
