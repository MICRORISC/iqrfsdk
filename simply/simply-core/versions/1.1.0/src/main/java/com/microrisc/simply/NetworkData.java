
package com.microrisc.simply;

/**
 * Network data exchanged between protocol and network layers.
 * 
 * @author Michal Konopa
 */
public interface NetworkData {
    
    /**
     * Returns effective data.
     * @return effective data
     */
    short[] getData();

    /**
     * Returns ID of source/destination network.
     * @return ID of source/destination network
     */
    public String getNetworkId();
}
