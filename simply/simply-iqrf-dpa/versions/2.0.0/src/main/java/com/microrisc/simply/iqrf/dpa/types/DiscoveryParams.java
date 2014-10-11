
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Encapsulates information about parameters of discovery process of IQMesh network.
 * 
 * @author Michal Konopa
 */
public final class DiscoveryParams {
    /** TX Power used for discovery. */
    private final int txPower;
    
    /** 
     * Specifies maximum node address to be part of the discovery process. This 
     * feature allows to split all node devices into tho parts: [1] devices having 
     * address from 0 to MaxAddr will be part of the discovery process thus they 
     * become routers, [2] devices having address from MaxAddr+1 to 239 will not 
     * be routers. See IQRF OS documentation for more information. 
     */
    private final int maxAddr;
    
    
    /** TX power lower bound. */
    public static final int TXPOWER_LOWER_BOUND = 0x00; 
    
    /** TX power upper bound. */
    public static final int TXPOWER_UPPER_BOUND = 0x07; 
    
    
    private static int checkTxPower(int txPower) {
        if ( (txPower < TXPOWER_LOWER_BOUND) || (txPower > TXPOWER_UPPER_BOUND) ) {
            throw new IllegalArgumentException("TX power out of bounds");
        }
        return txPower;
    }
    
    /** Max. node address lower bound. */
    public static final int MAXADDR_LOWER_BOUND = 0x00; 
    
    /** Max. node address upper bound. */
    public static final int MAXADDR_UPPER_BOUND = 0x07; 
    
    
    private static int checkMaxNodeAddress(int maxNodeAddress) {
        if ( (maxNodeAddress < MAXADDR_LOWER_BOUND) || (maxNodeAddress > MAXADDR_UPPER_BOUND) ) {
            throw new IllegalArgumentException("TX power out of bounds");
        }
        return maxNodeAddress;
    }
    
    /**
     * Creates new object, which encapsules discovery parameters.
     * @param txPower TX Power used for discovery.
     * @param maxAddr Specifies maximum node address to be part of the discovery 
     * process.
     * @throws IllegalArgumentException if: <br> 
     *         specified TX power is out of [{@code TXPOWER_LOWER_BOUND}..{@code TXPOWER_UPPER_BOUND}] interval <br> 
     *         specified node address is out of [{@code MAXADDR_LOWER_BOUND}..{@code MAXADDR_UPPER_BOUND}] interval
     */
    public DiscoveryParams(int txPower, int maxAddr) {
        this.txPower = checkTxPower(txPower);
        this.maxAddr = checkMaxNodeAddress(maxAddr);
    }

    /**
     * @return TX Power used for discovery
     */
    public int getTxPower() {
        return txPower;
    }

    /**
     * @return maximum number of zones that will be created during discovery process.
     */
    public int getMaxAddr() {
        return maxAddr;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" TX power: " + txPower + NEW_LINE);
        strBuilder.append(" Maximum address number: " + maxAddr + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
