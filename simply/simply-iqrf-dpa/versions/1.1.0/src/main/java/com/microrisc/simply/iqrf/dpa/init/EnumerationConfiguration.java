
package com.microrisc.simply.iqrf.dpa.init;

/**
 * Configuration of enumeration process.
 * 
 * @author Michal Konopa
 */
public class EnumerationConfiguration {
    /** Default value of number of attempts of getting peripherals from node. */
    public static int DEFAULT_GET_PER_ATTEMPTS_NUM = 3;
    
    /** Default value of timeout [in ms] of operation of getting peripherals from node. */
    public static int DEFAULT_GET_PER_TIMEOUT = 10000;
    
    
    /** Number of attempts of getting peripherals from node. */
    private int getPerAttemptsNum;

    /** Timeout [in ms] of operation of getting peripherals from node. */
    private long getPerTimeout;
    
    
    private int checkGetPerAttemptsNum(int getPerAttemptsNum) {
        if (getPerAttemptsNum <= 0) {
            throw new IllegalArgumentException(
                "Value of number of attempts of getting peripherals from node must be positive"
            );
        }
        return getPerAttemptsNum;
    } 
    
    private long checkGetPerTimeout(long getPerTimeout) {
        if (getPerTimeout < 0) {
            throw new IllegalArgumentException(
                "Value of timeout [in ms] of operation of getting peripherals from node must be nonnegative"
            );
        }
        return getPerTimeout;
    }
    
    
    /**
     * Creates new object of enumeration configuration.
     * @param getPerAttemptsNum number of attempts of getting peripherals from node
     * @param getPerTimeout timeout [in ms] of operation of getting peripherals from node
     * @param getBondedNodesAttemptsNum number of attempts of getting bonded nodes from coordinator
     * @param getBondedNodesTimeout timeout [in ms] of operation of getting bonded nodes from coordinator
     */
    public EnumerationConfiguration(int getPerAttemptsNum, long getPerTimeout) {
        this.getPerAttemptsNum = checkGetPerAttemptsNum(getPerAttemptsNum);
        this.getPerTimeout = checkGetPerTimeout(getPerTimeout);
    }
    
    /**
     * @return number of attempts of getting peripherals from node
     */
    public int getPerAttemptsNum() {
        return getPerAttemptsNum;
    }

    /**
     * @return timeout [in ms] of operation of getting peripherals from node
     */
    public long getPerTimeout() {
        return getPerTimeout;
    }
    
}
