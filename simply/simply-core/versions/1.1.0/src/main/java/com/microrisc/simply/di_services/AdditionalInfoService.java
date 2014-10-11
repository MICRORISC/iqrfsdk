

package com.microrisc.simply.di_services;

/**
 * Service relating to additional information returned from DI method calls.
 * For SYNCHRONOUS and ASYNCHRONOUS access to DI method calling.
 * 
 * @author Michal Konopa
 */
public interface AdditionalInfoService 
    extends 
        ServiceAccessBridge, 
        AdditionalInfoService_async, 
        AdditionalInfoService_sync 
{
}
