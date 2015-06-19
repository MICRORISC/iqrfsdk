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

package com.microrisc.simply.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Reader of configuration information from XML files.
 * 
 * @author Michal Konopa
 */
public final class XMLConfigurationMappingReader {
    /**
     * Reads in and returns configuration mapping from specified source XML file. 
     * Returned mapping's keys are values at {@code key} nodes. The values
     * of returned mapping are {@code Configuration} objects at nodes specified
     * by {@code sourceNodeName}. {@code key} node must be contained within 
     * {@code sourceNodeName} node, and all of its values should be unique.  
     * @param configFileName source XML file name
     * @param sourceNodeName name of node within the XML file, where to get configurations from
     * @param key name of node within {@code sourceNodeName}, whose values constitutes keys 
     *            of returned mapping
     * @return configuration mapping <br>
     *         empty mapping if no node with {@code sourceNodeName} exist within the source file
     * @throws ConfigurationException if an error has encountered during reading 
     *                               source file
     */
    public static Map<String, Configuration> getConfigMapping(
            String configFileName, String sourceNodeName, String key
    ) throws ConfigurationException {
        XMLConfiguration mapperConfig = new XMLConfiguration(configFileName);
       
        // get all 'nodeName' nodes
        List<HierarchicalConfiguration> nodeMappings = mapperConfig.configurationsAt(sourceNodeName);
        
        // result mapping
        Map<String, Configuration> configMappings = new HashMap<String, Configuration>();
        
        // if no mapping exists, return empty mappings
        if (nodeMappings.isEmpty()) {
            return configMappings;
        }
        
        // read in all impl mappings
        for(HierarchicalConfiguration nodeMapping : nodeMappings) {
            String keyValue = nodeMapping.getString(key);
            configMappings.put(keyValue, nodeMapping);
        }
        
        return configMappings;
    }
}
