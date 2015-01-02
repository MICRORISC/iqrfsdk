/*
 * Copyright 2015 MICRORISC s.r.o.
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

package com.microrisc.simply.iqrf.dpa.v210.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Parser of source files storing IQRF networks functionality to Simply objects
 * mappings.
 * <p>
 * Only the <b>XML</b> format of source files is supported nowadays.
 * 
 * @author Michal Konopa
 */
public final class NetworksFunctionalityToSimplyMappingParser {
    /**
     * Parses the specified source file and returns corresponding mapping.
     * @param sourceFileName name of the source file to parse
     * @return parsed networks functionality to Simply objects mapping
     * @throws org.apache.commons.configuration.ConfigurationException if
     *         an error has occured during parsing
     */
    public static NetworksFunctionalityToSimplyMapping parse(String sourceFileName) 
            throws ConfigurationException 
    {
        XMLConfiguration mapperConfig = new XMLConfiguration(sourceFileName);
        
        // result map
        Map<String, Map<String, Set<Integer>>> resultMapping = new HashMap<>();
        
        // get all 'network' nodes
        List<HierarchicalConfiguration> networkConfigs = mapperConfig.configurationsAt("network");
        
        for ( HierarchicalConfiguration networkConfig : networkConfigs ) {
            String networkId = networkConfig.getString("[@id]");
            
            Map<String, Set<Integer>> nodesMap = new HashMap<>();
            
            List<HierarchicalConfiguration> nodeConfigs = networkConfig.configurationsAt("nodes.node");
            
            for ( HierarchicalConfiguration nodeConfig : nodeConfigs ) {
                String nodeId = nodeConfig.getString("[@id]");
                List<Object> peripheralsList = nodeConfig.getList("peripherals");
                
                Set<Integer> peripherals = new HashSet<>();
                for ( Object perIdObj : peripheralsList ) {
                    Integer perId = Integer.parseInt((String)perIdObj);
                    peripherals.add(perId);
                }
                nodesMap.put(nodeId, peripherals);
            }
            
            resultMapping.put(networkId, nodesMap);
        }
        
        return new NetworksFunctionalityToSimplyMappingImpl(resultMapping);
    }
}
