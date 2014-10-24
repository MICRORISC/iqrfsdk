package com.microrisc.simply.iqrf.dpa.v210.examples.user_peripherals.myadc.def;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;

/**
 *  MyADC Device Interface.
 * 
 * @author Martin Strouhal
 */
@DeviceInterface
public interface MyADC 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {

    /**
     * Identifiers of this Device Interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        GET
    }

    /**
     * Gets actual value of MyADC.
     * @return actual value of MyADC <br> 
     *         {@code Integer.MAX_VALUE} if an error has occurred during processing
     */
    int get();
}
