

package com.microrisc.simply.iqrf.dpa.broadcasting.services;

import com.microrisc.simply.di_services.StandardServices;
import com.microrisc.simply.iqrf.dpa.di_services.HwProfileService;

/**
 * Broadcast services.
 * 
 * @author Michal Konopa
 */
public interface BroadcastServices 
    extends
        StandardServices,
        BroadcastCallable,
        HwProfileService
{    
}
