

package com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.devices.UART;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for UART. 
 * 
 * @author Michal Konopa
 */
public final class UARTStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<UART.MethodID, String> methodIdsMap = 
            new EnumMap<UART.MethodID, String>(UART.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(UART.MethodID.OPEN, "1");
        methodIdsMap.put(UART.MethodID.CLOSE, "2");
        methodIdsMap.put(UART.MethodID.WRITE_AND_READ, "3");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final UARTStandardTransformer instance = new UARTStandardTransformer();
    
    
    /**
     * @return UARTStandardTransformer instance 
     */
    static public UARTStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof UART.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type UART.MethodID."
            );
        }
        return methodIdsMap.get((UART.MethodID) methodId);
    }
}
