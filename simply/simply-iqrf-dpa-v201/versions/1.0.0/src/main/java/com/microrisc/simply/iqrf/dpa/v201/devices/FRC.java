
package com.microrisc.simply.iqrf.dpa.v201.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v201.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v201.types.FRC_Command;
import com.microrisc.simply.iqrf.dpa.v201.types.FRC_Data;
import java.util.UUID;

/**
 * DPA FRC Device Interface.
 * All methods return {@code null} if an error has occurred during processing
 * of corresponding method call.
 * 
 * @author Rostislav Spinar
 */
@DeviceInterface
public interface FRC 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        SEND,
        EXTRA_RESULT
    }
    
    /**
     * This command starts Fast Response Command (FRC) process supported by IQRF OS. 
     * Synchronous wrapper for 
     * {@link #async_send(FRC_Command.class) async_send} method.
     * @param frcCmd FRC command to use
     * @return FRC data collected from nodes
     */
    FRC_Data send(FRC_Command frcCmd);
  
    /**
     * Reads remaining bytes of the FRC result, so the total number of bytes obtained 
     * by both commands will be total 64. 
     * Synchronous wrapper for 
     * {@link #async_async_extraResult() async_async_extraResult} method.
     * 
     * @return remaining bytes of the FRC result
     */
    short[] extraResult();
    
    /**
     * This command starts Fast Response Command (FRC) process supported by IQRF OS. 
     * It allows quickly and using only one command to collect same type of information 
     * from multiple nodes in the network.
     * @param frcCmd FRC command to use
     * @return unique identifier of sent request  
     */
    UUID async_send(FRC_Command frcCmd);
  
    /**
     * Reads remaining bytes of the FRC result, so the total number of bytes obtained 
     * by both commands will be total 64. It is recommended to call this command 
     * immediately after the FRC Send command to preserve previously collected FRC 
     * data.
     * 
     * @return unique identifier of sent request
     */
    UUID async_extraResult();
}
