

package com.microrisc.simply.iqrf.dpa.broadcasting;

import com.microrisc.simply.ConnectorListener;
import com.microrisc.simply.ConnectorService;
import java.util.UUID;

/**
 * Broadcasting connector service.
 * 
 * @author Michal Konopa
 */
public interface BroadcastingConnectorService extends ConnectorService {
    /**
     * Performs broadcast call, which corresponds to specified network, Device 
     * Interface, called method, and parameters of that method. <br>
     * Result(or error indication) of the performed broadcast call is delivered to 
     * specified broadcasting connector listener after some time. User of 
     * the listener can get the result using returned value (call ID) of this method. 
     * @param listener listener of the broadcast call, which the result to deliver to
     * @param networkId ID of target network
     * @param deviceIface device interface the called method belongs to
     * @param methodId identifier of the called method
     * @param args arguments of the called method
     * @return unique identifier of this broadcast call request
     */
    UUID broadcastCallMethod(
            ConnectorListener listener, String networkId, Class deviceIface, 
            String methodId, Object[] args
    );
    
    /**
     * Like {@link BroadcastingConnectorService#broadcastCallMethod(com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastingConnectorListener, 
     * java.lang.String, java.lang.Class, java.lang.String, java.lang.Object[])  broadcastCallMethod} 
     * method, but specifies also a maximal time of processing of a called method.
     * @param maxProcTime maximal time of processing of the called method
     */
    UUID broadcastCallMethod(
            ConnectorListener listener, String networkId, Class deviceIface, 
            String methodId, Object[] args, long maxProcTime
    );
    
}
