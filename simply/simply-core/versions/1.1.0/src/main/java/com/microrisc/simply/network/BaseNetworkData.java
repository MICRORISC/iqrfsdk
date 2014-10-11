
package com.microrisc.simply.network;

import com.microrisc.simply.NetworkData;
import java.util.Arrays;

/**
 * Base class of network data exchanged between protocol and network layers.
 * 
 * @author Michal Konopa
 */
public class BaseNetworkData implements NetworkData {
    /** Effective data. */
    protected short[] data;
    
    /** ID of destination network. */
    protected String networkId;
    
    
    /**
     * Creates new network data object.
     * @param data effective data to send
     * @param networkId ID of destination network
     */
    public BaseNetworkData(short[] data, String networkId) {
        this.data = data;
        this.networkId = networkId;
    }

    /**
     * @return effective data
     */
    @Override
    public short[] getData() {
        return data;
    }

    /**
     * @return ID of destination network
     */
    @Override
    public String getNetworkId() {
        return networkId;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "data=" + Arrays.toString(data) +
                ", network ID=" + networkId + 
                " }");
    }
}
