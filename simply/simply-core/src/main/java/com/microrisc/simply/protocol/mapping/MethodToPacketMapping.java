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

/**
 * Describes mapping between Device Interface's method into packet of  
 * protocol layer.
 * 
 * @author Michal Konopa
 */
public final class MethodToPacketMapping {
    /** Constant mappings */
    private List<ConstValueToPacketMapping> constantMappings = null; 
    
    /** Method argument mappings. */
    private List<ValueToPacketMapping> argMappings = null; 
    
    
    /**
     * Constructor.
     * @param constantMappings constant mappings
     * @param argMappings method argument mappings
     */
    public MethodToPacketMapping(List<ConstValueToPacketMapping> constantMappings, 
            List<ValueToPacketMapping> argMappings) {
        this.constantMappings = constantMappings;
        this.argMappings = argMappings;
    }
    
    /**
     * @return constant mappings
     */
    public List<ConstValueToPacketMapping> getConstantMappings() {
        return constantMappings;
    }

    /**
     * @return the argument mappings
     */
    public List<ValueToPacketMapping> getArgMappings() {
        return argMappings;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "constant mappings=" + constantMappings + 
                ", arg mappings=" + argMappings + 
                " }");
    }
}
