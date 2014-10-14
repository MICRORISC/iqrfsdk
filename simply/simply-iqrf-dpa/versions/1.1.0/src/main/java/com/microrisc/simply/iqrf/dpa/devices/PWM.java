
package com.microrisc.simply.iqrf.dpa.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.types.PWM_Parameters;
import com.microrisc.simply.iqrf.types.VoidType;

/**
 * DPA PWM Device Interface.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface PWM 
extends DPA_Device, DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        SET
    }
    
    /**
     * Sets PWM parameters.
     * @param param PWM to use for settting
     * @return {@code null}, if an error has occurred during processing
     */
    VoidType set(PWM_Parameters param);
}
