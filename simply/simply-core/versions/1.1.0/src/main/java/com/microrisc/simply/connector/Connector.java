

package com.microrisc.simply.connector;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.ManageableObject;

/**
 * Base connector services.
 * 
 * @author Michal Konopa
 */
public interface Connector extends ManageableObject, ConnectorService {
    /**
     * Sets maximal time period ( in ms ), during which the requests can be 'idle'. 
     * When a request is idle for specific time period, it means:
     * - no call result has arrived for the request
     * - no client queried the request
     * for that period of time.
     * @param idleTime maximal time period, during which requests can be idle
     */
    void setMaxCallRequestIdleTime(long idleTime);
    
    /**
     * Returns maximal time period ( in ms ), during which the requests can be 'idle'. 
     * When a request is idle for specific time period, it means:
     * - no call result has arrived for the request
     * - no client queried the request
     * for that period of time.
     * @return maximal time period, during which requests can be idle
     */
    @Override
    long getMaxCallRequestIdleTime();
}
