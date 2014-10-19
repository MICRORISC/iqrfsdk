
package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * Encapsulates information about result of discovery process of IQMesh network.
 * 
 * @author Michal Konopa
 */
public final class DiscoveryResult {
    /** Number of discovered network nodes. */
    private final int discoveredNodesNum;
    
    
    /**
     * Creates new {@code DiscoveryResult} object.
     * @param discoveredNodesNum number of discovered network nodes
     */
    public DiscoveryResult(int discoveredNodesNum) {
        this.discoveredNodesNum = discoveredNodesNum;
    }

    /**
     * @return number of discovered network nodes
     */
    public int getDiscoveredNodesNum() {
        return discoveredNodesNum;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Discovered network nodes: " + discoveredNodesNum + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
