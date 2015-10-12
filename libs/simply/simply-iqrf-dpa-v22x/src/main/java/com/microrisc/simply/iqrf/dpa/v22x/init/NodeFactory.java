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

package com.microrisc.simply.iqrf.dpa.v22x.init;

import com.microrisc.simply.BaseNode;
import com.microrisc.simply.DeviceObject;
import com.microrisc.simply.Node;
import com.microrisc.simply.SimpleDeviceObjectFactory;
import com.microrisc.simply.init.InitConfigSettings;
import com.microrisc.simply.iqrf.dpa.v22x.devices.PeripheralInfoGetter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating nodes.
 * 
 * @author Michal Konopa
 */
public final class NodeFactory {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(NodeFactory.class);
    
    private static DPA_InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>>
            _initObjects = null;
    
    private static SimpleDeviceObjectFactory _devObjectFactory = null;
    
    /**
     * Creates and returns peripheral information object for specified node.
     */
    private static PeripheralInfoGetter createPerInfoObject(String networkId, String nodeId) 
            throws Exception {
        Class baseImplClass = _initObjects.getImplClassMapper().getImplClass(
                PeripheralInfoGetter.class
        );
        
        if ( _devObjectFactory == null ) {
            _devObjectFactory = new SimpleDeviceObjectFactory();
        }
        
        return (PeripheralInfoGetter)_devObjectFactory.getDeviceObject(
                networkId, nodeId, _initObjects.getConnectionStack().getConnector(),
                baseImplClass, _initObjects.getConfigSettings().getGeneralSettings()
        );
    }
    
    
    private static DPA_InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>>
    checkInitObjects(
            DPA_InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> initObjects
    ) {
        if ( initObjects == null ) {
            throw new IllegalArgumentException("DPA initialization objects cannot be null");
        }
        return initObjects;
    }
    
    /**
     * Initializes the Node Factory.
     * @param initObjects DPA initialization objects
     */
    public static void init(
            DPA_InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> initObjects
    ) {
        _initObjects = checkInitObjects(initObjects);
    }
    
    /**
     * Creates and returns new node with specified peripherals.
     * @param networkId ID of network the node belongs to
     * @param nodeId ID of node to create
     * @param perNumbers peripheral numbers to create node services for
     * @return new node
     * @throws java.lang.Exception if an error has occured during node creation
     */
    public static Node createNode(String networkId, String nodeId, Set<Integer> perNumbers) 
            throws Exception 
    {
        logger.debug("createNode - start: networkId={}, nodeId={}, perNumbers={}",
                networkId, nodeId, Arrays.toString(perNumbers.toArray( new Integer[0] ))
        );
        
        // node services
        Map<Class, DeviceObject> services = new HashMap<>();
        
        // creating Peripheral Information object
        PeripheralInfoGetter perInfoObject = createPerInfoObject(networkId, nodeId);
        
        // put info object into service's map
        services.put(PeripheralInfoGetter.class, (DeviceObject)perInfoObject);
        
        for ( int perId : perNumbers ) {
            Class devIface = _initObjects.getPeripheralToDevIfaceMapper().getDeviceInterface(perId);
            
            // IMPORTANT: if no device interface for specified peripheral number is found,
            // continue, not throw an exception
            if ( devIface == null ) {
                logger.warn("Interface not found for peripheral: {}", perId);
                continue;
            }
            
            Class implClass = _initObjects.getImplClassMapper().getImplClass(devIface);
            if ( implClass == null ) {
                throw new RuntimeException("Implementation for " + devIface.getName() + " not found");
            }
            
            if ( _devObjectFactory == null ) {
                _devObjectFactory = new SimpleDeviceObjectFactory();
            }
            
            // creating new device object for implementation class
            DeviceObject newDeviceObj = _devObjectFactory.getDeviceObject(
                    networkId, nodeId, _initObjects.getConnectionStack().getConnector(), 
                    implClass, _initObjects.getConfigSettings().getGeneralSettings()
            );
            
            // put object into service's map
            services.put(devIface, newDeviceObj);
        }
        
        Node node = new BaseNode(networkId, nodeId, services); 
        
        logger.debug("createNode - end: {}", node);
        return node;
    }
    
    /**
     * Creates and returns new node with services for all peripherals.
     * @param networkId ID of network the node belongs to
     * @param nodeId ID of node to create
     * @return new node
     * @throws java.lang.Exception if an error has occured during node creation
     */
    public static Node createNodeWithAllPeripherals(String networkId, String nodeId) 
            throws Exception 
    {
        Set<Integer> peripherals = _initObjects.getPeripheralToDevIfaceMapper().getMappedPeripherals();
        return createNode(networkId, nodeId, peripherals);
    }
}
