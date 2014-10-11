
package com.microrisc.simply.di_services;

import com.microrisc.simply.errors.CallRequestProcessingError;
import java.util.UUID;

/**
 * Service relating to error information of DI method calls.
 * For ASYNCHRONOUS access to Device Interface method calling.
 * 
 * @author Michal Konopa
 */
public interface CallErrorsService_async {
    /**
     * Returns error info about processing of specified method call.
     * @param callId DO method call about its processing to get error info
     * @return error info about processing of specified method call.
     */
    CallRequestProcessingError getCallRequestProcessingError(UUID callId);
}
