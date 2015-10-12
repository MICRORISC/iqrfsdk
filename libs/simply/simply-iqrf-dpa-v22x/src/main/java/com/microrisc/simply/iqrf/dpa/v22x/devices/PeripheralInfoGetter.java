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
import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralEnumeration;
import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralInfo;
import java.util.UUID;

/**
 * Device interface for getting information about peripherals from underlaying
 * network nodes.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if an error has occurred during processing
 * of this method.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface PeripheralInfoGetter 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        GET_PERIPHERAL_ENUMERATION,
        GET_PERIPHERAL_INFO,
        GET_MORE_PERIPHERALS_INFO
    }
    
    
    // ASYNCHRONOUS METHODS
    
    /**
     * Sends method call request for peripheral enumeration available on this node.
     * @return unique identifier of sent request 
     */
    UUID async_getPeripheralEnumeration();
    
    /**
     * Sends method call requests for information about specified peripheral.
     * @param peripheralNumber number of peripheral, whose info is requested
     * @return unique identifier of sent request
     */
    UUID async_getPeripheralInfo(int peripheralNumber);
    
    /**
     * Sends method call requests for information about sequence of more 
     * peripherals, starting with specified peripheral number.
     * @param startPeripheralNumber starting peripheral's number
     * @return unique identifier of sent request 
     */
    UUID async_getMorePeripheralsInfo(int startPeripheralNumber);
    
    
    
    // SYNCHRONOUS WRAPPERS
    
    /**
     * Synchronous wrapper for {@link #async_getPeripheralEnumeration() 
     * async_getPeripheralEnumeration} method.
     * @return information about peripheral enumeration
     */
    PeripheralEnumeration getPeripheralEnumeration();
    
    /**
     * Synchronous wrapper for {@link #async_getPeripheralInfo(int) 
     * async_getPeripheralInfo} method.
     * @param peripheralNumber number of peripheral, whose info is requested
     * @return information about specified peripheral
     */
    PeripheralInfo getPeripheralInfo(int peripheralNumber);
    
    /**
     * Synchronous wrapper for {@link #async_getMorePeripheralsInfo(int) 
     * async_getMorePeripheralsInfo} method.
     * @param startPeripheralNumber starting peripheral's number
     * @return information about peripherals
     */
    PeripheralInfo[] getMorePeripheralsInfo(int startPeripheralNumber);
}
