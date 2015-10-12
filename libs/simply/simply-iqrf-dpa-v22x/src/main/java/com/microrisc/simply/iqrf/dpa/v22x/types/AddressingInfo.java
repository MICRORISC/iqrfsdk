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
 * Encapsulates addressing information of IQMesh Coordinator.
 * 
 * @author Michal Konopa
 */
public final class AddressingInfo {
    /** Number of bonded network nodes. */ 
    private final int bondedNodesNum;
    
    /** Discovery ID of the network. */
    private final int did;
    
    /**
     * Creates new {@code AddressingInfo} object.
     * @param bondedNodesNum number of bonded network nodes
     * @param did discovery ID of the network
     */
    public AddressingInfo(int bondedNodesNum, int did) {
        this.bondedNodesNum = bondedNodesNum;
        this.did = did;
    }

    /**
     * @return number of bonded network devices
     */
    public int getBondedNodesNum() {
        return bondedNodesNum;
    }
    
    /**
     * @return discovery ID of the network
     */
    public int getDid() {
        return did;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Number of bonded nodes: " + bondedNodesNum + NEW_LINE);
        strBuilder.append(" Discovery ID of the network: " + did + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
