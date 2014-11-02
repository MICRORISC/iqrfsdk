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

package com.microrisc.simply.iqrf.dpa.v210.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v210.types.Thermometer_values;
import java.util.UUID;

/**
 * DPA Thermometer Device Interface.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface Thermometer 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        GET
    }
    
    /**
     * Sends method call request for reading on-board thermometer sensor value. 
     * @return unique identifier of sent request
     */
    UUID async_get();
    
    /**
     * Reads on-board thermometer sensor value.
     * Synchronous wrapper for {@link #async_get() async_get} method.
     * @return actual state of Thermometer<br>
     *         {@code null} if an error has occurred during processing
     */
    Thermometer_values get();
}
