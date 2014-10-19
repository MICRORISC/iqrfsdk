
package com.microrisc.simply.iqrf.dpa.v210.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v210.types.BaudRate;
import com.microrisc.simply.iqrf.types.VoidType;

/**
 * DPA UART Device Interface.
 * All methods return {@code null} if an error has occurred during processing
 * of corresponding method call.
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
    
    /**
     * Opens UART at specified baudrate and flushes internal read and write buffers.
     * @param baudRate baudrate to use
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType open(BaudRate baudRate);
    
    /**
     * Closes UART interface.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType close();
    
    /**
     * Reads and/or writes data to/from UART interface.
     * @param readTimeout specifies timeout in 10 ms unit to wait for data to 
     *        be read from UART after data is (optionally) written. <br>
     *        {@code 0xff} specifies that no data should be read.
     * @param data optional data to be written to the UART
     * @return optional data read from UART if the reading was requested and 
     *         data is available.
     */
    short[] writeAndRead(int readTimeout, short[] data);
}
