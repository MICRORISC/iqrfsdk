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

package com.microrisc.simply.iqrf.dpa.broadcasting.services;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.di_services.WaitingTimeoutService;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;

/**
 * Service relating to information about processing of executed broadcast request.
 * For SYNCHRONOUS access to broadcast calling.
 * 
 * @author Michal Konopa
 */
public interface BroadcastCallable_sync extends WaitingTimeoutService {
    /**
     * Executes a broadcast. Blocks, until result is present, or the default 
     * waiting timeout has elapsed.
     * @param networkId ID of a target network
     * @param deviceInterface target Device Interface
     * @param methodId ID of a target method
     * @param args method arguments
     * @param methodIdTransformer method ID transformer to use for transformation
     *        of the method ID
     * @return result of the broadcast <br>
     *         {@code null}, if an error has occured during processing of the broadcast
     */
    BroadcastResult broadcast(
            String networkId, Class deviceInterface, Object methodId, Object[] args, 
            MethodIdTransformer methodIdTransformer
    );
    
    /**
     * Same as 
     * {@link BroadcastCallable_sync#broadcast(java.lang.String, java.lang.Class, 
     * java.lang.Object, java.lang.Object[], com.microrisc.simply.di_services.MethodIdTransformer) 
     * broadcast}
     * but Simply will try to find out a method ID transformer himself.
     * @param networkId ID of a target network
     * @param deviceInterface target Device Interface
     * @param methodId ID of a target method
     * @param args method arguments
     * @return result of the broadcast <br>
     *         {@code null}, if an error has occured during processing of the broadcast
     */
    BroadcastResult broadcast(
            String networkId, Class deviceInterface, Object methodId, Object[] args
    );
}
