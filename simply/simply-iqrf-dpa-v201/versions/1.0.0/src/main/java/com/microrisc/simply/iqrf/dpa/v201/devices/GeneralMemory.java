
package com.microrisc.simply.iqrf.dpa.v201.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA Device Interface for memory general operations.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface GeneralMemory 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        READ,
        WRITE
    }
    
    /**
     * Sends method call request for reading from peripheral.
     * @param address address to read data from
     * @param length length of the data in bytes
     * @return unique identifier of sent request
     */
    UUID async_read(int address, int length);
    
    /**
     * Reads data from memory.
     * Synchronous wrapper for {@link #async_read(int, int) async_read} method.
     * @param address address to read data from
     * @param length length of the data in bytes
     * @return read data <br>
     *         {@code null}, if an error has occurred during processing
     */
    short[] read(int address, int length);
    
    
    /**
     * Sends method call request for writing data to peripheral.
     * @param address address to write data to
     * @param data actual data to be written to the memory
     * @return unique identifier of sent request
     */
    UUID async_write(int address, short[] data);
    
    /**
     * Writes data to memory.
     * Synchronous wrapper for {@link #async_write(int, short[]) async_write} method.
     * @param address address to write data to
     * @param data actual data to be written to the memory
     * @return <br>
     *        {@code null}, if an error has occurred during processing
     */
    VoidType write(int address, short[] data);
}
