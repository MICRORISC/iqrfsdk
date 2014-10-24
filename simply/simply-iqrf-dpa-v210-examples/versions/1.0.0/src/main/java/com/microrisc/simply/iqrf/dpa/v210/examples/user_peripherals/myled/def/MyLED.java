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

package com.microrisc.simply.iqrf.dpa.v210.examples.user_peripherals.myled.def;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v210.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;

/**
 * MyLED Device Interface.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface MyLED 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer
{
    /**
     * Identifiers of this Device Interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        SET,
        GET,
        PULSE
    }
    
    /**
     * Sets LED to specifed state.
     * @param state to set the LED into
     * @return {@code VoidType} object, if method call has processed allright <br>
     *         {@code null}, if an error has occured during processing
     */
    VoidType set(LED_State state);
    
    /**
     * Gets actual state of LED.
     * @return actual state of LED <br>
     *         {@code null}, if an error has occurred during processing
     */
    LED_State get();
    
    /**
     * Performs pulse on LED.
     * @return {@code VoidType} object, if method call has processed allright <br>
     *         {@code null}, if an error has occured during processing
     */
    VoidType pulse();
}
