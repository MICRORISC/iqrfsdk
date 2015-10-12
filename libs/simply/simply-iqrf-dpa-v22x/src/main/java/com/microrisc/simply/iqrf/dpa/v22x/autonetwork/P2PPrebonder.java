/*
 * Copyright 2014 MICRORISC s.r.o..
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

package com.microrisc.simply.iqrf.dpa.v22x.autonetwork;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.types.VoidType;

/**
 * For sending P2P packet to allow prebonding.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface P2PPrebonder 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        @P2PSenderMethodId
        SEND_PREBONDING_DATA
    }
    
    /**
     * Sends P2P prebonding data.
     * @param prebondingData prebonding data to send
     * @return {@code VoidType} object, if method call has processed allright <br>
     *         {@code null} if an error has occured during processing
     */
    VoidType sendPrebondingData(short[] prebondingData);
}
