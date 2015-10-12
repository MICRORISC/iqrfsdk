/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.devices.UART;
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
