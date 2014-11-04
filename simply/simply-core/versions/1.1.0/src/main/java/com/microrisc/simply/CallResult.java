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

/**
 * Encapsulates information about result of a called Device Interface method 
 * ( which was part of executed call request ).
 * This information consists of these parts: <br>
 * 1. own device object method call result - according to type of return value <br>
 * 2. additional information associated with the result <br>
 *
 * @author Michal Konopa
 */
public final class CallResult {
    /** Plain method result - according to return value type. */
    private final Object result;
    
    /** Additional information associated with the result. */
    private final Object additionalInfo;
    
    
    /**
     * Creates new {@code CallResult} object.
     * @param result method call result
     * @param additionalInfo additional information associated with result 
     */
    public CallResult(Object result, Object additionalInfo) {
        this.result = result;
        this.additionalInfo = additionalInfo;
    }
    
    /**
     * Creates new {@code CallResult} object. It is assumed that no additional 
     * information associated with the result is available 
     * @param result method call result
     */
    public CallResult(Object result) {
        this(result, null);
    }
    
    /**
     * Returns result of method call. Could be {@code null}, if some processing 
     * error has encountered.
     * @return result of method call <br>
     *         {@code null}, if some processing error has encountered
     */
    public Object getMethodCallResult() {
        return result;
    }
    
    /**
     * Returns additional information associated with result.
     * @return additional information associated with result.
     */
    public Object getAdditionalInfo() {
        return additionalInfo;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                ", method result=" + result + 
                ", additional info=" + additionalInfo +
                " }");
    }
}
