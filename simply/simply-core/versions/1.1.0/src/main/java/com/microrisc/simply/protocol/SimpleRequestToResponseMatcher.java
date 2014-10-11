
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
