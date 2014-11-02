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

package com.microrisc.simply.di_services;

import com.microrisc.simply.CallRequestProcessingState;

/**
 * Service relating to information about processing of executed call request.
 * For SYNCHRONOUS access to Device Interface method calling.
 * 
 * @author Michal Konopa
 */
public interface CallRequestProcessingService_sync {
    /**
     * Returns call request processing state of last executed call.
     * @return call request processing state of last executed call
     */
    CallRequestProcessingState getCallRequestProcessingStateOfLastCall();
    
    /**
     * Cancels processing of a call request of last executed call.
     */
    void cancelCallRequestOfLastCall();
}
