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

import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;

/**
 * Encapsulates information about parameters of discovery process of IQMesh network.
 * 
 * @author Michal Konopa
 */
public final class DiscoveryParams {
    /** TX Power used for discovery. */
    private final int txPower;
    
    /** 
     * Specifies maximum node address to be part of the discovery process. This 
     * feature allows to split all node devices into tho parts: [1] devices having 
     * address from 0 to MaxAddr will be part of the discovery process thus they 
     * become routers, [2] devices having address from MaxAddr+1 to 239 will not 
     * be routers. See IQRF OS documentation for more information. 
     */
    private final int maxAddr;
    
    
    /** TX power lower bound. */
    public static final int TXPOWER_LOWER_BOUND = 0x00; 
    
    /** TX power upper bound. */
    public static final int TXPOWER_UPPER_BOUND = 0x07; 
    
    
    private static int checkTxPower(int txPower) {
        if ( (txPower < TXPOWER_LOWER_BOUND) || (txPower > TXPOWER_UPPER_BOUND) ) {
            throw new IllegalArgumentException(
                    "TX power out of the bounds of"
                    + "[" + TXPOWER_LOWER_BOUND + ".." + TXPOWER_UPPER_BOUND + "]"
                    + ": " + txPower
            );
        }
        return txPower;
    }
    
    private static int checkMaxNodeAddress(int maxNodeAddress) {
        if ( (maxNodeAddress < 0 ) 
              || (maxNodeAddress > DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX) ) 
        {
            throw new IllegalArgumentException(
                    "Max node address out of the bounds of "
                    + "[" + 0 + ".." + DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX + "]"
                    + " : " + maxNodeAddress
            );
        }
        return maxNodeAddress;
    }
    
    
    /**
     * Creates new object, which encapsules discovery parameters.
     * @param txPower TX Power used for discovery.
     * @param maxAddr Specifies maximum node address to be part of the discovery 
     * process.
     * @throws IllegalArgumentException if: <br> 
     *         specified TX power is out of [{@code TXPOWER_LOWER_BOUND}..{@code TXPOWER_UPPER_BOUND}] interval <br> 
     *         specified node address is out of [{@code 0}..{@code 239}] interval
     */
    public DiscoveryParams(int txPower, int maxAddr) {
        this.txPower = checkTxPower(txPower);
        this.maxAddr = checkMaxNodeAddress(maxAddr);
    }

    /**
     * @return TX Power used for discovery
     */
    public int getTxPower() {
        return txPower;
    }

    /**
     * @return maximum number of zones that will be created during discovery process.
     */
    public int getMaxAddr() {
        return maxAddr;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" TX power: " + txPower + NEW_LINE);
        strBuilder.append(" Maximum address number: " + maxAddr + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
