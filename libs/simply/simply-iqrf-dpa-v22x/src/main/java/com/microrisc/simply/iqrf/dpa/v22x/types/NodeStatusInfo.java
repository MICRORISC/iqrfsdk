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

package com.microrisc.simply.iqrf.dpa.v22x.types;

import java.util.Arrays;

/**
 * Encapsulates status information of node of IQMesh network.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public final class NodeStatusInfo {
    /** Logical network address of the node. */
    private final int address;
    
    /** VRN (Virtual Routing Number). */
    private final int vrn;
    
    /** Zone index (zone number + 1) */
    private final int zoneIndex;
    
    /** NTW DID variable. */
    private final int ntwDID;
    
    /** parent VRN */
    private final int parentVrn;
    
    /** 2B user address */
    private final int userAddress;
    
    /** Network identification NID0, NID1 */
    private final short[] networkId;
    
    /** VRN of the first Node in a given zone */
    private final int vrnFirstNodeInZone;
    
    /** Network configuration */
    private final int networkConfiguration;
    
    /** 
     * Flags.
     * Bit 0 indicates whether the Node device is bonded.
     */
    private final int flags;
    
    
    /**
     * Creates new {@code NodeStatusInfo} object.
     * @param address logical network address of the node
     * @param vrn VRN (virtual routing number)
     * @param zoneIndex zone index
     * @param ntwDID {@code ntwDID} variable value
     * @param parentVrn parent VRN
     * @param userAddress 2B user address
     * @param networkId Network identification NID0, NID1
     * @param vrnFirstNodeInZone VRN of the first Node in a given zone
     * @param networkConfiguration Network configuration
     * @param flags flags
     */
    public NodeStatusInfo(
            int address, int vrn, int zoneIndex, int ntwDID, int parentVrn, 
            int userAddress, short[] networkId, int vrnFirstNodeInZone, 
            int networkConfiguration, int flags
    ) {
        this.address = address;
        this.vrn = vrn;
        this.zoneIndex = zoneIndex;
        this.ntwDID = ntwDID;
        this.parentVrn = parentVrn;
        this.userAddress = userAddress;
        this.networkId = networkId;
        this.vrnFirstNodeInZone = vrnFirstNodeInZone;
        this.networkConfiguration = networkConfiguration;
        this.flags = flags;
    }

    /**
     * @return the address
     */
    public int getAddress() {
        return address;
    }
    
    /**
     * @return Virtual Routing Number
     */
    public int getVrn() {
        return vrn;
    }

    /**
     * @return zone index
     */
    public int getZoneIndex() {
        return zoneIndex;
    }
    
    /**
     * @return {@code ntwDID} variable value 
     */
    public int getNtwDID() {
        return ntwDID;
    }
    
    /**
     * @return parent VRN
     */
    public int getParentVrn() {
        return parentVrn;
    }

    /**
     * @return the user address
     */
    public int getUserAddress() {
        return userAddress;
    }
    
    /**
     * @return the network identification
     */
    public short[] getNetworkId() {
        return networkId;
    }
    
    /**
     * @return the VRN of the first Node in a given zone
     */
    public int getVrnFirstNodeinZone() {
        return vrnFirstNodeInZone;
    }
    
    /**
     * @return the network configuration
     */
    public int getNetworkConfiguration() {
        return networkConfiguration;
    }
    
    /**
     * @return flags. Bit 0 indicates whether the Node device is bonded.
     */
    public int getFlags() {
        return flags;
    }
    
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Address: " + address + NEW_LINE);
        strBuilder.append(" VRN: " + vrn + NEW_LINE);
        strBuilder.append(" Zone index: " + zoneIndex + NEW_LINE);
        strBuilder.append(" Parent VRN: " + parentVrn + NEW_LINE);
        strBuilder.append(" User address: " + userAddress + NEW_LINE);
        strBuilder.append(" Network ID: " + Arrays.toString(networkId) + NEW_LINE);
        strBuilder.append(" VRN of the first node in zone: " + vrnFirstNodeInZone + NEW_LINE);
        strBuilder.append(" Network configuration: " + networkConfiguration + NEW_LINE);
        strBuilder.append(" Flags: " + flags + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormattedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("Address: " + address + NEW_LINE);
        strBuilder.append("VRN: " + vrn + NEW_LINE);
        strBuilder.append("Zone index: " + zoneIndex + NEW_LINE);
        strBuilder.append("Parent VRN: " + parentVrn + NEW_LINE);
        strBuilder.append("User address: " + userAddress + NEW_LINE);
        strBuilder.append("Network ID: " + Arrays.toString(networkId) + NEW_LINE);
        strBuilder.append("VRN of the first node in zone: " + vrnFirstNodeInZone + NEW_LINE);
        strBuilder.append("Network configuration: " + networkConfiguration + NEW_LINE);
        strBuilder.append("Flags: " + flags + NEW_LINE);
        
        return strBuilder.toString();
    }
}
