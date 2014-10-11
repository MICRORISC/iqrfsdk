

package com.microrisc.simply.di_services;

import java.util.UUID;

/**
 * Service relating to additional information returned from DI method calls.
 * For ASYNCHRONOUS access to DI method calling.
 * 
 * @author Michal Konopa
 */
public interface AdditionalInfoService_async {
    /**
     * Returns additional information returned from the specified method call.
     * @param callId DO method call about which to get additional information
     * @return additional information returned from the specified method call
     */
    Object getCallResultAdditionalInfo(UUID callId);
}
