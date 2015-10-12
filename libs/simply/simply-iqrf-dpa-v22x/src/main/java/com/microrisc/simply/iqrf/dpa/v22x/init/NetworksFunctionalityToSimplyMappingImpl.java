/*
 * Copyright 2015 MICRORISC s.r.o..
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

package com.microrisc.simply.iqrf.dpa.v22x.init;

import java.util.Map;
import java.util.Set;

/**
 * Basic implementation of {@link NetworksFunctionalityToSimplyMapping} interface.
 * 
 * @author Michal Konopa
 */
public final class NetworksFunctionalityToSimplyMappingImpl 
implements NetworksFunctionalityToSimplyMapping 
{
    private final Map<String, Map<String, Set<Integer>>> mapping;
    
    
    private Map<String, Map<String, Set<Integer>>> checkMapping(
            Map<String, Map<String, Set<Integer>>> mapping
    ) {
        if ( mapping == null ) {
            throw new IllegalArgumentException("Network functionality mapping cannot be null.");
        }
        return mapping;
    }
    
    /**
     * 
     * @param mapping 
     */
    public NetworksFunctionalityToSimplyMappingImpl(
            Map<String, Map<String, Set<Integer>>> mapping
    ) {
        this.mapping = checkMapping(mapping);
    }
    
    @Override
    public Map<String, Map<String, Set<Integer>>> getMapping() {
        return mapping;
    }
    
}
