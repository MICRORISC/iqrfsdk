

package com.microrisc.simply.di_services;

/**
 * Service relating to error information of DI method calls.
 * For SYNCHRONOUS and ASYNCHRONOUS access to Device Interface method calling.
 * 
 * @author Michal Konopa
 */
public interface CallErrorsService 
    extends 
        ServiceAccessBridge, 
        CallErrorsService_async, 
        CallErrorsService_sync 
{
}
