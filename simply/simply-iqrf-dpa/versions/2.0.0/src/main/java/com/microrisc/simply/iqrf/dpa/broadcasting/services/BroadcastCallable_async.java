

package com.microrisc.simply.iqrf.dpa.broadcasting.services;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.di_services.WaitingTimeoutService;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;
import java.util.UUID;

/**
 * Service relating to a process of calling of a broadcast functionality.
 * For ASYNCHRONOUS access to broadcast calling.
 * 
 * @author Michal Konopa
 */
public interface BroadcastCallable_async extends WaitingTimeoutService {
    /**
     * Sends request for a broadcast.
     * @param networkId ID of a target network
     * @param deviceInterface target Device Interface
     * @param methodId ID of a target method
     * @param args method arguments
     * @param methodIdTransformer method ID transformer to use for transformation
     *        of the method ID
     * @return unique identifier of this request <br>
     *         {@code null}, if an error has occured during processing of the request
     */
    UUID sendRequest(
            String networkId, Class deviceInterface, 
            Object methodId, Object[] args, MethodIdTransformer methodIdTransformer
    );
    
    /**
     * Same as 
     * {@link Broadcaster#sendRequest(com.microrisc.simply.Network, java.lang.Class, 
     * java.lang.Object, java.lang.Object[], com.microrisc.simply.di_services.MethodIdTransformer) 
     * sendRequest}
     * but Simply will try to find out method ID transformer himself. 
     * @param networkId ID of a target network
     * @param deviceInterface target Device Interface
     * @param methodId ID of a target method
     * @param args method arguments
     * @return unique identifier of this request <br>
     *         {@code null}, if an error has occured during processing of the request
     */
    UUID sendRequest(
            String networkId, Class deviceInterface, Object methodId, Object[] args
    );
    
    /**
     * Returns result of a broadcast, which is identified by the specified 
     * broadcast request ID. Blocks, until result for specified broadcast is 
     * present, or the specified timeout has elapsed.
     * @param requestId unique identifier of a performed broadcast
     * @param timeout timeout ( in ms ) to wait for the result
     * @return result of the broadcast identified by {@code requestId}
     */
    BroadcastResult getBroadcastResult(UUID requestId, long timeout);
    
    /**
     * Returns result of a broadcast, which is identified by the specified 
     * broadcast request ID. Blocks, until result for specified broadcast is 
     * present, or the <b>default</b> waiting timeout has elapsed.
     * @param requestId unique identifier of a performed broadcast
     * @return result of the broadcast identified by {@code requestId}
     */
    BroadcastResult getBroadcastResultInDefaultWaitingTimeout(UUID requestId);
    
    /**
     * Returns result of a broadcast, which is identified by the specified 
     * broadcast request ID. Returns result immediately, it doesn't wait for 
     * any timeout.
     * @param requestId unique identifier of a performed broadcast
     * @return result of the broadcast identified by {@code requestId}
     */
    BroadcastResult getBroadcastResultImmediately(UUID requestId);
}
