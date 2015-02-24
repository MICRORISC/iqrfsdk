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

package com.microrisc.simply.init;

import com.microrisc.simply.SimpleConnectionStack;
import com.microrisc.simply.ConnectionStack;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.NetworkLayerService;
import com.microrisc.simply.ProtocolLayerService;
import com.microrisc.simply.network.NetworkLayer;
import com.microrisc.simply.protocol.ProtocolLayer;
import com.microrisc.simply.connector.AbstractConnectorFactory;
import com.microrisc.simply.connector.Connector;
import com.microrisc.simply.network.AbstractNetworkLayerFactory;
import com.microrisc.simply.network.NetworkConnectionStorage;
import com.microrisc.simply.network.SimpleNetworkConnectionStorageFactory;
import com.microrisc.simply.protocol.MessageConvertor;
import com.microrisc.simply.protocol.mapping.ProtocolMapping;
import com.microrisc.simply.protocol.mapping.ProtocolMappingFactory;
import com.microrisc.simply.utilities.XMLConfigurationMappingReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Simple Simply initialization objects factory.
 * 
 * @author Michal Konopa
 */
public class SimpleInitObjectsFactory 
extends AbstractInitObjectsFactory<
            Configuration, 
            InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>>
        > {
    /** Configuration file. */
    protected String configFile;
    
    
    /** 
     * Creates network connection storage.
     * @param configuration input configuration
     * @return network connection storage
     * @throws java.lang.Exception if an error has occured during creating of 
     *         network connection storage
     */
    protected NetworkConnectionStorage createNetworkConnectionStorage(
            Configuration configuration
    ) throws Exception {
        return (new SimpleNetworkConnectionStorageFactory()).getNetworkConnectionStorage(configuration);
    }
    
    /** 
     * Creates network layer.
     * @param connectionStorage connection storage to use
     * @param configuration input configuration
     * @return network layer
     * @throws java.lang.Exception if an error has occured during creating of 
     *         network layer
     */
    protected NetworkLayer createNetworkLayer(
            NetworkConnectionStorage connectionStorage,
            Configuration configuration
    ) throws Exception {
        String factoryClassName = configuration.getString("networkLayer.factory.class");
        Class factoryClass = Class.forName(factoryClassName);
        java.lang.reflect.Constructor constructor = factoryClass.getConstructor();
        AbstractNetworkLayerFactory factory = (AbstractNetworkLayerFactory)constructor.newInstance();
        return factory.getNetworkLayer(connectionStorage, configuration);
    }
    
    /** 
     * Create protocol mapping.
     * @param configuration input configuration
     * @return protocol mapping
     * @throws java.lang.Exception if an error has occured during creating of 
     *         protocol mapping
     */
    protected ProtocolMapping createProtocolMapping(Configuration configuration) 
            throws Exception {
        String factoryClassName = configuration.getString("protocolLayer.protocolMapping.factory.class");
        Class factoryClass = Class.forName(factoryClassName);
        java.lang.reflect.Constructor constructor = factoryClass.getConstructor();
        ProtocolMappingFactory protoFactory = (ProtocolMappingFactory)constructor.newInstance(); 
        return protoFactory.createProtocolMapping();
    }
    
    /** 
     * Creates message convertor.
     * @param protoMapping protocol mapping to use
     * @param configuration input configuration
     * @return message convetor
     * @throws java.lang.Exception if an error has occured during creating of 
     *         message convertor 
     */
    protected MessageConvertor createMessageConvertor(
            ProtocolMapping protoMapping, 
            Configuration configuration
    ) throws Exception {
        String msgConvClassName = configuration.getString("protocolLayer.messageConvertor.class");
        Class msgConvClass = Class.forName(msgConvClassName);
        java.lang.reflect.Constructor constructor = 
                msgConvClass.getConstructor(ProtocolMapping.class);
        return (MessageConvertor)constructor.newInstance(protoMapping); 
    }
    
    /** 
     * Creates protocol layer.
     * @param networkLayerService network layer service to use
     * @param msgConvertor message convertor to use
     * @param configuration source configuration
     * @return protocol layer
     * @throws java.lang.Exception if an error has occured during creating of 
     *         protocol layer 
     */
    protected ProtocolLayer createProtocolLayer(
            NetworkLayerService networkLayerService, 
            MessageConvertor msgConvertor, 
            Configuration configuration
    ) throws Exception {
        String protoClassName = configuration.getString("protocolLayer.class");
        Class protoClass = Class.forName(protoClassName);
        java.lang.reflect.Constructor constructor 
                = protoClass.getConstructor(NetworkLayerService.class, MessageConvertor.class);
        return (ProtocolLayer)constructor.newInstance(networkLayerService, msgConvertor);
    }
    
    /** 
     * Creates connector.
     * @param protocolLayerService protocol layer service to use
     * @param configuration source configuration
     * @return connector
     * @throws java.lang.Exception if an error has occured during creating of 
     *         connector
     */
    protected Connector createConnector(
            ProtocolLayerService protocolLayerService, 
            Configuration configuration
    ) throws Exception { 
        String factoryClassName = configuration.getString("connector.factory.class");
        Class factoryClass = Class.forName(factoryClassName);
        java.lang.reflect.Constructor constructor = factoryClass.getConstructor();
        AbstractConnectorFactory factory = (AbstractConnectorFactory) constructor.newInstance();
        return factory.getConnector(protocolLayerService, configuration);
    }
    
    /** 
     * Creates connection stack.
     * @param configuration source configuration
     * @return connection stack
     * @throws com.microrisc.simply.SimplyException if an error has occured 
     *         during creating of connection stack 
     */
    protected ConnectionStack createConnectionStack(Configuration configuration) 
            throws SimplyException {
        ConnectionStack connectionStack = null;
        try {
            // creating network layer
            NetworkConnectionStorage connStorage = createNetworkConnectionStorage(
                    configuration
            );
            NetworkLayer networkLayer = createNetworkLayer(connStorage, configuration);
            
            // creating protocol layer
            ProtocolMapping protoMapping = createProtocolMapping(configuration);
            MessageConvertor msgConvertor = createMessageConvertor(protoMapping, configuration);
            ProtocolLayer protoLayer = createProtocolLayer(networkLayer, msgConvertor, configuration);
            
            // creating connector
            Connector connector = createConnector(protoLayer, configuration);
            connectionStack = new SimpleConnectionStack(networkLayer, protoLayer, connector);
        } catch ( Exception e ) {
            throw new SimplyException(e);
        }
        
        return connectionStack;
    }
    
    /** 
     * Creates and returns mapper of device interfaces to theirs implementing classes.
     * @param configuration source configuration
     * @return mapper of device interfaces to theirs implementing classes.
     * @throws java.lang.Exception if an error has occured during creating of 
     *         mapper
     */
    protected ImplClassesMapper createImplClassesMapper(Configuration configuration) 
            throws Exception {
        String mappingFile = configuration.getString("implClassesMapping.configFile");
        XMLConfiguration mapperConfig = new XMLConfiguration(mappingFile);
       
        // get all "interfaceMappings" nodes
        List<HierarchicalConfiguration> implMappings = 
            mapperConfig.configurationsAt("implMapping");
        
        // if no implementation class mapping exists, throw Exception 
        if ( implMappings.isEmpty() ) {
            throw new SimplyException(
                "Implementation mapping: No implementation class mapping exist"
            );
        }
        
        // mapping
        Map<Class, Class> ifaceToImpl = new HashMap<>();
        
        // read in all impl mappings
        for( HierarchicalConfiguration implMapping : implMappings ) {
            String ifaceStr = implMapping.getString("interface");
            String implStr = implMapping.getString("implClass");
            
            Class ifaceClass = Class.forName(ifaceStr);
            Class implClass = Class.forName(implStr);
            
            ifaceToImpl.put(ifaceClass, implClass);
        }
        return new SimpleImplClassesMapper(ifaceToImpl);
    }
    
    
    /** 
     * Creates connected networks settings.
     * @param configuration configuration needed for getting network settings
     * @return map of configuration of each network to connect
     * @throws org.apache.commons.configuration.ConfigurationException if 
     *         if an error has occured during getting connected networks configurations
     */ 
    protected Map<String, Configuration> createNetworksSettings(Configuration configuration) 
            throws ConfigurationException {
        String settingsFileName = configuration.getString("networkSettings.configFile");
        return XMLConfigurationMappingReader.getConfigMapping(settingsFileName, "network", "id");
    }
    
    
    @Override
    public InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> 
            getInitObjects(Configuration configuration
    ) throws Exception {
        ConnectionStack connStack = createConnectionStack(configuration);
        ImplClassesMapper implClassMapper = createImplClassesMapper(configuration);
        InitConfigSettings<Configuration, Map<String, Configuration>> initConfigSettings 
                = new MapInitConfigSettings(configuration, createNetworksSettings(configuration));
        return new SimpleInitObjects(connStack, implClassMapper, initConfigSettings);
    }
    
}
