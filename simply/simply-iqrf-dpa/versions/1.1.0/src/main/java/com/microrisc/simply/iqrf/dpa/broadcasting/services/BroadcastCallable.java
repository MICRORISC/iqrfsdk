

package com.microrisc.simply.iqrf.dpa.broadcasting.services;

/**
 * Service relating to a process of calling of a broadcast functionality.
 * For SYNCHRONOUS and ASYNCHRONOUS access to broadcast calling.
 * 
 * @author Michal Konopa
 */
public interface BroadcastCallable 
    extends 
        BroadcastCallable_async, 
        BroadcastCallable_sync
{    
}
