
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Encapsulates addressing information of IQMesh Coordinator.
 * 
 * @author Michal Konopa
 */
public final class AddressingInfo {
    /** Number of bonded network nodes. */ 
    private final int bondedNodesNum;
    
    
    /**
     * Creates new {@code AddressingInfo} object.
     * @param bondedNodesNum number of bonded network nodes 
     */
    public AddressingInfo(int bondedNodesNum ) {
        this.bondedNodesNum = bondedNodesNum;
    }

    /**
     * @return number of bonded network devices
     */
    public int getBondedNodesNum() {
        return bondedNodesNum;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Number of bonded nodes: " + bondedNodesNum + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
