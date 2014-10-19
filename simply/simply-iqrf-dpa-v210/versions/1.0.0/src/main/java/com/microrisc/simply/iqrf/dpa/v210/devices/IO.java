
package com.microrisc.simply.iqrf.dpa.v210.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v210.types.IO_Command;
import com.microrisc.simply.iqrf.dpa.v210.types.IO_DirectionSettings;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA Device Interface for general IO operations.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface IO 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        SET_DIRECTION,
        SET_OUTPUT_STATE,
        GET
    }
    
    /**
     * Sends method call request for setting the direction of the individual 
     * IO pins of the individual ports.
     * @param directionSettings direction settings
     * @return unique identifier of sent request
     */
    UUID async_setDirection(IO_DirectionSettings[] directionSettings);
    
    /**
     * Sets the direction of the individual IO pins of the individual ports. 
     * Additionally the same command can be used to setup weak pull-ups at 
     * the pins where available. <br>
     * See datasheet of the PIC MCU for a description of IO ports.
     * Synchronous wrapper for {@link #async_setDirection() async_setDirection} method.
     * @param directionSettings direction settings
     * @return {@code VoidType} object, if operation has processed correctly <br>
     *         {@code null}, if an error has occurred during processing
     */
    VoidType setDirection(IO_DirectionSettings[] directionSettings);
    
    
    /**
     * Sends method call request for setting the output state of the IO pins.
     * @param ioCommands IO commands
     * @return unique identifier of sent request
     */
    UUID async_setOutputState(IO_Command[] ioCommands);
    
    /**
     * Sets the output state of the IO pins.
     * Synchronous wrapper for {@link #async_setOutputState() async_setOutputState} method.
     * @param ioCommands IO commands
     * @return {@code VoidType} object, if operation has processed correctly <br>
     *         {@code null}, if an error has occurred during processing
     */
    VoidType setOutputState(IO_Command[] ioCommands);
    
    
    /**
     * Sends method call request for getting the input state of all supported 
     * the MCU ports.
     * @return unique identifier of sent request
     */
    UUID async_get();
    
    /**
     * Reads the input state of all supported the MCU ports.
     * Synchronous wrapper for {@link #async_getState() async_getState} method.
     * @return array of bytes representing state of port PORTA, PORTB, ..., 
     *         ending with the last supported MCU port. <br>
     *         {@code null}, if an error has occurred during processing
     */
    short[] get();
}
