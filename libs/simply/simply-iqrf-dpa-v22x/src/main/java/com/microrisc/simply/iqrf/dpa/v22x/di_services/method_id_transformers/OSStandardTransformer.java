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
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for OS. 
 * 
 * @author Michal Konopa
 */
public final class OSStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<OS.MethodID, String> methodIdsMap = new EnumMap<>(OS.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(OS.MethodID.READ, "1");
        methodIdsMap.put(OS.MethodID.RESET, "2");
        methodIdsMap.put(OS.MethodID.READ_HWP_CONFIGURATION, "3");
        methodIdsMap.put(OS.MethodID.RUN_RFPGM, "4");
        methodIdsMap.put(OS.MethodID.SLEEP, "5");
        methodIdsMap.put(OS.MethodID.BATCH, "6");
        methodIdsMap.put(OS.MethodID.SET_USEC, "7");
        methodIdsMap.put(OS.MethodID.SET_MID, "8");
        methodIdsMap.put(OS.MethodID.RESTART, "9");
        methodIdsMap.put(OS.MethodID.WRITE_HWP_CONFIGURATION, "10");
        methodIdsMap.put(OS.MethodID.WRITE_HWP_CONFIGURATION_BYTE, "11");
        methodIdsMap.put(OS.MethodID.LOAD_CODE, "12");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final OSStandardTransformer instance = new OSStandardTransformer();
    
    
    /**
     * @return OSStandardTransformer instance 
     */
    static public OSStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof OS.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type OS.MethodID."
            );
        }
        return methodIdsMap.get((OS.MethodID) methodId);
    }
    
}
