
package com.microrisc.simply.iqrf.dpa.v210.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v210.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA Device Interface for general LED operations.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface GeneralLED 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        SET,
        GET,
        PULSE
    }
    
    /**
     * Sends method call request for setting the LED to specified state.
     * @param state state to set the LED into
     * @return unique identifier of sent request
     */
    UUID async_set(LED_State state);
    
    /**
     * Sets the LED to specifed state.
     * Synchronous wrapper for {@link #async_set(LED_State.class) async_set} method.
     * @param state to set the LED into
     * @return {@code VoidType} object, if method call has processed allright <br>
     *         {@code null}, if an error has occured during processing
     */
    VoidType set(LED_State state);
    
    
    /**
     * Sends method call request for getting actual state of the LED.  
     * @return unique identifier of sent request
     */
    UUID async_get();
    
    /**
     * Gets actual state of the LED.
     * Synchronous wrapper for {@link #async_getState() async_getState} method.
     * @return actual state of LED <br>
     *         {@code null}, if an error has occurred during processing
     */
    LED_State get();
    
    
    /**
     * Sends method call request for generating one LED pulse.  
     * @return unique identifier of sent request
     */
    UUID async_pulse();
    
    /**
     * Generates one LED pulse.
     * Synchronous wrapper for {@link #pulse() pulse} method.
     * @return {@code VoidType} object, if method call has processed allright <br>
     *         {@code null}, if an error has occured during processing
     */
    VoidType pulse();
}
