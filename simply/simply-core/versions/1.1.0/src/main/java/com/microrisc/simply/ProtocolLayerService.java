

package com.microrisc.simply;

/**
 * Provides services for communcation between connector and network layer. 
 * 
 * @author Michal Konopa
 */
public interface ProtocolLayerService {
    /**
     * Registers specified protocol listener, to which will be the messages sent
     * from the layer.
     * @param listener listener to register
     */
    public void registerListener(ProtocoLayerListener listener);
    
    /**
     * Unregisters currently registered protocol listener, to which are the 
     * messages sent from the layer.
     */
    public void unregisterListener();
    
    /**
     * Sends specified call request to protocol layer.
     * @param request call request to send
     * @throws SimplyException if an error has occured during request sending
     */
    public void sendRequest(CallRequest request) throws SimplyException;
}
