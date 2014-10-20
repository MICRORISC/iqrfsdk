
package com.microrisc.simply.iqrf.dpa.v201.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v201.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v201.types.Thermometer_values;
import java.util.UUID;

/**
 * DPA Thermometer Device Interface.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface Thermometer 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        GET
    }
    
    /**
     * Sends method call request for reading on-board thermometer sensor value.  
     * @return unique identifier of sent request
     */
    UUID async_get();
    
    /**
     * Reads on-board thermometer sensor value.
     * Synchronous wrapper for {@link #async_getState() async_getState} method.
     * @return actual state of Thermometer<br>
     *         {@code null}, if an error has occurred during processing
     */
    Thermometer_values get();
}
