
package com.microrisc.simply.iqrf.dpa.v201.init;

import com.microrisc.simply.BaseNetwork;
import com.microrisc.simply.BaseNode;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.DeviceObject;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.SimpleDeviceObjectFactory;
import com.microrisc.simply.connector.response_waiting.ResponseWaitingConnector;
import com.microrisc.simply.init.AbstractInitializer;
import com.microrisc.simply.iqrf.dpa.v201.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v201.devices.PeripheralInfoGetter;
import com.microrisc.simply.iqrf.dpa.v201.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.v201.types.DiscoveryParams;
import com.microrisc.simply.iqrf.dpa.v201.types.DiscoveryResult;
import com.microrisc.simply.iqrf.dpa.v201.types.PeripheralEnumeration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates inicialization process of DPA based networks.
 * 
 * @author Michal Konopa
 */
public final class DPA_Initializer 
extends AbstractInitializer<SimpleDPA_InitObjects, Network> {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_Initializer.class);
    
    
    /** Inner objects. */
    private SimpleDPA_InitObjects initObjects = null;
    
    /** Device object factory. */
    private SimpleDeviceObjectFactory devObjectFactory = new SimpleDeviceObjectFactory();
    
    /** Configuration settings for initializer. */
    private DPA_InitializerConfiguration dpaInitConfig = null;
    
    
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
     * @return array of peripheral numbers the device supports
     */
    private int[] getPeripheralNumbers(PeripheralInfoGetter infoDeviceObj) 
            throws SimplyException {
        logger.debug("getPeripheralNumbers - start: infoDeviceObj={}", infoDeviceObj);
        
        EnumerationConfiguration enumConfig = dpaInitConfig.getEnumerationConfiguration();
        if ( enumConfig == null ) {
            throw new SimplyException("No enumeration configuration found");
        }
        
        int attemptId = 0;
        PeripheralEnumeration perEnum = null;
        while ( (attemptId < enumConfig.getPerAttemptsNum()) && ( perEnum == null ) ) {
            logger.info("Getting peripheral enumeration: {} attempt", attemptId+1);
            
            UUID uid = infoDeviceObj.async_getPeripheralEnumeration();
            if ( uid != null ) {
                perEnum = infoDeviceObj.getCallResult(uid, PeripheralEnumeration.class,
                        enumConfig.getPerTimeout()
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
        int[] allPerNumbers = new int[defaultPerNumbers.length + userPerTotal];
        System.arraycopy(defaultPerNumbers, 0, allPerNumbers, 0, defaultPerNumbers.length);
        
        final int STANDARD_PER_NUM = 32;
        for ( int userPerNum = 0; userPerNum < userPerTotal; userPerNum++ ) {
            allPerNumbers[defaultPerNumbers.length + userPerNum] = STANDARD_PER_NUM + userPerNum;
        }
        
        logger.debug("getPeripheralNumbers - end: {}", allPerNumbers);
        return allPerNumbers;
    }
    
    /**
     * For specified node ID creates map of services and returns it.
     * @param networkId ID of network, which the node belongs to
     * @param nodeId ID of node
     */
    private Map<Class, DeviceObject> createNodeServices(String networkId, String nodeId) 
            throws Exception {
        logger.debug("createNodeServices - start: nodeId={}", nodeId);
        
        System.out.println("Creating node services: ");
        
        // services map
        Map<Class, DeviceObject> services = new HashMap<>();
        
        // creating Peripheral Information object
        PeripheralInfoGetter perInfoObject = createPerInfoObject(networkId, nodeId);
            
        // put info object into service's map
        services.put(PeripheralInfoGetter.class, (DeviceObject)perInfoObject);
        
        // getting all supported services
        int[] perNumbers = getPeripheralNumbers(perInfoObject);
        
        System.out.print("Services: ");
        for (int perId : perNumbers) {
            System.out.print(perId + " ");
            
            Class devIface = initObjects.getPeripheralToDevIfaceMapper().
                    getDeviceInterface(String.valueOf(perId)
            );
            
            // IMPORTANT: if no device interface for specified peripheral number is found,
            // continue, not throw an exception
            if ( devIface == null ) {
                logger.warn("Interface not found for peripheral: {}", perId);
                continue;
            }
            
            Class implClass = initObjects.getImplClassMapper().getImplClass(devIface);
            if ( implClass == null ) {
                throw new RuntimeException("Implementation for " + devIface.getName() + " not found");
            }
            
            // creating new device object for implementation class
            DeviceObject newDeviceObj = devObjectFactory.getDeviceObject(
                    networkId, nodeId, initObjects.getConnectionStack().getConnector(), 
                    implClass, initObjects.getConfigSettings().getGeneralSettings()
            );
            
            // put object into service's map
            services.put(devIface, newDeviceObj);
        }
        
        System.out.println("\nNode services created");
        logger.debug("createNodeServices - end");
        return services;
    }
    
    /**
     * Returns list of bonded nodes IDs.
     * @param coord coordinator to use
     * @return 
     */
    private List<Integer> getBondedNodesIds(Coordinator coord) throws Exception {
        logger.debug("getBondedNodesIds - start: coord={}", coord);
        
        BondedNodesConfiguration bondedNodesConfig = dpaInitConfig.getBondedNodesConfiguration();
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
     * @return 
     */
    Node createNode(String networkId, String nodeId) throws Exception {
        logger.debug("createNode - start: networkId={}, nodeId={}", networkId, nodeId);
        System.out.println("Creating node " + nodeId + ":");
        
        Map<Class, DeviceObject> nodeServices = createNodeServices(networkId, nodeId);
        Node node = new BaseNode(networkId, nodeId, nodeServices);
        
        System.out.println("Node created\n");
        logger.debug("createNode - end: {}", node);
        return node;
    }
    
    /**
     * Creates and returns map of nodes, which are bonded to specified coordinator.
     * @param coord 
     */
    private Map<String, Node> createBondedNodes(String networkId, List<Integer> bondedNodesIds) 
            throws Exception {
        logger.debug("createBondedNodes - start: networkId={}, master={}", 
                networkId, Arrays.toString(bondedNodesIds.toArray(new Integer[0]))
        );
        
        // for new line in the printed output
        System.out.println();
        
        // maximal node number to use
        final int MAX_BONDED_NODE_NUMBER = 0xEF;
        
        Map<String, Node> nodesMap = new HashMap<>();
        for ( Integer bondedNodeId : bondedNodesIds ) {
            if (bondedNodeId > MAX_BONDED_NODE_NUMBER) {
                break;
            }
            
            Node bondedNode = null;
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
        System.out.println("Run discovery...");
        
        DiscoveryConfiguration discConfig = dpaInitConfig.getDiscoveryConfiguration();
        if ( discConfig == null ) {
            throw new SimplyException("No configuration for discovery found");
        }
        
        // setting connector
        ConnectorService connector = initObjects.getConnectionStack().getConnector();
        ResponseWaitingConnector respWaitConn = (ResponseWaitingConnector)connector;
        
        long prevRespTimeout = respWaitConn.getResponseTimeout();
        respWaitConn.setResponseTimeout(discConfig.discoveryTimeout());
        
        coord.setDefaultWaitingTimeout(discConfig.discoveryTimeout() + 2000);
        DiscoveryResult discResult = coord.runDiscovery(
                new DiscoveryParams(discConfig.dicoveryTxPower(), 0)
        );
        
        respWaitConn.setResponseTimeout(prevRespTimeout);
        return discResult;
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
                networkId, networkSettings);
        System.out.println("Creating network " + networkId + " ...");
        
        // creating master node
        Node masterNode = createNode(networkId, "0");
        logger.info("Master node created");
        
        // map of nodes of this network
        Map<String, Node> nodesMap = null;
        
        // checking, if coordinator is present at the master
        Coordinator masterCoord = masterNode.getDeviceObject(Coordinator.class);
        if ( masterCoord == null ) {
            logger.warn("Master node doesn't contain Coordinator interface."
                    + "No bonded nodes will be created");
            nodesMap = new HashMap<>();
            nodesMap.put("0", masterNode);
            return new BaseNetwork(networkId, nodesMap);
        }
        
        // getting currently bonded nodes
        List<Integer> bondedNodesIds = null;
        if ( dpaInitConfig.getBondedNodesConfiguration() != null ) {
            bondedNodesIds = getBondedNodesIds(masterCoord);
            System.out.println("Bonded nodes: " + Arrays.toString(bondedNodesIds.toArray(new Integer[0])));
        } else {
            bondedNodesIds = new LinkedList<>();
        }
        
        // running discovery process
        if ( dpaInitConfig.getDiscoveryConfiguration() != null ) {
            DiscoveryResult discoResult = runDiscovery(masterCoord);
            if (discoResult == null) {
                throw new SimplyException("Discovery failed");
            }
            System.out.println("Discovered nodes: " + discoResult.getDiscoveredNodesNum());
        }
        
        // setting routing hops
        /*
        RoutingHopsConfiguration routingHopsConfig = dpaInitConfig.getRoutingHopsConfiguration();
        if ( routingHopsConfig != null ) {
            System.out.println("Setting routing hops: ... ");
            RoutingHops prevRoutingHops = masterCoord.setHops(
                    new RoutingHops(routingHopsConfig.getRequestHops(), routingHopsConfig.getResponseHops())
            );
            System.out.println("Previous routing hops: " + prevRoutingHops);
        }
	*/
        
        // creating nodes bonded to Master node
        nodesMap = createBondedNodes(networkId, bondedNodesIds);
        nodesMap.put("0", masterNode);
        Network network = new BaseNetwork(networkId, nodesMap);
        
        System.out.println("Network " + networkId + " successfully created.");
        
        logger.debug("createNetwork - end: {}", network);
        return network;
    }
    
    @Override
    public Map<String, Network> initialize(SimpleDPA_InitObjects initObjects)
            throws Exception {
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
            Network network = createNetwork(
                    networkEntry.getKey(), networkEntry.getValue()
            );
            networksMap.put(networkEntry.getKey(), network);
        }
        System.out.println("Initialization of Simply complete.");
        
        logger.info("Initialization complete");
        logger.debug("initialize - end");
        return networksMap;
    }
}
