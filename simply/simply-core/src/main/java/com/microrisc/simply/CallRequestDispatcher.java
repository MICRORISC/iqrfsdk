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
 * Dispatcher of call requests.
 * 
 * @author Michal Konopa
 */
public interface CallRequestDispatcher {
    /**
     * Dispatches method call as specified by its method and its arguments and 
     * returns unique identifier of that call.
     * @param methodId ID of called method
     * @param args arguments of method
     * @return unique identifier of the method call <br>
     *         {@code null}, if an error has occured during this call processing
     */
    public UUID dispatchCall(String methodId, Object[] args);
    
    /**
     * Dispatches method call as specified by its method and its arguments and 
     * returns unique identifier of that call. Specifies timeout for processing
     * the method call.
     * @param methodId ID of called method
     * @param args arguments of method
     * @param timeout timeout of method call processing
     * @return unique identifier of the method call <br>
     *         {@code null}, if an error has occured during this call processing
     */
    public UUID dispatchCall(String methodId, Object[] args, long timeout);
}
