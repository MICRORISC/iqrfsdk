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

package com.microrisc.simply.iqrf.dpa.v22x.autonetwork;

import com.microrisc.simply.BaseNetwork;
import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.Network;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.di_services.WaitingTimeoutService;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;
import com.microrisc.simply.iqrf.dpa.protocol.ProtocolObjects;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDR;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Node;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.init.NodeFactory;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNode;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Parameter;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Request;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveredNodes;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveryParams;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveryResult;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Configuration;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Configuration.FRC_RESPONSE_TIME;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Data;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Prebonding;
import com.microrisc.simply.iqrf.dpa.v22x.types.LED_State;
import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.dpa.v22x.types.RoutingHops;
import com.microrisc.simply.iqrf.types.VoidType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link AutoNetworkAlgorithm} interface.
 * 
 * @author Michal Konopa
 * @author Martin Strouhal
 */
// June 2015 - Martin - implemented getNodes()
public final class AutoNetworkAlgorithmImpl implements AutoNetworkAlgorithm {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AutoNetworkAlgorithmImpl.class);
    
    
    /** Minimal discovery TX power. */
    public static final int DISCOVERY_TX_POWER_MIN = 0;
    
    /** Maximal discovery TX power. */
    public static final int DISCOVERY_TX_POWER_MAX = 7;
    
    /** Default discovery TX. */
    public static final int DISCOVERY_TX_POWER_DEFAULT = 4;
    
    
    /** Minimal prebonding interval [ in seconds ]. */
    public static final int PREBONDING_INTERVAL_MIN = 0;
    
    /** Maximal prebonding interval [ in seconds ]. */
    public static final int PREBONDING_INTERVAL_MAX = (0xFFFF-1) / 100;
    
    /** Default prebonding interval [ in seconds ]. */
    public static final long PREBONDING_INTERVAL_DEFAULT = 15;
    
    
    /** Default number of retries to authorize new bonded node. */
    public static final int AUTHORIZE_RETRIES_DEFAULT = 1;
    
    /** Default number of retries to run the discovery process after one iteration of algorithm. */
    public static final int DISCOVERY_RETRIES_DEFAULT = 1;
    
    
    /** Minimal timeout for node to hold temporary address [ in tens of seconds ]. */
    public static final long TEMPORARY_ADDRESS_TIMEOUT_MIN = 0;
    
    /** Maximal timeout for node to hold temporary address [ in tens of seconds ]. */
    public static final long TEMPORARY_ADDRESS_TIMEOUT_MAX = 0xFFFF-1;
    
    /** Default timeout for node to hold temporary address [in tens of seconds ]. */
    public static final long TEMPORARY_ADDRESS_TIMEOUT_DEFAULT = 60;
    
    
    /** 
     * Default indicator, wheather to use FRC automatically in checking 
     * the accessability of new bonded nodes. 
     */
    public static final boolean AUTOUSE_FRC_DEFAULT = true;
    
    /** 
     * Denotes, that the number of nodes to bond will be maximal - according to
     * the IQRF DPA network size limitations.  
     */
    public static final int NODES_NUMBER_TO_BOND_MAX = -1;
    
    
    // TX power for discovery process
    private final int discoveryTxPower;
    
    // time interval [in ms] for prebonding
    private final long prebondingInterval;
    private final int authorizeRetries;
    private final int discoveryRetries;
    
    // timeout [in ms] for holding temporary address
    private final long temporaryAddressTimeout;
    
    // use FRC automatically in checking the accessibility of new bonded nodes
    private final boolean autoUseFrc;
    
    // method ID transformer for P2P Prebonder
    private final MethodIdTransformer p2pPrebonderMethodIdTransformer;
    
    // number of nodes to bond
    private int numberOfNodesToBond;
    
    
    // checkers
    private static int checkDiscoveryTxPower(int discoveryTxPower) {
        if ( ( discoveryTxPower < DISCOVERY_TX_POWER_MIN ) 
              || ( discoveryTxPower > DISCOVERY_TX_POWER_MAX ) 
        ) {
            throw new IllegalArgumentException(
                "Discovery TX power must be in the " + DISCOVERY_TX_POWER_MIN 
                + ".."  + DISCOVERY_TX_POWER_MAX + " interval."
            );
        }
        return discoveryTxPower;
    }
    private static long checkPrebondingInterval(long prebondingInterval) {
        if ( prebondingInterval < 15 ) {
            throw new IllegalArgumentException("Prebonding interval cannot be lower than 15s.");
        }
        return prebondingInterval;
    }
    
    private static int checkAuthorizeRetries(int authorizeRetries) {
        if ( authorizeRetries < 0 ) {
            throw new IllegalArgumentException(
                "Number of retries to authorize new bonded node cannot be negative."
            );
        }
        return authorizeRetries;
    }
    
    private static int checkDiscoveryRetries(int discoveryRetries) {
        if ( discoveryRetries < 0 ) {
            throw new IllegalArgumentException(
                "Number of discovery retries cannot be negative."
            );
        }
        return discoveryRetries;
    }
    
    private static long checkTemporaryAddressTimeout(long temporaryAddressTimeout) {
        if ( temporaryAddressTimeout < 0 || temporaryAddressTimeout >= 0xFFFF ) {
            throw new IllegalArgumentException(
                "Temporary address timeout must be within the interval of ["
                + TEMPORARY_ADDRESS_TIMEOUT_MIN + ".." + TEMPORARY_ADDRESS_TIMEOUT_MAX
                + "]. Got: " + temporaryAddressTimeout    
            );
        }
        return temporaryAddressTimeout;
    }
    
    private static MethodIdTransformer checkP2PPrebonderMethodIdTransformer(
            MethodIdTransformer p2pPrebonderMethodIdTransformer
    ) {
        if ( p2pPrebonderMethodIdTransformer == null ) {
            throw new IllegalArgumentException(
                    "P2P Prebonder Method ID transformer cannot be null"
            );
        }
        return p2pPrebonderMethodIdTransformer;
    }
    
    private static int checkNumberOfNodesToBond(int numberOfNodesToBond) {
        if ( numberOfNodesToBond != NODES_NUMBER_TO_BOND_MAX 
                && 
            !(numberOfNodesToBond >= 0 && numberOfNodesToBond <= DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX)  
        ) {
            throw new IllegalArgumentException(
                "Number of nodes to bond must be in the interval of "
                + "[" + 0 + ".." +  DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX + "]"
                + " or equal to " + NODES_NUMBER_TO_BOND_MAX
            );
        }
        return numberOfNodesToBond;
    }
    
    /**
     * Builder for {@code AutoNetworkAlgorithmImpl} class.
     */
    public static class Builder {
        // required parameters
        private final Network network;
        private final BroadcastServices broadcastServices;
        
        // optional parameters
        private int discoveryTxPower = DISCOVERY_TX_POWER_DEFAULT;
        private long prebondingInterval = PREBONDING_INTERVAL_DEFAULT;
        private int authorizeRetries = AUTHORIZE_RETRIES_DEFAULT;
        private int discoveryRetries = DISCOVERY_RETRIES_DEFAULT;
        private long temporaryAddressTimeout = TEMPORARY_ADDRESS_TIMEOUT_DEFAULT;
        private boolean autoUseFrc = AUTOUSE_FRC_DEFAULT;
        private MethodIdTransformer p2pPrebonderMethodIdTransformer = null;
        private int numberOfNodesToBond = NODES_NUMBER_TO_BOND_MAX;
        
        /**
         * Creates the builder object.
         * @param network reference to network to use, must be != {@code null}
         * @param broadcastServices reference to broadcast services to use, must be != {@code null}
         */
        public Builder(Network network, BroadcastServices broadcastServices) {
            this.network = network;
            this.broadcastServices = broadcastServices;
        }
        
        /**
         * Sets value of discovery TX power.
         * @param val must be within the interval of 
         *      [DISCOVERY_TX_POWER_MIN..DISCOVERY_TX_POWER_MAX]
         * @return reference to this builder
         */
        public Builder discoveryTxPower(int val) {
            this.discoveryTxPower = val;
            return this;
        }
        
        /**
         * Sets value of prebonding interval.
         * @param val must be within the interval of 
         *      [PREBONDING_INTERVAL_MIN..PREBONDING_INTERVAL_MAX]
         * @return reference to this builder
         */
        public Builder prebondingInterval(long val) {
            this.prebondingInterval = val;
            return this;
        }
        
        /**
         * Sets value of authorize retries.
         * @param val must be nonnegative
         * @return reference to this builder
         */
        public Builder authorizeRetries(int val) {
            this.authorizeRetries = val;
            return this;
        }
        
        /**
         * Sets value of discovery retries.
         * @param val must be nonnegative
         * @return reference to this builder
         */
        public Builder discoveryRetries(int val) {
            this.discoveryRetries = val;
            return this;
        }
        
        /**
         * Sets value of temporary address timeout.
         * @param val must be within the interval of 
         *      [TEMPORARY_ADDRESS_TIMEOUT_MIN..TEMPORARY_ADDRESS_TIMEOUT_MAX]
         * @return reference to this builder
         */
        public Builder temporaryAddressTimeout(long val) {
            this.temporaryAddressTimeout = val;
            return this;
        }
        
        /**
         * Sets if to autouse of FRC.
         * @param val {@code true} if to use the autouse of FRC <br>
         *            {@code false} if not to use
         * @return reference to this builder
         */
        public Builder autoUseFrc(boolean val) {
            this.autoUseFrc = val;
            return this;
        }
        
        /**
         * Sets value of P2P Prebonder method ID transformer.
         * @param val must be != {@code null}
         * @return reference to this builder
         */
        public Builder p2pPrebonderMethodIdTransformer(MethodIdTransformer val) {
            this.p2pPrebonderMethodIdTransformer = val;
            return this;
        }
        
        /**
         * Sets number of nodes to bond.
         * @param val must be either {@code NODES_NUMBER_TO_BOND_MAX} or 
         *      within the interval of 
         *      [0..DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX] 
         * @return reference to this builder
         */
        public Builder numberOfNodesToBond(int val) {
            this.numberOfNodesToBond = val;
            return this;
        }
        
        /**
         * Builds according to specified settings and returns object of the 
         * Autonetwork algorithm. All settings are checked before the final object
         * is built.
         * @return object of the Autonetwork algorithm
         */
        public AutoNetworkAlgorithmImpl build() {
            return new AutoNetworkAlgorithmImpl(this);
        }
    }
    
    
    // network allowing dynamic changes in its structure
    private static class DynamicNetwork implements Network {
        private final String id;
        private final Map<String, com.microrisc.simply.Node> nodesMap;
    
    
        public DynamicNetwork(String id, Map<String, com.microrisc.simply.Node> nodesMap) {
            this.id = id;
            this.nodesMap = nodesMap;
        }
    
        @Override
        public String getId() {
            return id;
        }

        @Override
        public com.microrisc.simply.Node getNode(String nodeId) {
            return nodesMap.get(nodeId);
        }

        @Override
        public Map<String, com.microrisc.simply.Node> getNodesMap() {
            return new HashMap<>(nodesMap);
        }
        
        @Override
        public com.microrisc.simply.Node[] getNodes(String[] nodeIds){
            com.microrisc.simply.Node[] nodes = new com.microrisc.simply.Node[nodeIds.length];
            for(int i = 0; i < nodeIds.length; i++){
                nodes[i] = getNode(nodeIds[i]);
            }
            return nodes;
        }
        
        public void addNode(com.microrisc.simply.Node node) {
            nodesMap.put(node.getId(), node);
        }
        
        public void destroy() {
            nodesMap.clear();
        }
        
    }
    
    /** Network to start the algorithm with. */
    private final Network network;
    
    private static Network checkNetwork(Network network) {
        if ( network == null ) {
            throw new IllegalArgumentException("Initial network cannot be null");
        }
        return network;
    }
    
    /** Result network. */
    private final DynamicNetwork resultNetwork;
    
    /** Synchronization object for resultNetwork. */
    private final Object synchroResultNetwork = new Object();
  
    // returns copy of the specified network
    private Network getNetworkCopy(Network srcNetwork) {
        return new BaseNetwork( srcNetwork.getId(), new HashMap<>(srcNetwork.getNodesMap()) );
    }
    
    // creates dynamic network from source network
    private DynamicNetwork createDynamicNetwork(Network srcNetwork) {
        return new DynamicNetwork( srcNetwork.getId(), new HashMap<>(srcNetwork.getNodesMap()) );
    }
     
    // broadcast services
    private final BroadcastServices broadcastServices;
    
    private static BroadcastServices checkBroadcastServices(BroadcastServices broadcastServices) 
    {
        if ( broadcastServices == null ) {
            throw new IllegalArgumentException("Broadcast services cannot be null");
        }
        return broadcastServices;
    }
    
    
    /**
     * State of the algorithm
     */
    public static enum State {
        PREPARED,
        RUNNING,
        FINISHED_OK,
        CANCELLED,
        ERROR
    }
    
    // actual state of the algorithm
    private State actualState = State.PREPARED;
    
    // synchronization object for actualState
    private final Object synchroActualState = new Object();
    
    // sets the actual state
    private void setState(State newState) {
        synchronized ( synchroActualState ) {
            actualState = newState;
        }
    }
    
    
    // thread running the algorithm
    private class AlgoThread extends Thread {
        @Override
        public void run() {
            runAlgorithm();
        }
    }
    
    // algo thread
    private Thread algoThread = null;
    
    // timeout to wait for worker threads to join
    private static final long JOIN_WAIT_TIMEOUT = 2000;
    
    /**
     * Terminates algo thread.
     */
    private void terminateAlgoThread() {
        logger.debug("terminateAlgoThread - start:");
        
        // termination signal to algo thread
        algoThread.interrupt();
        
        // indicates, wheather this thread is interrupted
        boolean isInterrupted = false;
        
        try {
            if ( algoThread.isAlive() ) {
                algoThread.join(JOIN_WAIT_TIMEOUT);
            }
        } catch ( InterruptedException e ) {
            isInterrupted = true;
            logger.warn("Algo thread terminating - thread interrupted");
        }
        
        if ( !algoThread.isAlive() ) {
            logger.info("Algo thread stopped.");
        }
        
        if ( isInterrupted ) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("algo thread stopped.");
        logger.debug("terminateAlgoThread - end");
    }
    
    
    
    /** Bonded nodes. */
    private BondedNodes bondedNodes = null;
    
    /** Discovered nodes. */
    private DiscoveredNodes discoveredNodes = null;
    
    
    
    // prints specified bytes to hex string from most significant byte 
    private String toHexaFromLastByteString(short[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for ( int byteId = bytes.length-1; byteId >= 0; byteId-- ) {
            String hexString = Integer.toHexString(bytes[byteId]);
            if ( hexString.length() < 2 ) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        sb.append(']');
        
        return sb.toString();
    }
    
    // returns next free address
    private int nextFreeAddr(BondedNodes bondedNodes, int from ) throws Exception {
        int origAddr = from;

        for ( ; ; ) {
            if ( ++from > DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX ) {
                from = 1;
            }
            
            if ( !bondedNodes.isBonded(from) ) {
                return from;
            }
            
            if ( origAddr == from ) {
                throw new Exception( "NextFreeAddr: no free address" );
            }
        }
    }
    
    
    // updates information about nodes
    private void updateNodesInfo(Coordinator coordinator) throws Exception {
        logger.debug("updateNodesInfo - start: coordinator={}", coordinator);
        
        this.bondedNodes = coordinator.getBondedNodes();
        if ( bondedNodes == null ) {
            throw new Exception("Error while getting bonded nodes.");
        }
        
        logger.info("Bonded nodes: {}", getGentleListOfNodes(this.bondedNodes.getList()));
        
        this.discoveredNodes = coordinator.getDiscoveredNodes();
        if ( discoveredNodes == null ) {
            throw new Exception("Error while getting discovered nodes.");
        }
        
        logger.info("Discovered nodes: {}", getGentleListOfNodes(this.discoveredNodes.getList()));
        
        List<Integer> notDiscovered = new LinkedList<>();
        for ( int bondedNodeId : bondedNodes.getList() ) {
            if ( !discoveredNodes.isDiscovered(bondedNodeId) ) {
                notDiscovered.add(bondedNodeId);
            }
        }
        logger.info("NOT discovered nodes: {}", getGentleListOfNodes(notDiscovered));
        logger.debug("updateNodesInfo - end: ");
    }
    
    // checks, if there are some discovered nodes which are not bonded
    private boolean checkUnbondedNodes () {
        List<Integer> notBonded = new LinkedList<>();
        for ( int nodeAddr = 1; 
              nodeAddr <= DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX; 
              nodeAddr++ 
        ) {
            if ( discoveredNodes.isDiscovered(nodeAddr) && !bondedNodes.isBonded(nodeAddr) ) {
                notBonded.add(nodeAddr);
            }
        }
        
        if ( !notBonded.isEmpty() ) {
            logger.warn("Nodes {} are discovered but not bonded. Discover the network!", 
                    StringUtils.join(notBonded, ',')
            );
            return false;
        }
        return true;
    }
    
    // returns value of logarithm of base 2 for the specified value
    private int log2(int value) {
        if ( value == 0 ) {
            return 0;
        }
        return 31 - Integer.numberOfLeadingZeros(value);
    }
    
    // puts together both parts of incomming FRC data
    private static short[] getCompleteFrcData(short[] firstPart, short[] extraData) {
        short[] completeData = new short[firstPart.length + extraData.length];
        System.arraycopy(firstPart, 0, completeData, 0, firstPart.length);
        System.arraycopy(extraData, 0, completeData, firstPart.length, extraData.length);
        return completeData;
    }
    
    // comparing Node Ids
    private static class NodeIdComparator implements Comparator<String> {
        @Override
        public int compare(String nodeIdStr1, String nodeIdStr2) {
            int nodeId_1 = Integer.decode(nodeIdStr1);
            int nodeId_2 = Integer.decode(nodeIdStr2);
            return Integer.compare(nodeId_1, nodeId_2);
        }
    }
    
    // Node Id comparator
    private static final NodeIdComparator nodeIdComparator = new NodeIdComparator();
    
    // sorting specified results according to node ID in ascendent manner
    private static SortedMap<String, FRC_Prebonding.Result> sortFrcResult(
            Map<String, FRC_Prebonding.Result> result
    ) {
        TreeMap<String, FRC_Prebonding.Result> sortedResult = new TreeMap<>(nodeIdComparator);
        sortedResult.putAll(result);
        return sortedResult;
    }
    
    
    // stores information about elements used to send P2P packet to allow prebonding
    private static class P2PPrebondingInfo {
        Class p2pSender;
        DeviceInterfaceMethodId methodId;
        
        P2PPrebondingInfo(
                Class p2pSender, DeviceInterfaceMethodId methodId
        ) {
            this.p2pSender = p2pSender;
            this.methodId = methodId;
        }
    }
    
    private static final int FIRST_USER_PERIPHERAL = 0x20;
    
    // returns method ID annotated as P2PSenderMethodId 
    private DeviceInterfaceMethodId getP2PSenderMethodId(Class p2pSender) {
        // find annotated Method enum constant
        Class[] pubClasses = p2pSender.getClasses();
        for ( Class pubClass : pubClasses ) {
            if ( !pubClass.isEnum() ) {
                continue;
            }

            // get implemented interfaces
            Class[] ifaces = pubClass.getInterfaces();
            
            for ( Class iface : ifaces ) {
                if ( iface != DeviceInterfaceMethodId.class ) {
                    continue;
                }

                Field[] fields = pubClass.getFields();
                for ( Field field : fields ) {
                    if ( field.getAnnotation(P2PSenderMethodId.class) != null ) {
                        return (DeviceInterfaceMethodId)Enum.valueOf(pubClass, field.getName());
                    }
                }
            }
        }
        return null;
    }
    
    private P2PPrebondingInfo getP2PPrebondingInfo() throws Exception {
       Class p2pSender = ProtocolObjects.getPeripheralToDevIfaceMapper()
               .getDeviceInterface(FIRST_USER_PERIPHERAL);
       if ( p2pSender == null ) {
           throw new Exception("Could not found first user peripheral.");
       }
       
       DeviceInterfaceMethodId methodId = getP2PSenderMethodId(p2pSender);
       if ( methodId == null ) {
           throw new Exception("Could not found P2PSenderMethodId.");
       }
       
       return new P2PPrebondingInfo(p2pSender, methodId);
    }
    
    // activates prebonding on coordinator and nodes
    private void prebond(OS coordOs, Coordinator coordinator, P2PPrebondingInfo p2pInfo) 
            throws Exception 
    {
        logger.debug("prebond - start: coordOs={}, coordinator={}, p2pInfo={}",
                coordOs, coordinator, p2pInfo
        );
        
        int bondingMask = (bondedNodes.getNodesNumber() == 0) ? 
            0 : (int)Math.pow( 2, 1 + (int)log2( bondedNodes.getNodesNumber()) ) - 1;
        if ( bondingMask > 0xFF ) {
            bondingMask = 0xFF;
        }
        
        // 100 ms unit
        int wTimeout = (int)( ( (long)temporaryAddressTimeout * 1000 ) / 100 );
        
        // how long to wait for prebonding [ in seconds ]
        
        // real bonding time equals to: NominalBondingTime - 0.06 * ( NumberOfNodes + 4 ) [seconds]. 
        // we recommend to have real bonding time minimally 10s in order to let not yet bonded nodes 
        // to have enough time get bonded. Longer bonding time is always recommended.

        // minimal NominalBondingTime is 15  (derived from: 0.06 * (239 + 4) = 14.58 s)
        // maximal NominalBondingTime is 655 (derived from: 16.bit number with 10ms unit)
        
        // different approaches can be implemented to adjust the waiting time
        // e.g. linear interpolation between min a max values wrt to already bonded nodes
        
        // x1 (min) = 15 s              (input variable)
        // x2 (max) = 60 s              (input variable)
        // y1 (min) = 0 bonded nodes    (fixed for calculation below)
        // y2 (max) = 239 bonded nodes  (fixed for calculation below)
        
        // x = (y + (239/(x2-x1))*x1)/(239/(x2-x1))
        // e.g. for y=20 already bonded nodes => x = (20 + 5.3*15)/5.3 => 18.8s
        
        // set min and max waiting time values and make linear interpolation between them
        //short x1 = 15;
        //short x2 = 60;
        //int waitBonding = Math.round((bondedNodes.getNodesNumber() + (239/(x2-x1)) * x1)/(239/(x2-x1)));
        
        // simple approach of having constant waiting time during the network formation
        int waitBonding = (int)prebondingInterval;
        
        // 10 ms unit
        int waitBonding10ms = waitBonding * 100;

        logger.info(
            "Enable prebonding, mask = {}, address timeout = {}, and LEDR=1 at Nodes and Coordinator",
            Integer.toBinaryString(bondingMask), temporaryAddressTimeout
        );
        
        UUID nodesEnableUid = null;
        if ( bondedNodes.getNodesNumber() > 0 ) {
            String networkId = null;
            synchronized ( synchroResultNetwork ) {
                networkId = resultNetwork.id;
            }
            
            nodesEnableUid = broadcastServices.sendRequest(
                networkId, 
                OS.class, 
                OS.MethodID.BATCH,
                new Object[] {
                    new DPA_Request[] {
                        new DPA_Request(
                                LEDR.class, 
                                LEDR.MethodID.SET, 
                                new Object[] { LED_State.ON }, 
                                0xFFFF
                        ),
                        new DPA_Request(
                                Node.class, 
                                Node.MethodID.ENABLE_REMOTE_BONDING, 
                                new Object[] { 
                                    bondingMask,
                                    1,
                                    new short[] { 
                                        (short)(wTimeout >> 0), 
                                        (short)(wTimeout >> 8)
                                    }
                                }, 
                                0xFFFF 
                        ),
                        new DPA_Request(
                                p2pInfo.p2pSender, 
                                p2pInfo.methodId, 
                                new Object[] { 
                                    new short[] { 
                                        0x55, 
                                        (short)(waitBonding10ms >> 0 ), 
                                        (short)(waitBonding10ms >> 8 ), 
                                        (short)(bondedNodes.getNodesNumber() + 3) 
                                    } 
                                }, 
                                0xFFFF,
                                p2pPrebonderMethodIdTransformer
                        )
                    }
                }
            );
            
            if ( nodesEnableUid == null ) {
                throw new Exception(
                    "Error while sending request for enabling remote bonding on nodes"
                );
            }
        
            // wait the whole above broadcast and all peer2peer LP slots
            Thread.sleep( 
                ( bondedNodes.getNodesNumber() + 1 ) * 40 + bondedNodes.getNodesNumber()  * 60 
            );
        }
        
        UUID coordEnableUid = coordOs.call(
                OS.MethodID.BATCH, 
                new Object[] { 
                    new DPA_Request[] { 
                        new DPA_Request(
                                Coordinator.class, 
                                Coordinator.MethodID.ENABLE_REMOTE_BONDING,
                                new Object[] { 
                                    bondingMask, 
                                    1,
                                    new short[] { 
                                        (short)(wTimeout >> 0), 
                                        (short)(wTimeout >> 8)
                                    }
                                }, 
                                0xFFFF 
                        ),
                        new DPA_Request(
                                p2pInfo.p2pSender, 
                                p2pInfo.methodId, 
                                new Object[] { 
                                    new short[] { 
                                        0x55, 
                                        (short)(waitBonding10ms >> 0 ), 
                                        (short)(waitBonding10ms >> 8 ), 
                                        1 
                                    } 
                                }, 
                                0xFFFF,
                                p2pPrebonderMethodIdTransformer
                        )
                    }
                } 
        );
        
        if ( coordEnableUid == null ) {
            throw new Exception(
                "Error while sending request for enabling remote bonding on coordinator"
            );
        }

        logger.info("Waiting for prebonding for {} seconds ... ", waitBonding);

        try {
            Thread.sleep(waitBonding * 1000 + 1000);
        } catch ( InterruptedException ex ) {
            logger.error("Prebonding interrupted");
            if ( bondedNodes.getNodesNumber() > 0 ) {
                logger.info("Disable prebonding at nodes");
                
                String networkId = null;
                synchronized ( synchroResultNetwork ) {
                    networkId = resultNetwork.id;
                }
                BroadcastResult nodesDisablingResult = broadcastServices.broadcast(
                    networkId, Node.class, Node.MethodID.ENABLE_REMOTE_BONDING,
                        new Object[] { 0, 0, new short[] { 0, 0 } }
                );
                if ( nodesDisablingResult == null ) {
                    throw new Exception("Error while disabling remote bonding on nodes");
                }
            }

            logger.info("Disable coordinator prebonding");
            VoidType coordDisablingResult = coordinator.enableRemoteBonding(
                    0, 0, new short[] { 0, 0 }
            );
            if ( coordDisablingResult == null ) {
                throw new Exception("Error while disabling remote bonding on coordinator");
            }
            
            throw ex;
        }
        
        // getting results of enabling remote bonding
        if ( nodesEnableUid != null ) {
            BroadcastResult nodesEnableResult = broadcastServices
                    .getBroadcastResultImmediately(nodesEnableUid);
            if ( nodesEnableResult == null ) {
                throw new Exception(
                        "Result not available for enabling remote bonding on nodes. "
                        + "Current state: " 
                        + broadcastServices.getCallRequestProcessingState(nodesEnableUid)
                );
            }
        }
        
        VoidType coordEnableResult = coordOs.getCallResultImmediately(coordEnableUid, VoidType.class);
        if ( coordEnableResult == null ) {
            throw new Exception(
                    "Result not available for enabling remote bonding on coordinator. "
                    + "Current state: " + coordOs.getCallRequestProcessingState(coordEnableUid)
            );
        }
        
        logger.debug("prebond - end: ");
    }
    
    // disables prebonding and returns results of the disabling request
    private Map<String, FRC_Prebonding.Result> disablePrebonding(com.microrisc.simply.Node coordNode) 
            throws Exception 
    {
        FRC coordFrc = coordNode.getDeviceObject(FRC.class);
        if ( coordFrc == null ) {
            throw new Exception("Could not find FRC on coordinator node.");
        }
        
        // For FRC peripheral can be set timeout following way otherwise timeout is unlimited:
        // timeout for IQRF = Bonded Nodes x 130 + _RESPONSE_FRC_TIME_xxx_MS + 250 [ms]
        // + overhead for the Java framework (for threads) = 2000 [ms]
        final short overhead = 2000;
        long timeout = bondedNodes.getNodesNumber() * 130 + (long)FRC_Configuration.FRC_RESPONSE_TIME.TIME_40_MS.getRepsonseTimeInInt() + 250 + overhead;
        coordFrc.setDefaultWaitingTimeout(timeout);
 
        // set FRC response time to be sure it is set correctly
        coordFrc.setFRCParams( new FRC_Configuration(FRC_RESPONSE_TIME.TIME_40_MS) );
        
        FRC_Data frcData = coordFrc.send( new FRC_Prebonding( new short[] { 0x01, 0x00 }) );
        if ( frcData == null ) {
            throw new Exception("Error while disabling prebonding.");
        }
        
        Thread.sleep(100);
        
        short[] extraData = coordFrc.extraResult();
        if ( extraData == null ) {
            throw new Exception("Error while disabling prebonding - getting extra data.");
        }

        return FRC_Prebonding.parse(getCompleteFrcData(frcData.getData(), extraData));
    }
    
    // returns list of nodes, which provided prebonding
    private List<Integer> getPrebondingNodes(Map<String, FRC_Prebonding.Result> frcResult) {
        List<Integer> prebondingNodes = new LinkedList<>();
        for ( int nodeAddr = 1;
              nodeAddr <= DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX;
              nodeAddr++
        ) {
            FRC_Prebonding.Result nodeResult = frcResult.get(Integer.toString(nodeAddr));
            if ( nodeResult.getBit1() == 0x01 ) {
                prebondingNodes.add(nodeAddr);
            }
        }
        return prebondingNodes;
    }
    
    // reads prebonded MIDs from prebonding nodes and adds them into specified list 
    private void addPrebondedMIDsFromPrebondingNodes(
            List<Integer> prebondingNodes, List<RemotelyBondedModuleId> prebondedMIDs
    ) throws Exception {
        for ( int nodeAddr : prebondingNodes ) {
            com.microrisc.simply.Node node = null;
            synchronized ( synchroResultNetwork ) {
                node = resultNetwork.getNode(Integer.toString(nodeAddr));
            }
            
            if ( node == null ) {
                throw new Exception("Node " + nodeAddr + " not available.");
            }

            Node nodeIface = node.getDeviceObject(Node.class);
            if ( nodeIface == null ) {
                throw new Exception("Node interface at " + nodeAddr + " node not found.");
            }

            RemotelyBondedModuleId remoBondedModuleId = nodeIface.readRemotelyBondedModuleId();
            
            // getting lowest 2 bytes of module ID
            short[] lowest2bytes = new short[2];
            System.arraycopy(remoBondedModuleId.getModuleId(), 0, lowest2bytes, 0, 2);
            
            if (remoBondedModuleId != null) {
                logger.info("Node {} prebonded MID={}, UserData={}",
                        nodeAddr, toHexaFromLastByteString(remoBondedModuleId.getModuleId()),
                        toHexaFromLastByteString(remoBondedModuleId.getUserData())
                );

                if (!prebondedMIDs.contains(remoBondedModuleId)) {

                    boolean duplicate = false;

                    // check all modules in the list
                    for (RemotelyBondedModuleId moduleId : prebondedMIDs) {

                        // getting lowest 2 bytes of module ID
                        short[] lowest2bytesfromlist = new short[2];
                        System.arraycopy(moduleId.getModuleId(), 0, lowest2bytesfromlist, 0, 2);

                        // double check that there are no modules with same ID (2 lowest bytes)
                        boolean result = Arrays.equals(lowest2bytesfromlist, lowest2bytes);

                        if (result) {
                            // if the lowest 2 bytes are same then remove even the module which is in the list already
                            duplicate = true;
                            prebondedMIDs.remove(moduleId);
                            logger.info("Prebonded MID={} removed from the list, same 2B of MID.", toHexaFromLastByteString(moduleId.getModuleId()));
                        }
                    }

                    // add module only with unique ID (2B) otherwise they would get same address during authorization
                    if (!duplicate) {
                        // adding module into list for authorization
                        prebondedMIDs.add(remoBondedModuleId);
                        logger.info("Prebonded MID={} added to the list.", toHexaFromLastByteString(remoBondedModuleId.getModuleId()));
                    }
                }
            } else {
                logger.error("Unable to read prebonded MID from node {}", nodeAddr);
            }
        }
    }
    
    private String getGentleListOfNodes(List<Integer> nodesList) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[");
        sb.append(nodesList.size());
        sb.append("] ");
        
        ListIterator<Integer> iter = nodesList.listIterator();
        int prevNodeId = -1;
        int intervalBegin = -1;
        boolean inInterval = false;
        
        while ( iter.hasNext() ) {
            if ( prevNodeId == -1 ) {
                prevNodeId = iter.next();
                if ( !iter.hasNext() ) {
                    sb.append(prevNodeId);    
                }
                continue;
            }
            
            int nodeId = iter.next();
            
            if ( (nodeId - prevNodeId) == 1 ) {
                if ( !inInterval ) {
                    intervalBegin = prevNodeId;
                    inInterval = true;
                    if ( !iter.hasNext() ) {
                        sb.append('<');
                        sb.append(intervalBegin);
                        sb.append('-');
                        sb.append(nodeId);
                        sb.append('>');
                    }
                } else {
                    if ( !iter.hasNext() ) {
                        sb.append('<');
                        sb.append(intervalBegin);
                        sb.append('-');
                        sb.append(nodeId);
                        sb.append('>');
                    }
                }
            } else {
                if ( inInterval ) {
                    sb.append('<');
                    sb.append(intervalBegin);
                    sb.append('-');
                    sb.append(prevNodeId);
                    sb.append('>');
                    sb.append(", "); 
                    inInterval = false;
                } else {
                    sb.append(prevNodeId);
                    sb.append(", "); 
                }
                
                if ( !iter.hasNext() ) {
                    sb.append(nodeId);
                }
            }
            
            prevNodeId = nodeId;
        }
        
        if ( sb.substring(sb.length()-2).equals(", ") ) {
            sb.delete(sb.length()-2, sb.length());
        }
        
        return sb.toString();
    }
    
    private void logSortedPrebondDisablingResult(
        SortedMap<String, FRC_Prebonding.Result> sortedPrebondDisablingResult ) 
    {
        StringBuilder sb = new StringBuilder();
        List<Integer> bit0Nodes = new LinkedList<>(); 
        List<Integer> bit1Nodes = new LinkedList<>(); 
        
        for ( Map.Entry<String, FRC_Prebonding.Result> dataEntry 
                : sortedPrebondDisablingResult.entrySet()
        ) {
            if ( dataEntry.getValue().getBit0() == 1 ) {
                bit0Nodes.add(Integer.valueOf(dataEntry.getKey()));
            }
            
            if ( dataEntry.getValue().getBit1() == 1 ) {
                bit1Nodes.add(Integer.valueOf(dataEntry.getKey()));
            }
        }
        
        sb.append("bit0: ");
        sb.append(getGentleListOfNodes(bit0Nodes));
        
        sb.append('\n');
        
        sb.append("bit1: ");
        sb.append(getGentleListOfNodes(bit1Nodes));
        
        logger.info("{}", sb.toString());
    }
    
    // returns the list of prebonded MIDs
    private List<RemotelyBondedModuleId> getPrebondedMIDs(
            Coordinator coordinator, com.microrisc.simply.Node coordNode
    ) throws Exception 
    {
        logger.debug("getPrebondedMIDs - start: coordinator={}, coordNode={}",
                coordinator, coordNode
        );
        List<RemotelyBondedModuleId> prebondedMIDs = new LinkedList<>();
        
        RemotelyBondedModuleId remoBondedModuleId = coordinator.readRemotelyBondedModuleId();
        if ( remoBondedModuleId != null ) {
            logger.info(
                "Coordinator prebonded MID={}, UserData={}", 
                toHexaFromLastByteString(remoBondedModuleId.getModuleId()), 
                toHexaFromLastByteString(remoBondedModuleId.getUserData())
            );
            prebondedMIDs.add(remoBondedModuleId);
        } else {
            logger.error("Unable to read prebonded MID from coordinator");
        }
        
        if ( bondedNodes.getNodesNumber() == 0 ) {
            logger.debug("getPrebondedMIDs - end: {}", StringUtils.join(prebondedMIDs, ','));
            return prebondedMIDs;
        }
        
        logger.info(
            "Running FRC to disable and check for prebonding ( bit 0 is 1 when "
            + "node is accessible; bit1 is 1 if the node provided pre-bonding to a new node )"
        );
        
        Map<String, FRC_Prebonding.Result> prebondDisablingResult = disablePrebonding(coordNode);
                    
        // sort the disabling result
        SortedMap<String, FRC_Prebonding.Result> sortedPrebondDisablingResult 
                = sortFrcResult(prebondDisablingResult);
        
        logSortedPrebondDisablingResult(sortedPrebondDisablingResult);
        
        // getting prebonding nodes
        List<Integer> prebondingNodes = getPrebondingNodes(sortedPrebondDisablingResult);        
        if ( prebondingNodes.isEmpty() ) {
            logger.info("No node prebonded.");
            logger.debug("getPrebondedMIDs - end: []");
            return prebondedMIDs;
        }
        
        logger.info("Nodes provided prebonding: {}", getGentleListOfNodes(prebondingNodes)) ;
        
        // adding prebonded MIDs from prebonding nodes
        addPrebondedMIDsFromPrebondingNodes(prebondingNodes, prebondedMIDs);
        
        logger.debug("getPrebondedMIDs - end: {}", StringUtils.join(prebondedMIDs, ','));
        return prebondedMIDs;
    }
    
    
    // authorizes bonds
    private List<Integer> authorizeBonds(
            Coordinator coordinator, List<RemotelyBondedModuleId> prebondedMIDs
    ) throws Exception {
        logger.debug("authorizeBonds - start: coordinator={}, prebondedMIDs={}",
                coordinator, prebondedMIDs
        );
        
        List<Integer> newAddrs = new LinkedList<>();
        int nextAddr = DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX;

        for ( RemotelyBondedModuleId moduleId : prebondedMIDs ) {
            if ( newBondedNodesCount == numberOfNodesToBond ) {
                logger.info(
                    "Required number of new bonded nodes reached. Authorization"
                    + " prematurely finished. "
                );
                break;
            }
            
            // getting lowest 2 bytes of module ID
            short[] lowest2bytes = new short[2];
            System.arraycopy(moduleId.getModuleId(), 0, lowest2bytes, 0, 2);
            
            for ( int authorizeRetry = authorizeRetries; authorizeRetry != 0; authorizeRetry-- ) {
                if ( authorizeRetry == authorizeRetries ) {
                    nextAddr = nextFreeAddr(bondedNodes, nextAddr);
                }
                
                BondedNode bondedNode = coordinator.authorizeBond(nextAddr, lowest2bytes);
                if ( bondedNode == null ) {
                    
                    logger.error(
                        "Authorizing node {}, retries={}, authorization failed ...", 
                        toHexaFromLastByteString(moduleId.getModuleId()),
                        authorizeRetry
                    );
                    
                    // last retry
                    if ( authorizeRetry == 1 ) {
                        Integer devCount = coordinator.removeBondedNode(nextAddr);
                        if ( devCount == null ) {
                            logger.error("Error while removing bond");
                        }
                    }
                    
                    continue;
                }
                
                logger.info(
                    "Authorizing node {}, address={}, devices count={}, waiting to finish authorization...", 
                    toHexaFromLastByteString(moduleId.getModuleId()),
                    bondedNode.getBondedAddress(), 
                    bondedNode.getBondedNodesNum()
                );
                
                // waiting with the possibility of interruption
                Thread.sleep( bondedNodes.getNodesNumber() * 60 + 150 );
                
                newAddrs.add(Integer.valueOf(bondedNode.getBondedAddress()));
                updateNodesInfo(coordinator);
            }
        }
        
        logger.debug("authorizeBonds - end: {}", StringUtils.join(newAddrs, ','));
        return newAddrs;
    }
    
    // checks new nodes, removes the nonresponding ones
    // returns IDs of nodes, which are responding
    private List<Integer> checkNewNodes(
            String networkId, Coordinator coordinator, com.microrisc.simply.Node coordNode, List<Integer> newAddrs
    ) throws Exception {
        logger.debug("checkNewNodes - start: coordinator={}, coordNode={}, newAddrs={}",
                coordinator, coordNode, StringUtils.join(newAddrs, ',')
        );
        
        List<Integer> respondingNodes = new LinkedList<>();
        
        FRC coordFrc = coordNode.getDeviceObject(FRC.class);
        if ( coordFrc == null ) {
            throw new Exception("FRC peripheral could not been found on coordinator");
        }
        
        // For FRC peripheral can be set timeout following way otherwise timeout is unlimited:
        // timeout for IQRF = Bonded Nodes x 130 + _RESPONSE_FRC_TIME_xxx_MS + 250 [ms]
        // + overhead for the Java framework (for threads) = 2000 [ms]
        final short overhead = 2000;
        long timeout = bondedNodes.getNodesNumber() * 130 + (long)FRC_Configuration.FRC_RESPONSE_TIME.TIME_40_MS.getRepsonseTimeInInt() + 250 + overhead;
        coordFrc.setDefaultWaitingTimeout(timeout);
 
        // set FRC response time to be sure it is set correctly for prebonding
        coordFrc.setFRCParams( new FRC_Configuration(FRC_RESPONSE_TIME.TIME_40_MS) );
        
        FRC_Data frcData = coordFrc.send( new FRC_Prebonding( new short[] { 0x01, 0x00 }) );
        if ( frcData == null ) {
            throw new Exception("Error while checking new nodes.");
        }
        
        Thread.sleep(100);
        
        short[] extraData = coordFrc.extraResult();
        if ( extraData == null ) {
            throw new Exception("Error while checking new nodes - getting extra data.");
        }
        
        short[] frcDataCheck = getCompleteFrcData(frcData.getData(), extraData);
        
        for ( int newAddr : newAddrs ) {
            if ( ( ( frcDataCheck[0 + newAddr / 8] >> ( newAddr % 8 ) ) & 0x01 ) == 0x00 )
            {
                logger.warn("Removing bond {}", newAddr);
                
                // creating new node
                com.microrisc.simply.Node newNode 
                = NodeFactory.createNodeWithAllPeripherals(networkId, Integer.toString(newAddr));
                               
                OS os = newNode.getDeviceObject(OS.class);
                if ( os == null ) {
                    throw new Exception("OS peripheral could not been found on the node");
                }
                
                VoidType result = os.batch(
                    new DPA_Request[] { 
                        new DPA_Request( Node.class, Node.MethodID.REMOVE_BOND, new Object[] {}, 0xFFFF ),
                        new DPA_Request( OS.class, OS.MethodID.RESTART, new Object[] {}, 0xFFFF ) }
                );
                
                if( result == null ) {
                    logger.error("Removing bond of the remote node {} failed", newAddr);
                }
                
                // Wait for sure
                Thread.sleep(( bondedNodes.getNodesNumber() + 1 ) * (40 + 40));
                Integer bondedNodesNum = coordinator.removeBondedNode(newAddr);
                if (bondedNodesNum == null) {
                    logger.error("Removing bond at coordinator {} failed", newAddr);
                }
            } else {
                respondingNodes.add(newAddr);
            }
        }
        
        logger.debug("checkNewNodes - end: {}", getGentleListOfNodes(respondingNodes));
        return respondingNodes;
    }
    
    // remove nodes with temporary address 0xFE
    private void forceRemovalofNodesWithTemporaryAddress() throws Exception {
        UUID nodesRemoveTAUid = null;
        
        if ( bondedNodes.getNodesNumber() > 0 ) {
            String networkId = null;
            
            logger.info("Removing nodes with temporary address 0xFE");
            
            synchronized ( synchroResultNetwork ) {
                networkId = resultNetwork.id;
            }
            
            // reference to all nodes with temporary address 0xFE
            com.microrisc.simply.Node temporaryNodes 
                = NodeFactory.createNodeWithAllPeripherals(networkId, Integer.toString(0xFE));
            
            // reference to OS peripheral
            OS tnsOS = temporaryNodes.getDeviceObject(OS.class);
            
            // issue a batch for 0xFE nodes
            nodesRemoveTAUid = tnsOS.call(
                OS.MethodID.BATCH,
                new Object[] {
                    new DPA_Request[] {
                        new DPA_Request( Node.class, Node.MethodID.REMOVE_BOND, new Object[] {}, 0xFFFF ),
                        new DPA_Request( OS.class, OS.MethodID.RESTART, new Object[] {}, 0xFFFF )
                    }
                }
            );
            
            if ( nodesRemoveTAUid == null ) {
                throw new Exception(
                    "Error while sending request for removing nodes with temporary address"
                );
            }
        
            // wait the whole above broadcast and little bit more
            Thread.sleep(
                ( ( bondedNodes.getNodesNumber() + 1 ) * 60 ) + 150 
            );
        }
    }
    
    // runs discovery
    private void runDiscovery(Coordinator coordinator) throws Exception {
        logger.debug("runDiscovery - start: coordinator={}", coordinator);
        
        for ( int discoveryRetry = discoveryRetries; discoveryRetry != 0; discoveryRetry-- ) {
            UUID uid = coordinator.call(
                    Coordinator.MethodID.RUN_DISCOVERY, 
                    new Object[] { new DiscoveryParams(discoveryTxPower, 0) } 
            );
            
            if ( uid == null ) {
                throw new Exception("Request for running discovery failed.");
            }
            
            while ( true ) {
                CallRequestProcessingState procState = coordinator.getCallRequestProcessingState(uid);
                if ( procState == null ) {
                    throw new Exception("Error while getting state of processing during discovery");
                }
                
                switch ( procState ) {
                    case CANCELLED:
                        throw new Exception("Discovery was cancelled");
                    case ERROR:
                        CallRequestProcessingError procError = coordinator.getCallRequestProcessingError(uid);
                        if ( procError != null ) {
                            throw new Exception("Error during discovery: " + procError.getErrorType());
                        }
                        throw new Exception("Error during discovery");
                    case RESULT_ARRIVED:
                        break;
                    case WAITING_FOR_PROCESSING:
                    case WAITING_FOR_RESULT:
                        Thread.sleep(1000);
                }
                if ( procState == CallRequestProcessingState.RESULT_ARRIVED ) {
                    break;
                }
            }
            
            // how to determine the discovery timeout?
            DiscoveryResult discoResult = coordinator
                    .getCallResultInDefaultWaitingTimeout(uid, DiscoveryResult.class);
            
            if ( discoResult == null ) {
                logger.error("Discovery failed.");
                continue;
            }

            logger.info("Discovered {} nodes", discoResult.getDiscoveredNodesNum());
            updateNodesInfo(coordinator);
            if ( discoResult.getDiscoveredNodesNum() == bondedNodes.getNodesNumber() ) {
                break;
            }
        }
        
        logger.debug("runDiscovery - end: ");
    }
    
    // creates and adds new nodes into result network
    private void addNewNodesWithAllPeripherals(List<Integer> newAddrs) throws Exception {
        logger.debug("addNewNodesWithAllPeripherals - start: newAddrs={}", 
                StringUtils.join(newAddrs, ',')
        );
        
        String networkId = null;
        synchronized ( synchroResultNetwork ) {
            networkId = resultNetwork.id;
        }
        
        for ( int addr : newAddrs ) {
            com.microrisc.simply.Node newNode 
                = NodeFactory.createNodeWithAllPeripherals(networkId, Integer.toString(addr));
            
            synchronized ( synchroResultNetwork ) {
                resultNetwork.addNode(newNode);
            } 
        }
        
        logger.debug("addNewNodesWithAllPeripherals - end:");
    }
    
    // number of nodes already bonded to the network from the start of the algorithm
    private int newBondedNodesCount = 0;
    
    // indicates, wheather there is currently bonded the user specified number of nodes
    // in the network
    private boolean isBondedRequiredNumberOfNodes() {
        if ( numberOfNodesToBond == NODES_NUMBER_TO_BOND_MAX ) {
            if ( bondedNodes.getNodesNumber() 
                    == DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX 
            ) {
                return true;
            }
            return false;
        }
        
        if ( numberOfNodesToBond == newBondedNodesCount ) {
            return true;
        }
        return false;
    }
    
    
    // performs the algorithm
    private void runAlgorithm() {
        logger.debug("runAlgorithm - start: ");
        
        setState(State.RUNNING);
        
        DateTime startTime = new DateTime();
        logger.info("Automatic network construction started at {}", startTime.toString("HH:mm:ss"));
        
        logger.info("Finding coordinator");
        
        com.microrisc.simply.Node coordNode = null;
        synchronized ( synchroResultNetwork ) {
            coordNode = resultNetwork.getNode(
                Integer.toString(DPA_ProtocolProperties.NADR_Properties.IQMESH_COORDINATOR_ADDRESS)
            );
        }
        
        if ( coordNode == null ) {
            setState(State.ERROR);
            logger.error("Coordinator node not found.");
            logger.debug("runAlgorithm - end");
            return; 
        }
        
        Coordinator coordinator = coordNode.getDeviceObject(Coordinator.class);
        if ( coordinator == null ) {
            setState(State.ERROR);
            logger.error("Coordinator interface not found.");
            logger.debug("runAlgorithm - end");
            return;
        }
        
        // storing previous value to be able to restore it later
        long prevDefaultWaitingTimeout = coordinator.getDefaultWaitingTimeout();
        
        // IMPORTANT: SET DEFAULT TIMEOUT FOR GETTING RESULT TO 'UNLIMITED', 
        // DPA TIMING IS HANDLED BY THE TIMING STATE MACHINE
        coordinator.setDefaultWaitingTimeout( WaitingTimeoutService.UNLIMITED_WAITING_TIMEOUT );
        
        OS coordOs = coordNode.getDeviceObject(OS.class);
        if ( coordOs == null ) {
            setState(State.ERROR);
            logger.error("OS interface not found.");
            logger.debug("runAlgorithm - end");
            return;
        }
        
        coordinator.setRequestHwProfile(DPA_ProtocolProperties.HWPID_Properties.DO_NOT_CHECK);
        coordOs.setRequestHwProfile(DPA_ProtocolProperties.HWPID_Properties.DO_NOT_CHECK);
        
        logger.info("Initial network check");
        try {
            updateNodesInfo(coordinator);
        } catch ( Exception ex ) {
            setState(State.ERROR);
            coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
            logger.error("Update nodes info error: {}", ex );
            logger.debug("runAlgorithm - end");
            return;
        }
        
        int origNodesCount = bondedNodes.getNodesNumber();
        if ( !checkUnbondedNodes() ) {
            setState(State.ERROR);
            coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
            logger.debug("runAlgorithm - end");
            return;
        }
        
        logger.info("Number of hops set to the number of routers");
        RoutingHops prevRoutingHops = coordinator.setHops( new RoutingHops(0xFF, 0xFF) );
        if ( prevRoutingHops == null ) {
            setState(State.ERROR);
            coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
            logger.error("Error while setting hops");
            logger.debug("runAlgorithm - end");
        }
        
        logger.info("No LED indication and use of optimal time slot length");
        DPA_Parameter prevParam = coordinator.setDPA_Param( 
                new DPA_Parameter(DPA_Parameter.DPA_ValueType.LAST_RSSI, false, false) 
        );
        if ( prevParam == null ) {
            setState(State.ERROR);
            coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
            logger.error("Error while setting DPA parameter");
            logger.debug("runAlgorithm - end");
        }
        
        P2PPrebondingInfo p2pPrebondInfo = null;
        try {
            // information needed to send P2P packet to allow prebonding
            p2pPrebondInfo = getP2PPrebondingInfo();
        } catch ( Exception ex ) {
            setState(State.ERROR);
            coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
            logger.error("Error while getting info about P2P Sender peripheral: {}", ex );
            logger.debug("runAlgorithm - end");
        }
        
        logger.info("Automatic network construction in progress");
        
        // adjusting number of nodes to bond with respect to the number of nodes
        // currently bonded to the network
        if ( ( bondedNodes.getNodesNumber() + numberOfNodesToBond ) 
                >  
            DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX  
        ) {
            numberOfNodesToBond = DPA_ProtocolProperties.NADR_Properties.IQMESH_NODE_ADDRESS_MAX
                    - bondedNodes.getNodesNumber();
        }
        
        int round = 1;
        while ( 
            !isBondedRequiredNumberOfNodes()
        ) {
            if ( Thread.interrupted() ) {
                setState(State.CANCELLED);
                coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
                logger.info("Algorithm interrupted");
                logger.debug("runAlgorithm - end");
                return;
            }
            
            PeriodFormatter perFormatter = new PeriodFormatterBuilder()
                    .printZeroAlways()
                    .appendHours()
                    .appendSuffix(" hour", " hours")
                    .appendSeparator(" ")
                    .appendMinutes()
                    .appendSuffix(" minute", " minutes")
                    .appendSeparator(" ")
                    .appendSeconds()
                    .appendSuffix(" second", " seconds")
                    .toFormatter();
            logger.info(
                    "Round={}, All nodes={}, New nodes from start={}, Time={}", 
                    round, 
                    bondedNodes.getNodesNumber(), 
                    bondedNodes.getNodesNumber() - origNodesCount,
                    perFormatter.print( new Period( startTime, new DateTime() ))
            );
            
            try {
                // do prebonding
                prebond(coordOs, coordinator, p2pPrebondInfo);

                // get prebonded MIDs
                List<RemotelyBondedModuleId> prebondedMIDs = getPrebondedMIDs(coordinator, coordNode);

                // authorize bonded nodes
                List<Integer> newAddrs = authorizeBonds(coordinator, prebondedMIDs);

                // no new addresses authorized - continue with next iteration
                if ( newAddrs.isEmpty() ) {
                    round++;
                    continue;
                }
            
                if ( autoUseFrc ) {
                    logger.info("Running FRC to check new nodes and removing 0xFE nodes");
                    newAddrs = checkNewNodes(resultNetwork.getId(), coordinator, coordNode, newAddrs);
                    forceRemovalofNodesWithTemporaryAddress();
                }

                if(!newAddrs.isEmpty()) {
                    logger.info( "Running discovery ...");
                    runDiscovery(coordinator);
                }
                
                // adding new bonded nodes into network
                addNewNodesWithAllPeripherals(newAddrs);
                newBondedNodesCount += newAddrs.size();
            } catch ( InterruptedException e ) {
                setState(State.CANCELLED);
                coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
                logger.warn("Algorithm cancelled");
                logger.debug("runAlgorithm - end");
                return;
            } catch ( Exception e ) {
                setState(State.ERROR);
                coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
                logger.error("Error while running algorithm: ", e);
                logger.debug("runAlgorithm - end");
                return;
            }
            round++;
        }
        
        coordinator.setDefaultWaitingTimeout(prevDefaultWaitingTimeout);
        setState(State.FINISHED_OK);
        logger.debug("runAlgorithm - end");
    }
    
    
    /**
     * Creates new object of the network building algorithm.
     */
    private AutoNetworkAlgorithmImpl(Builder builder) {
        this.network = checkNetwork(builder.network);
        this.resultNetwork = createDynamicNetwork(network);
        
        this.broadcastServices = checkBroadcastServices(builder.broadcastServices);
        
        this.discoveryTxPower = checkDiscoveryTxPower(builder.discoveryTxPower);
        this.prebondingInterval = checkPrebondingInterval(builder.prebondingInterval);
        this.authorizeRetries = checkAuthorizeRetries(builder.authorizeRetries);
        this.discoveryRetries = checkDiscoveryRetries(builder.discoveryRetries);
        this.temporaryAddressTimeout = checkTemporaryAddressTimeout(builder.temporaryAddressTimeout);
        this.autoUseFrc = builder.autoUseFrc;
        this.p2pPrebonderMethodIdTransformer = checkP2PPrebonderMethodIdTransformer(
                builder.p2pPrebonderMethodIdTransformer
        );
        this.numberOfNodesToBond = checkNumberOfNodesToBond(builder.numberOfNodesToBond);
        
        this.algoThread = new AlgoThread();
    }
    
    // indication, if the algorithm has already been started
    private boolean started = false;
    
    // indication, if the algorithm has already been canceled
    private boolean canceled = false;
    
    
    @Override
    public void start() {
        logger.debug("start - start: ");
        
        if ( started ) {
            throw new IllegalStateException("Algorithm cannot be started more than once.");
        }
        
        algoThread.start();
        
        started = true;
        logger.info("Started");
        logger.debug("start - end");
    }
    
    
    public State getState() {
        synchronized ( synchroActualState ) {
            return actualState;
        }
    }
    
    public boolean isPrepared() {
        synchronized ( synchroActualState ) {
            return ( actualState == State.PREPARED );
        }
    }
    
    public boolean isRunning() {
        synchronized ( synchroActualState ) {
            return ( actualState == State.RUNNING );
        }
    }
    
    @Override
    public boolean isFinished() {
        synchronized ( synchroActualState ) {
            return ( 
                actualState == State.FINISHED_OK
                || actualState == State.CANCELLED
                || actualState == State.ERROR
            );
        }
    }
    
    public boolean isCancelled() {
        synchronized ( synchroActualState ) {
            return ( actualState == State.CANCELLED );
        }
    }
    
    public boolean isError() {
        synchronized ( synchroActualState ) {
            return ( actualState == State.ERROR );
        }
    }
    
    @Override
    public void cancel() {
        logger.debug("cancel - start:");
        
        if ( canceled ) {
            throw new IllegalStateException("Algorithm cannot be cancelled more than once.");
        }
        
        terminateAlgoThread();
        algoThread = null;
        
        canceled = true;
        logger.info("Cancelled.");
        logger.debug("cancel - end");
    }
    
    /**
     * Returns the network, which is the result of the algorithm's running. It
     * is perfectly possible to call this method even if the algorithm is still
     * running - the method returns the actual result. <br>
     * The returned value is the copy of the network instance the algorithm
     * is running on. 
     * @return result network
     */
    public Network getResultNetwork() {
        synchronized ( synchroResultNetwork ) {
            return getNetworkCopy(resultNetwork);
        }
    }
    
}
