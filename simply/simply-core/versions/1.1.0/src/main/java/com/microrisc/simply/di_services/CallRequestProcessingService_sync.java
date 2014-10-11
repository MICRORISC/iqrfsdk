
package com.microrisc.simply.di_services;

import com.microrisc.simply.CallRequestProcessingState;

/**
 * Service relating to information about processing of executed call request.
 * For SYNCHRONOUS access to Device Interface method calling.
 * 
 * @author Michal Konopa
 */
public interface CallRequestProcessingService_sync {
    /**
     * Returns call request processing state of last executed call.
     * @return call request processing state of last executed call
     */
    CallRequestProcessingState getCallRequestProcessingStateOfLastCall();
    
    /**
     * Cancels processing of a call request of last executed call.
     */
    void cancelCallRequestOfLastCall();
}
