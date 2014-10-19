
package com.microrisc.simply.iqrf.dpa.v210.types;

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
