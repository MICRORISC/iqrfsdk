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
