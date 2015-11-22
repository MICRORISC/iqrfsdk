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
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA EEEPROM Device Interface.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if an error has occurred during processing
 * of this method.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface EEEPROM 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        READ,
        WRITE,
        EXTENDED_READ,
        EXTENDED_WRITE
    }
    
    
    // ASYNCHRONOUS METHODS
    
    /**
     * Sends method call request for reading from peripheral.
     * @param blockNumber number of (zero based) block to read from
     * @param length length of the data to read (in bytes), must be equal to the block size
     * @return unique identifier of sent request
     */
    UUID async_read(int blockNumber, int length);
    
    /**
     * Sends method call request for writing to peripheral.
     * @param blockNumber number of (zero based) block to write the data into
     * @param data actual data to be written to the memory, its length must be 
     *             equal to the block size
     * @return unique identifier of sent request
     */
    UUID async_write(int blockNumber, short[] data);
    
    /**
     * Sends method call request for reading from peripheral.
     * @param address (physical) to read data from. The address range for DCTR-7x
     *        is 0x0000-0x7FFF or 0x0700-0x7FFF at [N] or at [C] devices respectively.
     * @param length of the data to read in bytes. Allowed range is 0-54 bytes. 
     *        Reading behind maximum address range is undefined.
     * @return unique identifier of sent request
     */
    UUID async_extendedRead(int address, int length);
    
    /**
     * Sends method call request for writing to peripheral.
     * @param address (physical) to write data to. The address range for DCTR-7x
     *        is 0x0000-0x7FFF or 0x0700-0x7FFF at [N] or at [C] devices respectively.
     * @param data actual data to be written to the memory. Length of the data 
     *        to write in bytes. Allowed range is 1-54 bytes. Writing to multiple
     *        adjacent 64 byte pages of the EEPROM chip or behind maximum address
     *        range by one extended write command is unsupported and undefined.
     * @return unique identifier of sent request
     */
    UUID async_extendedWrite(int address, short[] data);
    
    
    
    // SYNCHRONOUS WRAPPERS
    
    /**
     * Synchronous wrapper for {@link #async_read(int, int) async_read} method.
     * @param blockNumber number of (zero based) block to read from
     * @param length length of the data to read (in bytes), must be equal to the block size
     * @return read data
     */
    short[] read(int blockNumber, int length);
    
    /**
     * Synchronous wrapper for {@link #async_write(int, short[])  async_write} method.
     * @param blockNumber number of (zero based) block to write the data into
     * @param data actual data to be written to the memory, its length must be 
     *             equal to the block size
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType write(int blockNumber, short[] data);
    
        /**
     * Synchronous wrapper for {@link #async_read(int, int) async_read} method.
     * @param address number of (zero based) block to read from
     * @param length length of the data to read (in bytes), must be equal to the block size
     * @return read data
     */
    short[] extendedRead(int address, int length);
    
    /**
     * Synchronous wrapper for {@link #async_write(int, short[])  async_write} method.
     * @param address number of (zero based) block to write the data into
     * @param data actual data to be written to the memory, its length must be 
     *             equal to the block size
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType extendedWrite(int address, short[] data);
}
