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

/**
 * Encapsulates information about node newly bonded to IQMesh network.
 * 
 * @author Michal Konopa
 */
public final class BondedNode {
    /** Address of the node newly bonded to the network. */
    private final short bondedAddress;
    
    /** Number of bonded network nodes. */
    private final short bondedNodesNum;
    
    
    /**
     * Creates new {@code BondedNode} object.
     * @param bondedAddress address of the node newly bonded to the network
     * @param bondedNodesNum number of bonded network nodes
     */
    public BondedNode(short bondedAddress, short bondedNodesNum) {
        this.bondedAddress = bondedAddress;
        this.bondedNodesNum = bondedNodesNum;
    }

    /**
     * @return address of the node newly bonded to the network
     */
    public short getBondedAddress() {
        return bondedAddress;
    }

    /**
     * @return number of bonded nodes
     */
    public int getBondedNodesNum() {
        return bondedNodesNum;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Bonded address: " + bondedAddress + NEW_LINE);
        strBuilder.append(" Number of bonded nodes: " + bondedNodesNum + NEW_LINE);
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
        
        strBuilder.append("Bonded address: " + bondedAddress + NEW_LINE);
        strBuilder.append("Number of bonded nodes: " + bondedNodesNum + NEW_LINE);
        
        return strBuilder.toString();
    }
}
