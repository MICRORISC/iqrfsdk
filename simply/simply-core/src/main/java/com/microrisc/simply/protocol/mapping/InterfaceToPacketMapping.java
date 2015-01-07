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

package com.microrisc.simply.protocol.mapping;

import java.util.List;
import java.util.Map;

/**
 * Describes Device Interface mapping into packet of used protocol layer.
 * 
 * @author Michal Konopa
 */
public final class InterfaceToPacketMapping{ 
    /** Constant mappings */
    private List<ConstValueToPacketMapping> constantMappings = null;
    
    /** method mappings */
    private Map<String, MethodToPacketMapping> methodMappings = null;
    
    
    
    /**
     * Constructor. 
     * @param constantMappings constant mappings
     * @param methodMappings method mappings
     */
    public InterfaceToPacketMapping(List<ConstValueToPacketMapping> constantMappings, 
            Map<String, MethodToPacketMapping> methodMappings) {
        this.constantMappings = constantMappings;
        this.methodMappings = methodMappings;
    }
    
    /**
     * Returns method mapping correponding to specified method ID.
     * @param methodId method ID
     * @return method mapping correponding to specified method ID.
     * @return {@code null}, if such mapping was not found
     */
    public MethodToPacketMapping getMethodMapping(String methodId) {
        return methodMappings.get(methodId);
    } 
    
    /**
     * @return constant mappings.
     */
    public List<ConstValueToPacketMapping> getConstantMappings() {
        return constantMappings;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "constant mappings=" + constantMappings + 
                ", method mappings=" + methodMappings + 
                " }");
    }
}
