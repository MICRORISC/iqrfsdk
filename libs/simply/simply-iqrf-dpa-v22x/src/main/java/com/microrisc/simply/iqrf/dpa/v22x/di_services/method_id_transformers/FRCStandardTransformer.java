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
import com.microrisc.simply.iqrf.dpa.v22x.devices.FRC;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for FRC. 
 * 
 * @author Michal Konopa
 */
public final class FRCStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<FRC.MethodID, String> methodIdsMap = 
            new EnumMap<FRC.MethodID, String>(FRC.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(FRC.MethodID.SEND, "1");
        methodIdsMap.put(FRC.MethodID.EXTRA_RESULT, "2");
        methodIdsMap.put(FRC.MethodID.SEND_SELECTIVE, "3");
        methodIdsMap.put(FRC.MethodID.SET_FRC_PARAMS, "4");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final FRCStandardTransformer instance = new FRCStandardTransformer();
    
    
    /**
     * @return FRCStandardTransformer instance 
     */
    static public FRCStandardTransformer getInstance() {
        return instance;
    }
    
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof FRC.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type FRC.MethodID."
            );
        }
        return methodIdsMap.get((FRC.MethodID) methodId);
    }
    
}
