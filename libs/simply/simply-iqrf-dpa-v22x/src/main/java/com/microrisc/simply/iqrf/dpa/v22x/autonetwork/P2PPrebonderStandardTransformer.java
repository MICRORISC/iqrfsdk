/*
 * Copyright 2014 MICRORISC s.r.o..
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

package com.microrisc.simply.iqrf.dpa.v22x.autonetwork;

import com.microrisc.simply.di_services.MethodIdTransformer;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for {@link P2PPrebonder}. 
 * 
 * @author Michal Konopa
 */
public final class P2PPrebonderStandardTransformer 
implements MethodIdTransformer 
{
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<P2PPrebonder.MethodID, String> methodIdsMap 
            = new EnumMap<>(P2PPrebonder.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(P2PPrebonder.MethodID.SEND_PREBONDING_DATA, "1");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    private P2PPrebonderStandardTransformer() {}
    
    /** Singleton. */
    private static final P2PPrebonderStandardTransformer instance = new P2PPrebonderStandardTransformer();
    
    
    /**
     * @return P2PPrebonderStandardTransformer instance 
     */
    static public P2PPrebonderStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof P2PPrebonder.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type P2PPrebonder.MethodID."
            );
        }
        return methodIdsMap.get((P2PPrebonder.MethodID) methodId);
    }
}
