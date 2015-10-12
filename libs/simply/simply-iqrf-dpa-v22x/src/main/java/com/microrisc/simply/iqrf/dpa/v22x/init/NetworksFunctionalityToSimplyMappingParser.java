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

package com.microrisc.simply.iqrf.dpa.v22x.init;

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
    
    // returns set of peripherals IDs
    private static Set<Integer> getPeripherals(HierarchicalConfiguration config) {
        List<Object> peripheralsList = config.getList("peripherals");

        Set<Integer> peripherals = new HashSet<>();
        for ( Object perIdObj : peripheralsList ) {
            Integer perId = Integer.parseInt((String)perIdObj);
            peripherals.add(perId);
        }
        
        return peripherals;
    }
    
    // reads configurations of inidividual nodes and stores them into specified map
    private static void readIndividualNodesConfigurations(
            HierarchicalConfiguration networkConfig, 
            Map<String, Set<Integer>> nodesMap
    ) {
        List<HierarchicalConfiguration> nodeConfigs = networkConfig.configurationsAt("nodes.node");

        for ( HierarchicalConfiguration nodeConfig : nodeConfigs ) {
            String nodeId = nodeConfig.getString("[@id]");
            Set<Integer> peripherals = getPeripherals(nodeConfig);
            
            if ( nodesMap.containsKey(nodeId) ) {
                throw new IllegalStateException("Multiple declaration of node: " + nodeId);
            }
            nodesMap.put(nodeId, peripherals);
        }
    }
    
    private static int checkNumericNodeId(int nodeId) {
        if ( nodeId < 0 ) {
            throw new IllegalArgumentException("Node ID cannot be less than 0");
        }
        return nodeId;
    }
    
    private static void checkNodeInterval(int minId, int maxId) {
        if ( minId > maxId ) {
            throw new IllegalArgumentException("Min node ID cannot be grether than max node ID");
        }
    }
    
    // reads configurations of nodes intervals and stores them into specified map
    private static void readNodesIntervalsConfigurations(
            HierarchicalConfiguration networkConfig,
            Map<String, Set<Integer>> nodesMap
    ) {
        List<HierarchicalConfiguration> nodesIntervalConfigs = networkConfig.configurationsAt("nodesInterval");

        for ( HierarchicalConfiguration nodesIntervalConfig : nodesIntervalConfigs ) {
            int minId = checkNumericNodeId(nodesIntervalConfig.getInt("[@min]"));
            int maxId = checkNumericNodeId(nodesIntervalConfig.getInt("[@max]"));
            checkNodeInterval(minId, maxId);
            
            Set<Integer> peripherals = getPeripherals(nodesIntervalConfig);
            
            for ( int nodeId = minId; nodeId <= maxId; nodeId++ ) {
                if ( nodesMap.containsKey(Integer.toString(nodeId)) ) {
                    throw new IllegalStateException("Multiple declaration of node: " + nodeId);
                }
                nodesMap.put(Integer.toString(nodeId), peripherals);
            }
        }
    }
    
    // returns set of nodes IDs
    private static Set<String> getNodes(HierarchicalConfiguration nodesSetsConfig) {
        List<Object> nodesList = nodesSetsConfig.getList("[@id]");
        Set<String> nodesSet = new HashSet<>();
        for ( Object nodeObj : nodesList ) {
            nodesSet.add(nodeObj.toString());
        }
        return nodesSet;
    }
    
    // reads configurations of nodes sets and stores them into specified map
    private static void readNodesSetsConfigurations(
            HierarchicalConfiguration networkConfig,
            Map<String, Set<Integer>> nodesMap
    ) {
        List<HierarchicalConfiguration> nodesSetsConfigs = networkConfig.configurationsAt("nodesSet");

        for ( HierarchicalConfiguration nodesSetsConfig : nodesSetsConfigs ) {
            Set<Integer> peripherals = getPeripherals(nodesSetsConfig);
            Set<String> nodesSet = getNodes(nodesSetsConfig);
            
            for ( String nodeId : nodesSet ) {
                if ( nodesMap.containsKey(nodeId) ) {
                    throw new IllegalStateException("Multiple declaration of node: " + nodeId);
                }
                nodesMap.put(nodeId, peripherals);
            }
        }
    }
    
    
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
            Map<String, Set<Integer>> nodesMap = new HashMap<>();
            
            readIndividualNodesConfigurations(networkConfig, nodesMap);
            readNodesIntervalsConfigurations(networkConfig, nodesMap);
            readNodesSetsConfigurations(networkConfig, nodesMap);
            
            resultMapping.put(networkConfig.getString("[@id]"), nodesMap);
        }
        
        return new NetworksFunctionalityToSimplyMappingImpl(resultMapping);
    }
}
