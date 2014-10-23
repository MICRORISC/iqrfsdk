
package com.microrisc.simply.iqrf.dpa.v210.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v210.types.PeripheralEnumeration;
import com.microrisc.simply.iqrf.dpa.v210.types.PeripheralInfo;
import java.util.UUID;

/**
 * Device interface for getting information about peripherals from underlaying
 * network nodes.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface PeripheralInfoGetter 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        GET_PERIPHERAL_ENUMERATION,
        GET_PERIPHERAL_INFO,
        GET_MORE_PERIPHERALS_INFO
    }
    
    /**
     * Sends method call request for peripheral enumeration available on this node.
     * @return unique identifier of sent request 
     */
    UUID async_getPeripheralEnumeration();
    
    /**
     * Returns information about peripheral enumeration on this node.
     * Synchronous wrapper for {@link #async_getPeripheralEnumeration() 
     * async_getPeripheralEnumeration} method.
     * @return information about peripheral enumeration <br>
     *         {@code null} if an error has occurred during processing
     */
    PeripheralEnumeration getPeripheralEnumeration();
    
    /**
     * Sends method call requests for information about specified peripheral.
     * @param peripheralNumber number of peripheral, whose info is requested
     * @return unique identifier of sent request
     */
    UUID async_getPeripheralInfo(int peripheralNumber);
    
    /**
     * Returns information about specified peripheral.
     * Synchronous wrapper for {@link #async_getPeripheralInfo(int) 
     * async_getPeripheralInfo} method.
     * @param peripheralNumber number of peripheral, whose info is requested
     * @return information about specified peripheral <br>
     *         {@code null} if an error has occurred during processing
     */
    PeripheralInfo getPeripheralInfo(int peripheralNumber);
    
    /**
     * Sends method call requests for information about sequence of more 
     * peripherals, starting with specified peripheral number.
     * @param startPeripheralNumber starting peripheral's number
     * @return unique identifier of sent request 
     */
    UUID async_getMorePeripheralsInfo(int startPeripheralNumber);
    
    /**
     * Returns information about sequence of peripherals, starting with 
     * specified peripheral number.
     * Synchronous wrapper for {@link #async_getMorePeripheralsInfo(int) 
     * async_getMorePeripheralsInfo} method.
     * @param startPeripheralNumber starting peripheral's number
     * @return information about peripherals <br>
     *         {@code null} if an error has occurred during processing
     */
    PeripheralInfo[] getMorePeripheralsInfo(int startPeripheralNumber);
}
