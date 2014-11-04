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
import java.util.UUID;

/**
 * Service relating to information about processing of executed call request.
 * For ASYNCHRONOUS access to Device Interface method calling.
 * 
 * @author Michal Konopa
 */
public interface CallRequestProcessingService_async {
    /**
     * Returns state of processing of a call request as specified by {@code callId}.
     * @param callId ID of a call request, whose processing info to return
     * @return state of processing of call request as specified by {@code callId}.
     */
    CallRequestProcessingState getCallRequestProcessingState(UUID callId);
    
    /**
     * Cancels processing of a call request as specified by {@code callId}.
     * @param callId ID of a call request whose processing to cancell
     */
    void cancelCallRequest(UUID callId);
}
