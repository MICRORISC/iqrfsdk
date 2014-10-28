package com.microrisc.simply.iqrf.dpa.v210.examples.user_peripherals.mydallas.def;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;

/**
 *  MyDallas18B20 Device Interface.
 * 
 * @author Martin Strouhal
 */
@DeviceInterface
public interface MyDallas18B20 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {

    /**
     * Identifiers of this Device Interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        GET
    }

    /**
     * Gets actual temperature.
     *
     * @return actual temperature <br> 
     *         {@code Float.MAX_VALUE} if an error has occurred during processing
     */
    float get();
}
