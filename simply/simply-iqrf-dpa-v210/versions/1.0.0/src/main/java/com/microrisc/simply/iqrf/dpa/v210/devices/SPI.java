
package com.microrisc.simply.iqrf.dpa.v210.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.di_services.DPA_StandardServices;

/**
 * DPA SPI Device Interface.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface SPI 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        WRITE_AND_READ
    }
    
    /**
     * Reads and/or writes data to/from SPI interface.
     * @param readTimeout specifies timeout in 10 ms unit to wait for data to 
     *        be read from SPI after data is (optionally) written. <br>
     *        {@code 0xff} specifies that no data should be read.
     * @param data optional data to be written to the SPI
     * @return optional data read from SPI if the reading was requested and 
     *         data is available. <br>
     *        {@code null}, if an error has occurred during processing
     */
    short[] writeAndRead(int readTimeout, short[] data);
}
