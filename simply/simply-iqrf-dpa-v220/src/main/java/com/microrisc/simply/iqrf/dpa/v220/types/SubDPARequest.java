/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microrisc.simply.iqrf.dpa.v220.types;

/**
 * Encapsulates information about subDPA request of IQMesh network.
 * 
 * @author Rostislav Spinar
 */
public final class SubDPARequest {
    /** 
     * Network address of the device in the sub network controlled by the 
     * IQMesh - Coordinator of the [NC] device to send the DPA request to. 
     */
    private final int subNADR;
    
    /** 
     * Peripeheral number to send the DPA request to.
     */
    private final int subPNUM;
    
    /**
     * DPA request command.
     */
    private final int subPCMD;
    
    /** DPA request HW profile. */
    private final int subHWPID;
    
    /**
     * Optional DPA request data depending on the actual subPCMD used.
     */
    private final short[] subPData;
    
    private static int checkSubNADR(int subNADR) {
        if ( (subNADR < 0) || (subNADR > 255) ) {
            throw new IllegalArgumentException(
                    "subNADR must be within the interval of [0..255]"
            );
        }
        return subNADR;
    }
    
    private static int checkSubPNUM(int subPNUM) {
        if ( (subPNUM < 0) || (subPNUM > 255) ) {
            throw new IllegalArgumentException(
                    "subPNUM must be within the interval of [0..255]"
            );
        }
        return subPNUM;
    }
    
    private static int checkSubPCMD(int subPCMD) {
        if ( (subPCMD < 0) || (subPCMD > 255) ) {
            throw new IllegalArgumentException(
                    "subPCMD must be within the interval of [0..255]"
            );
        }
        return subPCMD;
    }
    
    private static int checkSubHWPID(int subHWPID) {
        if ( (subHWPID < 0) || (subHWPID > 255) ) {
            throw new IllegalArgumentException(
                    "subHWPID must be within the interval of [0..255]"
            );
        }
        return subHWPID;
    }
    
    
    /**
     * Creates new object, which encapsules discovery parameters.
     * @param subNADR Network address of the device in the sub network
     * @param subPNUM Peripehral number to send the DPA request to
     * @param subPCMD DPA request command
     * @param subHWPID DPA request HW profile
     * @param subPData Optional DPA request data depending on the actual subPCMD used.
     */
    public SubDPARequest(
        int subNADR, int subPNUM, int subPCMD, int subHWPID, short[] subPData
    ) {
        this.subNADR = checkSubNADR(subNADR);
        this.subPNUM = checkSubPNUM(subPNUM);
        this.subPCMD = checkSubPCMD(subPCMD);
        this.subHWPID = checkSubHWPID(subHWPID);
        this.subPData = ( subPData == null )? new short[] {} : subPData;
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
     * @return DPA request HW profile
     */
    public int getSubHWPID() {
        return subHWPID;
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
        strBuilder.append(" Sub HWPID: " + subHWPID + NEW_LINE);
        strBuilder.append(" Sub PData: " + subPData + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
