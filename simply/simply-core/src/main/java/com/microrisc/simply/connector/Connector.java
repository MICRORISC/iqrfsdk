/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
     * Sets maximal time period ( in ms ), during which the call requests can be 'idle'. 
     * When a request is idle for specific time period, it means:
     * - no call result has arrived for the request
     * - no client queried the request
     * for that period of time.
     * @param idleTime maximal time period, during which requests can be idle
     */
    void setCallRequestsMaximalIdleTime(long idleTime);
    
    /**
     * Returns maximal time period ( in ms ), during which requests can be 'idle'. 
     * When a request is idle for specific time period, it means:
     * - no call result has arrived for the request
     * - no client queried the request
     * for that period of time.
     * @return maximal time period, during which requests can be idle
     */
    @Override
    long getCallRequestsMaximalIdleTime();
}
