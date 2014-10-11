
package com.microrisc.simply.protocol;

import com.microrisc.simply.CallRequest;

/**
 * Encapsulate comparing 2 method call request. It is helper class for protocol
 * layer.
 * 
 * @author Michal Konopa
 */
public final class CallRequestComparator {
    /**
     * Compares specified call requests for equality and returns result of this
     * comparison.
     * @param firstRequest first request to compare
     * @param secondRequest second request to compare
     * @return {@code true}, if both requests are equal <br>
     *         {@code false}, otherwise
     */
    static public boolean areEqual(CallRequest firstRequest, CallRequest secondRequest) {
        if ( !(firstRequest.getNetworkId().equals(secondRequest.getNetworkId())) ) {
            return false;
        }
        
        if ( !(firstRequest.getNodeId().equals(secondRequest.getNodeId())) ) {
            return false;
        }
        
        if ( !(firstRequest.getDeviceInterface().equals(secondRequest.getDeviceInterface())) ) {
            return false;
        }
        
        if ( !(firstRequest.getMethodId().equals(secondRequest.getMethodId())) ) {
            return false;
        }
        
        Object[] firstRequestArgs = firstRequest.getArgs();
        Object[] secondRequestArgs = secondRequest.getArgs();
        
        if (firstRequestArgs == null) {
            if (secondRequestArgs == null) {
                return true;
            } else {
                return false;
            }
        }
        
        if (secondRequestArgs == null) {
            return false;
        }
        
        if (firstRequestArgs.length != secondRequestArgs.length) {
            return false;
        }
        
        for (int argId = 0; argId < firstRequestArgs.length; argId++) {
            if ( !(firstRequestArgs[argId].equals(secondRequestArgs[argId]))) {
                return false;
            }
        }
        
        return true;
    }
}
