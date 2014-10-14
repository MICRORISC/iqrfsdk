

package com.microrisc.simply;

import java.util.UUID;

/**
 * Dispatcher of call requests.
 * 
 * @author Michal Konopa
 */
public interface CallRequestDispatcher {
    /**
     * Dispatches method call as specified by its method and its arguments and 
     * returns unique identifier of that call.
     * @param methodId ID of called method
     * @param args arguments of method
     * @return unique identifier of the method call
     *         {@code null}, if an error has occured during this call processing
     */
    public UUID dispatchCall(String methodId, Object[] args);
    
    /**
     * Dispatches method call as specified by its method and its arguments and 
     * returns unique identifier of that call. Specifies timeout for processing
     * the method call.
     * @param methodId ID of called method
     * @param args arguments of method
     * @param timeout timeout of method call processing
     * @return unique identifier of the method call
     *         {@code null}, if an error has occured during this call processing
     */
    public UUID dispatchCall(String methodId, Object[] args, long timeout);
}
