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

package com.microrisc.simply.iqrf.dpa.broadcasting;

import com.microrisc.simply.ConnectorListener;
import com.microrisc.simply.ConnectorService;
import java.util.UUID;

/**
 * Broadcasting connector service.
 * 
 * @author Michal Konopa
 */
public interface BroadcastingConnectorService extends ConnectorService {
    /**
     * Performs broadcast call, which corresponds to specified network, Device 
     * Interface, called method, and parameters of that method. <br>
     * Result(or error indication) of the performed broadcast call is delivered to 
     * specified broadcasting connector listener after some time. User of 
     * the listener can get the result using returned value (call ID) of this method. 
     * @param listener listener of the broadcast call, which the result to deliver to
     * @param networkId ID of target network
     * @param deviceIface device interface the called method belongs to
     * @param methodId identifier of the called method
     * @param args arguments of the called method
     * @return unique identifier of this broadcast call request
     */
    UUID broadcastCallMethod(
            ConnectorListener listener, String networkId, Class deviceIface, 
            String methodId, Object[] args
    );
    
    /**
     * Like {@link BroadcastingConnectorService#broadcastCallMethod(com.microrisc.simply.ConnectorListener, 
     * java.lang.String, java.lang.Class, java.lang.String, java.lang.Object[])  broadcastCallMethod} 
     * method, but specifies also a maximal time of processing of a called method.
     * @param maxProcTime maximal time of processing of the called method
     */
    UUID broadcastCallMethod(
            ConnectorListener listener, String networkId, Class deviceIface, 
            String methodId, Object[] args, long maxProcTime
    );
    
}
