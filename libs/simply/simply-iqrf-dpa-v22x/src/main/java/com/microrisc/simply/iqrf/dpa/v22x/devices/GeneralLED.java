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

package com.microrisc.simply.iqrf.dpa.v22x.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v22x.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA Device Interface for general LED operations.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if an error has occurred during processing
 * of this method.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface GeneralLED 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        SET,
        GET,
        PULSE
    }
    
    
    // ASYNCHRONOUS METHODS
    
    /**
     * Sends method call request for setting the LED to specified state.
     * @param state state to set the LED into
     * @return unique identifier of sent request
     */
    UUID async_set(LED_State state);
    
    /**
     * Sends method call request for getting actual state of the LED.  
     * @return unique identifier of sent request
     */
    UUID async_get();
    
    /**
     * Sends method call request for generating one LED pulse.  
     * @return unique identifier of sent request
     */
    UUID async_pulse();
    
    
    
    // SYNCHRONOUS WRAPPERS
    
    /**
     * Synchronous wrapper for {@link 
     * #async_set(com.microrisc.simply.iqrf.dpa.v220.types.LED_State) async_set} 
     * method.
     * @param state to set the LED into
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType set(LED_State state);
    
    /**
     * Synchronous wrapper for {@link #async_get() async_get} method.
     * @return actual state of LED
     */
    LED_State get();
    
    /**
     * Synchronous wrapper for {@link #async_pulse() async_pulse} method.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType pulse();
}
