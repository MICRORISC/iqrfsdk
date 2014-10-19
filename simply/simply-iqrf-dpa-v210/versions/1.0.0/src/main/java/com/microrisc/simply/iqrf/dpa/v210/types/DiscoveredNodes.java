
package com.microrisc.simply.iqrf.dpa.v210.types;

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
