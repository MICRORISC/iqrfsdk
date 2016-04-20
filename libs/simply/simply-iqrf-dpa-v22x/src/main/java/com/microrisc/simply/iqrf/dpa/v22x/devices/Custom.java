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
import java.util.UUID;

/**
 * Custom Device Interface.
 * <p>
 * Custom is peripheral which is used for all user peripherals when isn't used
 * any mapping of user peripherals.<br>
 * All used user peripherals must be specified in PeripheralDistribution.xml
 * file.
 * <p>
 * @author Martin Strouhal
 */
@DeviceInterface
public interface Custom
        extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {

    /**
     * Identifiers of this Device Interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        SEND
    }

    // ASYNCHRONOUS METHODS
    /**
     * Send custom data to user periheral.
     * <p>
     * @param peripheralId id of peripheral
     * @param cmdId id of command
     * @param data to send
     * @return unique identifier of sent request
     */
    UUID async_send(short peripheralId, short cmdId, short[] data);

    // SYNCHRONOUS WRAPPERS
    /**
     * Synchronous wrapper for
     * {@link #async_send(short, short, short[])  async_send} method.
     * <p>
     * @param peripheralId id of peripheral
     * @param cmdId id of command
     * @param data to send
     * @return response data <br> {@code null} if an error has occurred during
     * processing
     */
    short[] send(short peripheralId, short cmdId, short[] data);

}
