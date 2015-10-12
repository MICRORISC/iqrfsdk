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
import com.microrisc.simply.iqrf.dpa.v22x.types.BaudRate;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA UART Device Interface.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if an error has occurred during processing
 * of this method.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface UART 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        OPEN,
        CLOSE,
        WRITE_AND_READ
    }
    
    
    // ASYNCHRONOUS METHODS
    
    /**
     * Sends method call request for opening UART at specified baudrate and 
     * flushing internal read and write buffers.
     * @param baudRate baudrate to use
     * @return unique identifier of sent request
     */
    UUID async_open(BaudRate baudRate);
    
    /**
     * Sends method call request for closing UART interface.
     * @return unique identifier of sent request
     */
    UUID async_close();
    
    /**
     * Sends method call request for reading and/or writing data to/from UART interface.
     * @param readTimeout specifies timeout in 10 ms unit to wait for data to 
     *        be read from UART after data is (optionally) written. <br>
     *        {@code 0xff} specifies that no data should be read.
     * @param data optional data to be written to the UART
     * @return unique identifier of sent request
     */
    UUID async_writeAndRead(int readTimeout, short[] data);
    
    
    
    // SYNCHRONOUS WRAPPERS
    
    /**
     * Synchronous wrapper for {@link 
     * #async_open(com.microrisc.simply.iqrf.dpa.v220.types.BaudRate)  async_open} method.
     * @param baudRate baudrate to use
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType open(BaudRate baudRate);
    
    /**
     * Synchronous wrapper for {@link #async_close() async_close} method.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType close();
    
    /**
     * Synchronous wrapper for {@link #async_writeAndRead(int, short[]) async_writeAndRead} method.
     * @param readTimeout specifies timeout in 10 ms unit to wait for data to 
     *        be read from UART after data is (optionally) written. <br>
     *        {@code 0xff} specifies that no data should be read.
     * @param data optional data to be written to the UART
     * @return optional data read from UART if the reading was requested and 
     *         data is available.
     */
    short[] writeAndRead(int readTimeout, short[] data);
}
