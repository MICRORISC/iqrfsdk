
package com.microrisc.simply;

/**
 * Listener of data, which comes from network layer.
 * 
 * @author Michal Konopa
 */
public interface NetworkLayerListener {
    /**
     * Will be called, when specified data come from the network.
     * @param data data from the network
     */
    public void onGetData(NetworkData data);
}
