

package com.microrisc.simply.iqrf.dpa;

import com.microrisc.simply.Simply;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;


/**
 * Extended Simply interface for access to DPA functionality.
 * 
 * @author Michal Konopa
 */
public interface DPA_Simply extends Simply {
    /**
     * Returns access to broadcast services.
     * @return access to broadcast services
     */
    BroadcastServices getBroadcastServices();
    
    /**
     * Returns manager of asynchronous messaging.
     * @return manager of asynchronous messaging
     */
    AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> 
        getAsynchronousMessagingManager();
}
