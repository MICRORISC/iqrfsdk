

package com.microrisc.simply.di_services;

/**
 * Standard services for Device Interfaces.
 * 
 * @author Michal Konopa
 */
public interface StandardServices
    extends
        AsyncCallable,
        CallRequestProcessingService, 
        CallErrorsService, 
        AdditionalInfoService
{
}
