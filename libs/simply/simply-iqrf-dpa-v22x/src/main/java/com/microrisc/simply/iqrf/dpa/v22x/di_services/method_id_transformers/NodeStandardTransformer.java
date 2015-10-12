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
import com.microrisc.simply.iqrf.dpa.v22x.devices.Node;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for Node. 
 * 
 * @author Michal Konopa
 */
public final class NodeStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<Node.MethodID, String> methodIdsMap = 
            new EnumMap<Node.MethodID, String>(Node.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(Node.MethodID.READ, "1");
        methodIdsMap.put(Node.MethodID.REMOVE_BOND, "2");
        methodIdsMap.put(Node.MethodID.ENABLE_REMOTE_BONDING, "3");
        methodIdsMap.put(Node.MethodID.READ_REMOTELY_BONDED_MODULE_ID, "4");
        methodIdsMap.put(Node.MethodID.CLEAR_REMOTELY_BONDED_MODULE_ID, "5");
        methodIdsMap.put(Node.MethodID.REMOVE_BOND_ADDRESS, "6");
        methodIdsMap.put(Node.MethodID.BACKUP, "7");
        methodIdsMap.put(Node.MethodID.RESTORE, "8");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final NodeStandardTransformer instance = new NodeStandardTransformer();
    
    
    /**
     * @return NodeStandardTransformer instance 
     */
    static public NodeStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof Node.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type Node.MethodID."
            );
        }
        return methodIdsMap.get((Node.MethodID) methodId);
    }
    
}
