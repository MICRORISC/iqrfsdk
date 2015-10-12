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
import com.microrisc.simply.iqrf.dpa.v22x.devices.GeneralMemory;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for general memory. 
 * 
 * @author Michal Konopa
 */
public final class GeneralMemoryStandardTransformer implements MethodIdTransformer {
    
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<GeneralMemory.MethodID, String> methodIdsMap = 
            new EnumMap<GeneralMemory.MethodID, String>(GeneralMemory.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(GeneralMemory.MethodID.READ, "1");
        methodIdsMap.put(GeneralMemory.MethodID.WRITE, "2");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final GeneralMemoryStandardTransformer instance = new GeneralMemoryStandardTransformer();
    
    
    /**
     * @return GeneralMemoryStandardTransformer instance 
     */
    static public GeneralMemoryStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof GeneralMemory.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type GeneralMemory.MethodID."
            );
        }
        return methodIdsMap.get((GeneralMemory.MethodID) methodId);
    }
    
}
