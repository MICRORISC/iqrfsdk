

package com.microrisc.simply.di_services;

import com.microrisc.simply.CallRequestProcessingState;
import java.util.UUID;

/**
 * Service relating to information about processing of executed call request.
 * For ASYNCHRONOUS access to Device Interface method calling.
 * 
 * @author Michal Konopa
 */
public interface CallRequestProcessingService_async {
    /**
     * Returns state of processing of a call request as specified by {@code callId}.
     * @param callId ID of a call request, whose processing info to return
     * @return state of processing of call request as specified by {@code callId}.
     */
    CallRequestProcessingState getCallRequestProcessingState(UUID callId);
    
    /**
     * Cancels processing of a call request as specified by {@code callId}.
     * @param callId ID of a call request whose processing to cancell
     */
    void cancelCallRequest(UUID callId);
}
