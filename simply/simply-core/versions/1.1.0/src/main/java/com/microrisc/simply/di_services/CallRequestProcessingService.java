

package com.microrisc.simply.di_services;

/**
 * Service relating to information about processing of executed call request.
 * For SYNCHRONOUS and ASYNCHRONOUS access to Device Interface method calling.
 * 
 * @author Michal Konopa
 */
public interface CallRequestProcessingService 
extends ServiceAccessBridge, 
        CallRequestProcessingService_async, 
        CallRequestProcessingService_sync 
{
}
