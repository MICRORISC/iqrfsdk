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

import java.util.LinkedList;
import java.util.List;

/**
 * Stores information about discovered nodes.
 * 
 * @author Michal Konopa
 */
public final class DiscoveredNodes {
    private final IntegerFastQueryList discoveredNodesList;
    
    
    /**
     * Creates new discovered nodes object. 
     * @param discoveredNodesList list of IDs of discovered nodes
     * @throws IllegalArgumentException if nodes list is {@code null}
     */
    public DiscoveredNodes(List<Integer> discoveredNodesList) {
        this.discoveredNodesList = new IntegerFastQueryList(discoveredNodesList);
    }
    
    /**
     * Returns number of all discovered nodes.
     * @return number of all discovered nodes
     */
    public int getNodesNumber() {
        return discoveredNodesList.getSize();
    }
    
    /**
     * Returns indication, wheather node of specified address is discovered or not.
     * @param nodeAddress address of node to examine
     * @return {@code true} if node with specified address is discovered
     *         {@code false}, otherwise
     */
    public boolean isDiscovered(int nodeAddress) {
        return discoveredNodesList.isPresent(nodeAddress);
    }
    
    /**
     * Returns list of discovered nodes.
     * @return list of discovered nodes.
     */
    public List<Integer> getList() {
        List<Integer> listToReturn = new LinkedList<>();
        for (Integer nodeId : discoveredNodesList.getList()) {
            listToReturn.add(nodeId);
        }
        return listToReturn;
    }
    
    public String discoveredNodesListToString() {
        return discoveredNodesList.membersListToString();
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Discovered nodes: " + discoveredNodesListToString() + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
