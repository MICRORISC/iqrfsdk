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

package com.microrisc.simply.protocol;

import com.microrisc.simply.BaseCallResponse;
import com.microrisc.simply.CallRequest;

/**
 * Simple comparator class for decision, if a request match with a response.
 * 
 * @author Michal Konopa
 */
public class SimpleRequestToResponseMatcher {
    // Suppress default constructor for noninstantiability
    private SimpleRequestToResponseMatcher() {
        throw new AssertionError();
    }
    
    /**
     * Returns {@code true}, if the specified response is the response on specified 
     * request. Otherwise returns {@code false}.
     * @param request call request
     * @param response response
     * @return {@code true} if the specified response is the response on 
     *                      specified request <br>
     *         {@code false} otherwise 
     */
    static public boolean match(CallRequest request, BaseCallResponse response) {
        BaseCallResponse.MethodMessageSource respSource = response.getMessageSource();
        
        if ( !request.getNetworkId().equals(respSource.getNetworkId())) {
            return false;
        }
        
        if ( !request.getNodeId().equals(respSource.getNodeId())) {
            return false;
        }
        
        if ( !request.getDeviceInterface().equals(respSource.getDeviceInterface())) {
            return false;
        }
        
        if ( !request.getMethodId().equals(respSource.getMethodId())) {
            return false;
        }
        
        return true;
    }
}
