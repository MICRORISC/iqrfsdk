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

import java.util.List;

/**
 * Stores information about bonded nodes.
 * 
 * @author Michal Konopa
 */
public final class BondedNodes {
    private final IntegerFastQueryList bondedNodesList;
    
    
    /**
     * Creates new bonded nodes object. 
     * @param bondedNodesList list of IDs of bonded nodes
     * @throws IllegalArgumentException if nodes list is {@code null}
     */
    public BondedNodes(List<Integer> bondedNodesList) {
        this.bondedNodesList = new IntegerFastQueryList(bondedNodesList);
    }
    
    /**
     * Returns number of all bonded nodes.
     * @return number of all bonded nodes
     */
    public int getNodesNumber() {
        return bondedNodesList.getSize();
    }
    
    /**
     * Returns indication, wheather node of specified address is bonded or not.
     * @param nodeAddress address of node to examine
     * @return {@code true} if node with specified address is bonded
     *         {@code false}, otherwise
     */
    public boolean isBonded(int nodeAddress) {
        return bondedNodesList.isPresent(nodeAddress);
    }
    
    /**
     * Returns list of bonded nodes.
     * @return list of bonded nodes.
     */
    public List<Integer> getList() {
        return bondedNodesList.getList();
    }
    
    public String bondedNodesListToString() {
        return bondedNodesList.membersListToString();
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Bonded nodes: " + bondedNodesListToString() + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
