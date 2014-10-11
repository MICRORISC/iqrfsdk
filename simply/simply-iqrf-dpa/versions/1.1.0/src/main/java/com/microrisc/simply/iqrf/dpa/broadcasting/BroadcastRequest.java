

package com.microrisc.simply.iqrf.dpa.broadcasting;

import com.microrisc.simply.CallRequest;
import java.util.UUID;

/**
 * Encapsulates information about broadcast request.
 * 
 * @author Michal Konopa
 */
public final class BroadcastRequest extends CallRequest {
    
    /**
     * Creates new broadcast request according to specified parameters.
     * @param uid ID of this request
     * @param networkId ID of network
     * @param devInterface device interface
     * @param methodId ID of method, which has been called
     * @param args arguments of the called method
     */
    public BroadcastRequest(
            UUID uid, String networkId, Class devInterface, String methodId, Object[] args
    ) {
        super(uid, networkId, null, devInterface, methodId, args);
    }
}
