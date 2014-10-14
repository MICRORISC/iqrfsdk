

package com.microrisc.simply;

import com.microrisc.simply.network.NetworkLayerException;

/**
 * Services for low level access to the underlaying network.
 * 
 * @author Michal Konopa
 */
public interface NetworkLayerService {
    /**
     * Registers specified network listener, to which sent the data from 
     * network layer.
     * @param listener listener to register
     */
    void registerListener(NetworkLayerListener listener);
    
    /**
     * Unregisters currently registered network listener, which the data are 
     * sent to from the network.
     */
    void unregisterListener();
    
    /**
     * Sends specified network data to the network.
     * @param data data to send
     * @throws NetworkLayerException if an error has occured during sending
     *         the data
     */
    void sendData(NetworkData data) throws NetworkLayerException;
}
