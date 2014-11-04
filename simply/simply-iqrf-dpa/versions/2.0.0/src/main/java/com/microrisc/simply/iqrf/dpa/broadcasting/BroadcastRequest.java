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

import com.microrisc.simply.CallRequest;
import java.util.UUID;

/**
 * Encapsulates information about broadcast request.
 * 
 * @author Michal Konopa
 */
public final class BroadcastRequest extends CallRequest {
    
    /**
     * Creates new broadcast request according to specified parameters.
     * @param uid ID of this request
     * @param networkId ID of network
     * @param devInterface device interface
     * @param methodId ID of method, which has been called
     * @param args arguments of the called method
     */
    public BroadcastRequest(
            UUID uid, String networkId, Class devInterface, String methodId, Object[] args
    ) {
        super(uid, networkId, null, devInterface, methodId, args);
    }
}
