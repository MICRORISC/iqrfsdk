

package com.microrisc.simply.di_services;

/**
 * Service relating to additional information returned from DI method calls.
 * For SYNCHRONOUS access to DI method calling.
 * 
 * @author Michal Konopa
 */
public interface AdditionalInfoService_sync {
    /**
     * Returns additional information returned from the last executed method call.
     * @return additional information returned from the last executed method call
     */
    Object getCallResultAdditionalInfoOfLastCall();
}
