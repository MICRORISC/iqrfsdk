
package com.microrisc.simply.di_services;

import com.microrisc.simply.errors.CallRequestProcessingError;

/**
 * Service relating to error information of DI method calls.
 * For SYNCHRONOUS access to DI method calling.
 * 
 * @author Michal Konopa
 */
public interface CallErrorsService_sync {
    /**
     * Returns error information relating to the last processed method call.
     * @return error information relating to the last processed method call.
     */
    CallRequestProcessingError getCallRequestProcessingErrorOfLastCall();
}
