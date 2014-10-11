
package com.microrisc.simply.iqrf.dpa.types;

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
