
package com.microrisc.simply.iqrf.dpa.types;

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
