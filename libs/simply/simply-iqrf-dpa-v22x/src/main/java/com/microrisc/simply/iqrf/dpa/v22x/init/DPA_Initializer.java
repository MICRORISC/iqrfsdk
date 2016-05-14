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

package com.microrisc.simply.iqrf.dpa.v22x.init;

import com.microrisc.simply.BaseNetwork;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.SimpleDeviceObjectFactory;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.connector.response_waiting.ResponseWaitingConnector;
import com.microrisc.simply.init.AbstractInitializer;
import com.microrisc.simply.init.InitConfigSettings;
import com.microrisc.simply.iqrf.RF_Mode;
import com.microrisc.simply.iqrf.dpa.DPA_NetworkImpl;
import com.microrisc.simply.iqrf.dpa.DPA_Node;
import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.devices.PeripheralInfoGetter;
import com.microrisc.simply.iqrf.dpa.v22x.protocol.DPA_ProtocolLayer;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveryParams;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveryResult;
import com.microrisc.simply.iqrf.dpa.v22x.types.OsInfo;
import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralEnumeration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates inicialization process of DPA based networks.
 * 
 * @author Michal Konopa
 * @author Martin Strouhal
 */
//JUNE-2015 - improved determing and using RF mode
public final class DPA_Initializer 
extends 
    AbstractInitializer<DPA_InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>>, Network> 
{
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_Initializer.class);
    
    
    /** Inner objects. */
    private DPA_InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> initObjects = null;
    
    /** Device object factory. */
    private final SimpleDeviceObjectFactory devObjectFactory = new SimpleDeviceObjectFactory();
    
    /** Configuration settings for initializer. */
    private DPA_InitializerConfiguration dpaInitConfig = null;
    
    
    private void determineAndUseNetworkConfig(String networkId, Node masterNode){        
        // checking, if OS is present at the master
        OS masterOS = masterNode.getDeviceObject(OS.class);
        OsInfo.TR_Type.TR_TypeSeries trSeries;
        if ( masterOS == null ) {
            logger.warn("Master node doesn't contain OS interface.");
            logger.warn("TR_TypeSeries is unkonown.");
            trSeries = OsInfo.TR_Type.TR_TypeSeries.UNKNOWN;
        } else {       
            //read info about module
            OsInfo info = masterOS.read();
            if(info == null){
                logger.warn("Module configuration wasn't read succesfully.");
                logger.warn("TR_TypeSeries is unkonown.");
                trSeries = OsInfo.TR_Type.TR_TypeSeries.UNKNOWN;                
            }else{
                // save tr type
                trSeries = info.getTrType().getSeries();
            }
        }        
        
        RF_Mode rfMode;
        
        // get PeripheralInfoGetter and check it
        PeripheralInfoGetter peripheralInfo = 
                masterNode.getDeviceObject(PeripheralInfoGetter.class);
        if(peripheralInfo == null){
            logger.warn("Master node doesn't contain PeripheralInfoGetter interface.");
            logger.warn("It will be used STD RF mode.");
            rfMode = RF_Mode.STD;
        } else {
            PeripheralEnumeration enumeration = 
                    peripheralInfo.getPeripheralEnumeration();
            if(enumeration == null){
                logger.warn("Peripheral enumeration wasn't read succesfully.");
                logger.warn("It will be used STD RF mode.");
                rfMode = RF_Mode.STD;
            } else {
                // Flags            Various flags:
                // bit 0           STD IQMESH RF Mode supported
                // bit 1           LP IQMESH RF Mode supported
                // recognize RF mode
                switch (enumeration.getFlags()){
                    case 0b10:
                        rfMode = RF_Mode.LP;
                        logger.info("Recognized LP mode.");
                        break;
                    case 0b01:
                        rfMode = RF_Mode.STD;
                        logger.info("Recognized STD mode.");
                        break;
                    default: 
                        logger.warn("RF mode wasn't read succesfully.");
                        logger.warn("It will be used STD RF mode.");
                        rfMode = RF_Mode.STD;
                }
            }            
        }        
              
        DeterminetedNetworkConfig determinetedConfig = 
                new SimpleDeterminetedNetworkConfig(trSeries, rfMode);
        
        if(initObjects.getConnectionStack().getProtocolLayer() instanceof DPA_ProtocolLayer){
            DPA_ProtocolLayer protocolLayer = (DPA_ProtocolLayer)
                    initObjects.getConnectionStack().getProtocolLayer();
            
            protocolLayer.addNetworkConfig(networkId, determinetedConfig);
        }        
    }
    
    /**
     * Creates and returns peripheral information object for specified node.
     */
    private PeripheralInfoGetter createPerInfoObject(String networkId, String nodeId) 
            throws Exception {
        Class baseImplClass = initObjects.getImplClassMapper().getImplClass(
                PeripheralInfoGetter.class
        );
        
        return (PeripheralInfoGetter)devObjectFactory.getDeviceObject(
                networkId, nodeId, initObjects.getConnectionStack().getConnector(),
                baseImplClass, initObjects.getConfigSettings().getGeneralSettings()
        );
    }
    
    /**
     * Returns peripheral numbers provided by specified node.
     * @param infoDeviceObj
     * @return set of peripheral numbers the device supports
     */
    private Set<Integer> getPeripheralNumbers(PeripheralInfoGetter infoDeviceObj) 
            throws SimplyException {
        logger.debug("getPeripheralNumbers - start: infoDeviceObj={}", infoDeviceObj);
        
        GettingPeripheralsConfiguration gettingPerConfig 
                = dpaInitConfig.getEnumerationConfiguration().getGettingPeripheralsConfiguration();
        if ( gettingPerConfig == null ) {
            throw new SimplyException("Getting peripherals configuration not available");
        }
        
        int attemptId = 0;
        PeripheralEnumeration perEnum = null;
        while ( (attemptId < gettingPerConfig.getPerAttemptsNum()) && ( perEnum == null ) ) {
            logger.info("Getting peripheral enumeration: {} attempt", attemptId+1);
            
            UUID uid = infoDeviceObj.async_getPeripheralEnumeration();
            if ( uid != null ) {
                perEnum = infoDeviceObj.getCallResult(uid, PeripheralEnumeration.class,
                        gettingPerConfig.getPerTimeout()
                );
            }  
            
            if ( perEnum == null ) { 
                logger.info("State of peripheral enumeration request: " + 
                        infoDeviceObj.getCallRequestProcessingState(uid)
                );
            }
            attemptId++;
        }
        
        if ( perEnum == null ) {
            throw new SimplyException("No response from peripheral enumeration request.");
        }
        
        int[] defaultPerNumbers = perEnum.getDefaultPeripherals();
        int userPerTotal = perEnum.getUserDefPeripheralsNum();
        Set<Integer> allPerNumbers = new HashSet<>();
        for ( int defPerNumber : defaultPerNumbers ) {
            allPerNumbers.add(defPerNumber);
        }
        
        final int STANDARD_PER_NUM = 32;
        for ( int userPerNum = 0; userPerNum < userPerTotal; userPerNum++ ) {
            allPerNumbers.add(STANDARD_PER_NUM + userPerNum);
        }
        
        logger.debug("getPeripheralNumbers - end: {}", allPerNumbers);
        return allPerNumbers;
    }
    
    /**
     * Returns list of bonded nodes IDs.
     * @param coord coordinator to use
     * @return list of bonded nodes IDs
     */
    private List<Integer> getBondedNodesIds(Coordinator coord) throws Exception {
        logger.debug("getBondedNodesIds - start: coord={}", coord);
        
        BondedNodesConfiguration bondedNodesConfig = null;
        switch ( dpaInitConfig.getInitializationType() ) {
            case ENUMERATION:
                bondedNodesConfig = dpaInitConfig.getEnumerationConfiguration().getBondedNodesConfiguration();
                break;
            case FIXED:
                bondedNodesConfig = dpaInitConfig.getFixedInitConfiguration().getBondedNodesConfiguration();
                break;
        }
        
        if ( bondedNodesConfig == null ) {
            throw new SimplyException("No configuration found for bonded nodes.");
        }
        
        int attemptId = 0;
        BondedNodes result = null;
        while ( (attemptId < bondedNodesConfig.getBondedNodesAttemptsNum()) && (result == null) ) {
            logger.info("Get bonded nodes: {} attempt", attemptId+1);
            
            UUID uid = coord.call(Coordinator.MethodID.GET_BONDED_NODES, null);
            if ( uid != null ) {
                result = coord.getCallResult(uid, BondedNodes.class, 
                        bondedNodesConfig.getBondedNodesTimeout()
                );
            }
            attemptId++;
        }
        
        if ( result == null ) {
            throw new Exception("Request for getting bonded nodes failed");
        }
        
        List<Integer> bondedNodesIds = result.getList();
        logger.debug("getBondedNodesIds - end: {}", result.bondedNodesListToString());
        return bondedNodesIds;
    }
    
    /**
     * Creates node for specified nodeId and returns it.
     * @param networkId network ID
     * @param nodeId node ID
     * @return node for specified nodeId
     */
    private DPA_Node createNode(String networkId, String nodeId) throws Exception {
        logger.debug("createNode - start: networkId={}, nodeId={}", networkId, nodeId);
        System.out.println("Creating node " + nodeId + ":");
        
        // creating Peripheral Information object to get all supported peripherals
        PeripheralInfoGetter perInfoObject = createPerInfoObject(networkId, nodeId);
        
        Set<Integer> peripheralNumbers = getPeripheralNumbers(perInfoObject);
        System.out.println("Peripherals: " + Arrays.toString(peripheralNumbers.toArray( new Integer[0])) );
        
        DPA_Node node = NodeFactory.createNode(networkId, nodeId, peripheralNumbers);
        
        System.out.println("Node created\n");
        logger.debug("createNode - end: {}", node);
        
        return node;
    }
    
    // Creates and returns map of nodes, which are bonded to specified coordinator.
    private Map<String, DPA_Node> createBondedNodes(String networkId, List<Integer> bondedNodesIds) 
            throws Exception {
        logger.debug("createBondedNodes - start: networkId={}, master={}", 
                networkId, Arrays.toString(bondedNodesIds.toArray( new Integer[0] ))
        );
        
        // for new line in the printed output
        System.out.println();        
        
        Map<String, DPA_Node> nodesMap = new HashMap<>();
        for ( Integer bondedNodeId : bondedNodesIds ) {
            if ( bondedNodeId > DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX ) {
                continue;
            }
            
            DPA_Node bondedNode = null;
            try {
                bondedNode = createNode(networkId, String.valueOf(bondedNodeId));
            } catch ( Exception e ) {
                throw new Exception("Fail to create bonded node " + bondedNodeId, e);
            }
            
            nodesMap.put(String.valueOf(bondedNodeId), bondedNode);
        }
        
        logger.debug("createBondedNodes - end: {}", nodesMap);
        return nodesMap;
    }
    
    /**
     * Runs process of discovery and returns its results.
     * @param coord coordinator to run discovery on
     * @return discovery result
     */
    private DiscoveryResult runDiscovery(Coordinator coord) throws SimplyException {
        // run discovery process
        System.out.println("Run discovery ...");
        
        DiscoveryConfiguration discConfig = dpaInitConfig.getDiscoveryConfiguration();
        
        // setting connector
        ConnectorService connector = initObjects.getConnectionStack().getConnector();
        ResponseWaitingConnector respWaitConn = (ResponseWaitingConnector)connector;
        
        long prevRespTimeout = respWaitConn.getResponseTimeout();
        respWaitConn.setResponseTimeout(discConfig.discoveryTimeout());
        
        long prevDefaultWaitingTimeout = coord.getDefaultWaitingTimeout();
        coord.setDefaultWaitingTimeout(discConfig.discoveryTimeout() + 2000);
        
        DiscoveryResult discResult = coord.runDiscovery(
                new DiscoveryParams(discConfig.dicoveryTxPower(), 0)
        );
        
        coord.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
        respWaitConn.setResponseTimeout(prevRespTimeout);
        return discResult;
    }
    
    // creates network enumerated using enumeration of devices inside IQRF network
    private Network createEnumeratedNetwork(String networkId, Configuration networkSettings) 
            throws Exception 
    {
        logger.debug("createEnumeratedNetwork - start: networkId={}, networkSettings={}", 
                networkId, networkSettings
        );

        // creating master node
        DPA_Node masterNode = createNode(networkId, "0");
        logger.info("Master node created");
        
        //determine config depending on each network and set to use in protocol layer
        determineAndUseNetworkConfig(networkId, masterNode);
        
        // map of nodes of this network
        Map<String, DPA_Node> nodesMap = null;
        
        // checking, if coordinator is present at the master
        Coordinator masterCoord = masterNode.getDeviceObject(Coordinator.class);
        if ( masterCoord == null ) {
            logger.warn(
                    "Master node doesn't contain Coordinator interface."
                    + "No bonded nodes will be created"
            );
            nodesMap = new HashMap<>();
            nodesMap.put("0", masterNode);
            return new DPA_NetworkImpl(networkId, nodesMap);
        }
        
        EnumerationConfiguration enumConfig = dpaInitConfig.getEnumerationConfiguration();
        if ( enumConfig == null ) {
            throw new SimplyException("Configuration for enumeration not found.");
        }
        
        // getting currently bonded nodes
        List<Integer> bondedNodesIds = null;
        if ( enumConfig.getBondedNodesConfiguration() != null ) {
            bondedNodesIds = getBondedNodesIds(masterCoord);
            System.out.println("Number of bonded nodes: " + bondedNodesIds.size());
            System.out.println("Bonded nodes: " + Arrays.toString(bondedNodesIds.toArray(new Integer[0])));
        } else {
            bondedNodesIds = new LinkedList<>();
        }
        
        // running discovery process
        if ( dpaInitConfig.getDiscoveryConfiguration() != null ) {
            DiscoveryResult discoResult = runDiscovery(masterCoord);
            if ( discoResult == null ) {
                throw new SimplyException("Discovery failed");
            }
            System.out.println("Number of discovered nodes: " + discoResult.getDiscoveredNodesNum());
            
            if ( bondedNodesIds.size() != discoResult.getDiscoveredNodesNum() ) {
                logger.warn(
                        "Number of bonded nodes NOT equal to the number of discovered nodes:"
                        + " bonded nodes number = " + bondedNodesIds.size()
                        + " discovered nodes number = " + discoResult.getDiscoveredNodesNum()
                );
            } 
        }
        
        // creating nodes bonded to the Master node
        nodesMap = createBondedNodes(networkId, bondedNodesIds);
        nodesMap.put("0", masterNode);
        Network network = new DPA_NetworkImpl(networkId, nodesMap);
        
        logger.debug("createEnumeratedNetwork - end: {}", network);
        return network;
    }
    
    // creates nodes map from specified fixed mapping
    private Map<String, DPA_Node> createNodesFromNetworkFuncMapping(
            String networkId, Map<String, Set<Integer>> networkMapping, Set<Integer> bondedNodesIds
    ) throws SimplyException, Exception 
    {
        logger.debug("createNodesFromNetworkFuncMapping - start: networkId={}, networkMapping={}", 
                networkId, networkMapping
        );
        
        FixedInitConfiguration fixedInitConfig = dpaInitConfig.getFixedInitConfiguration();
        if ( fixedInitConfig == null ) {
            throw new SimplyException("Configuration for fixed initialization not found.");
        }
        
        Map<String, DPA_Node> nodesMap = new HashMap<>();
        for ( Map.Entry<String, Set<Integer>> nodeMappingEntry : networkMapping.entrySet() ) {
            int nodeId = Integer.parseInt(nodeMappingEntry.getKey());
            // coordinator was already created
            if ( nodeId == 0 ) {
                continue;
            }
            
            if ( !bondedNodesIds.contains(nodeId) ) {
                logger.warn("Node " + nodeId + " not bonded. Representation will not be created." );
                continue;
            }
            
            System.out.println("Creating node " + nodeId + ":");
            System.out.println("Peripherals: " + Arrays.toString(nodeMappingEntry.getValue().toArray( new Integer[0])) );
            
            DPA_Node node = NodeFactory.createNode(
                    networkId, nodeMappingEntry.getKey(), nodeMappingEntry.getValue()
            );
            nodesMap.put(nodeMappingEntry.getKey(), node);
            
            System.out.println("Node created");
        }
        
        logger.debug("createNodesFromNetworkFuncMapping - end: {}", nodesMap);
        return nodesMap;
    }
    
    // creates network defined by fixed entity
    private Network createFixedNetwork(String networkId, Configuration networkSettings) 
            throws Exception {
        logger.debug("createFixedNetwork - start: networkId={}, networkSettings={}", 
                networkId, networkSettings
        );
        
        FixedInitConfiguration fixedInitConfig = dpaInitConfig.getFixedInitConfiguration();
        if ( fixedInitConfig == null ) {
            throw new SimplyException("Fixed initialization configuration is missing."); 
        }
        
        Map<String, Set<Integer>> networkMapping 
                = fixedInitConfig.getNetworksFunctionalityToSimplyMapping().getMapping().get(networkId);
        if ( networkMapping == null ) {
            throw new SimplyException(
                "Mapping of functionality for network " + networkId + " not available."
            );
        }
        
        // creating master node
        DPA_Node masterNode = NodeFactory.createNode(networkId, "0", networkMapping.get("0"));
        logger.info("Master node created");                        
        
        //determine config depending on each network and set to use in protocol layer
        determineAndUseNetworkConfig(networkId, masterNode);        
        
        // checking, if coordinator is present at the master
        Coordinator masterCoord = masterNode.getDeviceObject(Coordinator.class);
        if ( masterCoord == null ) {
            logger.warn(
                    "Master node doesn't contain Coordinator interface."
                    + "No bonded nodes will be created"
            );
            Map<String, Node> nodesMap = new HashMap<>();
            nodesMap.put("0", masterNode);
            return new BaseNetwork(networkId, nodesMap);
        }
        
        // getting currently bonded nodes
        List<Integer> bondedNodesIds = null;
        if ( fixedInitConfig.getBondedNodesConfiguration() != null ) {
            bondedNodesIds = getBondedNodesIds(masterCoord);
            System.out.println("Number of bonded nodes: " + bondedNodesIds.size());
            System.out.println("Bonded nodes: " + Arrays.toString(bondedNodesIds.toArray(new Integer[0])));
        } else {
            bondedNodesIds = new LinkedList<>();
        }
        
        // running discovery process
        if ( dpaInitConfig.getDiscoveryConfiguration() != null ) {
            DiscoveryResult discoResult = runDiscovery(masterCoord);
            if ( discoResult == null ) {
                throw new SimplyException("Discovery failed");
            }
            System.out.println("Number of discovered nodes: " + discoResult.getDiscoveredNodesNum());
            
            if ( bondedNodesIds.size() != discoResult.getDiscoveredNodesNum() ) {
                logger.warn(
                        "Number of bonded nodes NOT equal to the number of discovered nodes:"
                        + " bonded nodes number = " + bondedNodesIds.size()
                        + " ,discovered nodes number = " + discoResult.getDiscoveredNodesNum()
                );
            } 
        }
        
        // creating nodes bonded to the Master node
        Map<String, DPA_Node> nodesMap = createNodesFromNetworkFuncMapping(
                networkId, networkMapping, new HashSet<>(bondedNodesIds)
        );
        nodesMap.put("0", masterNode);
        Network network = new DPA_NetworkImpl(networkId, nodesMap);
        
        logger.debug("createFixedNetwork - end: {}", network);
        return network;
    }
    
    
    /**
     * Creates and returns new network - according to specified settings.
     * @param networkId ID of created network
     * @param networkSettings settings of created network
     * @return network
     */
    private Network createNetwork(String networkId, Configuration networkSettings) 
            throws Exception {
        logger.debug("createNetwork - start: networkId={}, networkSettings={}", 
                networkId, networkSettings
        );
        
        System.out.println("Creating network " + networkId + " ...");
        
        Network network = null;
        
        switch ( dpaInitConfig.getInitializationType() ) {
            case ENUMERATION:
                network = createEnumeratedNetwork(networkId, networkSettings);
                break;
            case FIXED:
                network = createFixedNetwork(networkId, networkSettings);
                break;
            default:
                throw new SimplyException(
                        "Unsupported initialization type: " + dpaInitConfig.getInitializationType()
                );
        }
        
        System.out.println("Network " + networkId + " successfully created.");
        
        logger.debug("createNetwork - end: {}", network);
        return network;
    }
    
    @Override
    public Map<String, Network> initialize(
            DPA_InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> initObjects
    ) throws Exception {
        logger.debug("initialize - start: innerObjects={}", initObjects);
        System.out.println("Starting initialization of Simply ...");
        
        this.initObjects = initObjects;
        this.dpaInitConfig = DPA_InitializerConfigurationFactory.
                getDPA_InitializerConfiguration(initObjects.getConfigSettings().getGeneralSettings()
        );
        
        // starting the connector
        this.initObjects.getConnectionStack().start();
            
        // result map of networks
        Map<String, Network> networksMap = new HashMap<>();
        
        // initialize each network
        Map<String, Configuration> networksSettings = initObjects.getConfigSettings().getNetworksSettings(); 
        for ( Map.Entry<String, Configuration> networkEntry : networksSettings.entrySet() ) {
            Network network = createNetwork(networkEntry.getKey(), networkEntry.getValue());
            networksMap.put(networkEntry.getKey(), network);
        }                       
        System.out.println("Initialization of Simply complete.");
        
        logger.info("Initialization complete");
        logger.debug("initialize - end");
        return networksMap;
    }
}
