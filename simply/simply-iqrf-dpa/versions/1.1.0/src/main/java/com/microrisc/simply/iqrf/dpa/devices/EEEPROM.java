
package com.microrisc.simply.iqrf.dpa.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA EEEPROM Device Interface.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface EEEPROM 
extends DPA_Device, DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        READ,
        WRITE
    }
    
    /**
     * Sends method call request for reading from peripheral.
     * @param blockNumber number of memory block to read from
     * @param length length of the data to read (in bytes), must be equal to the block size
     * @return unique identifier of sent request
     */
    UUID async_read(int blockNumber, int length);
    
    /**
     * Reads in data of specified length from specified block.
     * Synchronous wrapper for {@link #async_read() async_read} method.
     * @param blockNumber number of memory block to read from
     * @param length length of the data to read (in bytes), must be equal to the block size
     * @return read data <br>
     *         {@code null}, if some error has occurred during processing
     */
    Short[] read(int blockNumber, int length);
    
    
    /**
     * Sends method call request for writing to peripheral.
     * @param blockNumber number of memory block to write the data into
     * @param data actual data to be written to the memory, must be equal to the block size
     * @return unique identifier of sent request
     */
    UUID async_write(int blockNumber, short[] data);
    
    /**
     * Writes specified data to specified address.
     * Synchronous wrapper for {@link #async_write() async_write} method.
     * @param blockNumber number of memory block to write the data into
     * @param data actual data to be written to the memory, must be equal to the block size
     * @return {@code VoidType} object, if method call has processed allright <br>
     *         {@code null}, if some error has occurred during processing
     */
    VoidType write(int blockNumber, short[] data);
}
