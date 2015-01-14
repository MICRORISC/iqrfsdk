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

package com.microrisc.simply;

import java.util.UUID;


/**
 * Connector services. Main intended consumers of that services are Device Objects. 
 * 
 * @author Michal Konopa
 */
public interface ConnectorService {
    /** Unlimited maximal processing time for a call request. */
    long UNLIMITED_MAXIMAL_PROCESSING_TIME = -1;
    
    
    /**
     * Performs action on underlaying network device, which corresponds to 
     * specified device object, called method, and parameters of that method.<br>
     * Result(or error indication) of the performed action is delivered to 
     * specified device object after some time. User of the Device Object can
     * get the result using returned value(call ID) of this method. 
     * @param deviceObject DO on which method has been called
     * @param deviceIface device interface the called method belongs to
     * @param methodId identifier of the called method
     * @param args arguments of the called method
     * @return unique identifier of this method call request
     */
    UUID callMethod(ConnectedDeviceObject deviceObject, Class deviceIface,
            String methodId, Object[] args
    );
    
    /**
     * Like {@link ConnectorService#callMethod(com.microrisc.simply.ConnectedDeviceObject, 
     * java.lang.Class, java.lang.String, java.lang.Object[] ) callMethod} method,
     * but specifies also a maximal time of processing of a called method.
     * @param maxProcTime maximal time of processing of the called method
     */
    UUID callMethod(ConnectedDeviceObject deviceObject, Class deviceIface,
            String methodId, Object[] args, long maxProcTime
    );
    
    /**
     * Sets maximal processing time for specified call request.
     * @param requestId ID of a call request, whose processing time to set
     * @param maxProcTime processing time of the specified request 
     */
    void setCallRequestMaximalProcessingTime(UUID requestId, long maxProcTime);
    
    /**
     * Returns information about processing of executed call request as specified
     * by {@code callId}.
     * @param requestId ID of a call request, which to return information for
     * @return information about processing of executed call request as specified
     *         by {@code callId}.
     */
    CallRequestProcessingInfo getCallRequestProcessingInfo(UUID requestId);
    
    /**
     * Cancels processing of a call request as specified by {@code callId}.
     * @param requestId ID of a call request whose processing to cancell
     */
    void cancelCallRequest(UUID requestId);
    
    /**
     * Returns maximal time ( in ms ), during which the call requests can be present
     * at a connector without any interaction. When a request is idle for specific
     * time period, it means:
     * - no call result has arrived for the request
     * - no client queried the request
     * for that period of time.
     * 
     * Connector Service must garantee that idle request will not be
     * silently cancelled and removed from the connector during this period of time.
     * 
     * @return maximal idle time period for the requests present at a connector
     */
    long getCallRequestsMaximalIdleTime();
}
