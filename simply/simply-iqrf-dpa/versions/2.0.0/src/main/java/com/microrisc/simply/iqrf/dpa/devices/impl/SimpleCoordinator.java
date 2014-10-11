
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers.CoordinatorStandardTransformer;
import com.microrisc.simply.iqrf.dpa.types.AddressingInfo;
import com.microrisc.simply.iqrf.dpa.types.BondedNode;
import com.microrisc.simply.iqrf.dpa.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.types.DPA_Parameter;
import com.microrisc.simply.iqrf.dpa.types.DiscoveredNodes;
import com.microrisc.simply.iqrf.dpa.types.DiscoveryParams;
import com.microrisc.simply.iqrf.dpa.types.DiscoveryResult;
import com.microrisc.simply.iqrf.dpa.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.dpa.types.RoutingHops;
import com.microrisc.simply.iqrf.dpa.types.SubDPARequest;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;
import org.apache.commons.lang.ArrayUtils;

/**
 * Simple Coordinator implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleCoordinator 
extends DPA_DeviceObject implements Coordinator {
   
    public SimpleCoordinator(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((Coordinator.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }
        
        if ( args == null ) {
            return dispatchCall( methodIdStr, new Object[] { getRequestHwProfile() } );
        }
        
        Object[] argsWithHwProfile = new Object[ args.length + 1 ];
        argsWithHwProfile[0] = getRequestHwProfile();
        System.arraycopy( args, 0, argsWithHwProfile, 1, args.length );
        return dispatchCall( methodIdStr, argsWithHwProfile);
    }
    
    @Override
    public String transform(Object methodId) {
        return CoordinatorStandardTransformer.getInstance().transform(methodId);
    }
    
    @Override
    public AddressingInfo getAddressingInfo() {
        UUID uid = dispatchCall("1", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout());
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, AddressingInfo.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public DiscoveredNodes getDiscoveredNodes() {
        UUID uid = dispatchCall("2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout());
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, DiscoveredNodes.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public BondedNodes getBondedNodes() {
        UUID uid = dispatchCall("3", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout());
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, BondedNodes.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType clearAllBonds() {
        UUID uid = dispatchCall("4", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout());
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }

    
    private static int checkNodeAddress(int nodeAddress) {
        if ( !DataTypesChecker.isByteValue(nodeAddress) ) {
            throw new IllegalArgumentException("Node address out of bounds");
        }
        return nodeAddress;
    }
    
    private static int checkBondingMask(int bondingMask) {
        if ( !DataTypesChecker.isByteValue(bondingMask) ) {
            throw new IllegalArgumentException("Bonding mask out of bounds");
        }
        return bondingMask;
    }
    
    @Override
    public BondedNode bondNode(int address, int bondingMask) {
        checkNodeAddress(address);
        checkBondingMask(bondingMask);
        UUID uid = dispatchCall(
                "5", new Object[] { getRequestHwProfile(), address, bondingMask },
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        
        // bonding operation takes approximatly 10 s - this should be set
        // by setDefaultWaitingTimeout() before calling this method
        return getCallResult(uid, BondedNode.class, getDefaultWaitingTimeout());
    }

    @Override
    public Integer removeBondedNode(int address) {
        checkNodeAddress(address);
        UUID uid = dispatchCall(
                "6", new Object[] { getRequestHwProfile(), address }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, Integer.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public Integer rebondNode(int address) {
        checkNodeAddress(address);
        UUID uid = dispatchCall(
                "7", new Object[] { getRequestHwProfile(), address }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, Integer.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public DiscoveryResult runDiscovery(DiscoveryParams discoveryParams) {
        UUID uid = dispatchCall(
                "8", new Object[] { getRequestHwProfile(), discoveryParams }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, DiscoveryResult.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public DPA_Parameter setDPA_Param(DPA_Parameter dpaParams) {
        UUID uid = dispatchCall(
                "9", new Object[] { getRequestHwProfile(), dpaParams }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, DPA_Parameter.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public RoutingHops setHops(RoutingHops hops) {
        UUID uid = dispatchCall(
                "10", new Object[] { getRequestHwProfile(), hops }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, RoutingHops.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public Short[] discoveryData(int address) {
        checkNodeAddress(address);
        UUID uid = dispatchCall(
                "11", new Object[] { getRequestHwProfile(), address }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, Short[].class, getDefaultWaitingTimeout());
    }
    
    
    private static int checkIndex(int index) {
        if ( !DataTypesChecker.isByteValue(index) ) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return index;
    }
    
    @Override
    public Short[] backup(int index) {
        checkIndex(index);
        UUID uid = dispatchCall(
                "12", new Object[] { getRequestHwProfile(), index }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, Short[].class, getDefaultWaitingTimeout());
    }
    
    /** Network data length. */
    public static final int NETWORK_DATA_LENGTH = 18;
    
    private static void checkNetworkData(short[] networkData) {
        if ( networkData == null ) {
            throw new IllegalArgumentException("Network data cannot be null");
        }
        if ( networkData.length != NETWORK_DATA_LENGTH ) {
            throw new IllegalArgumentException(
                    "Invalid network data lentgh. Expected: " + NETWORK_DATA_LENGTH
            );
        }
    }
    
    @Override
    public VoidType restore(short[] networkData) {
        checkNetworkData(networkData);
        UUID uid = dispatchCall(
                "13", new Object[] { getRequestHwProfile(), networkData }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType restore(Short[] networkData) {
        checkNetworkData(ArrayUtils.toPrimitive(networkData));
        UUID uid = dispatchCall(
                "13", new Object[] { getRequestHwProfile(), networkData }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    
    /** Network data length. */
    public static final int MODULE_ID_LENGTH = 2;
    
    private static void checkModuleId(short[] moduleId) {
        if ( moduleId == null ) {
            throw new IllegalArgumentException("Module ID cannot be null");
        }
        if ( moduleId.length != MODULE_ID_LENGTH ) {
            throw new IllegalArgumentException(
                    "Invalid module ID. Expected: " + MODULE_ID_LENGTH
            );
        }
    }
    
    @Override
    public BondedNode authorizeBond(int address, short[] moduleId) {
        checkNodeAddress(address);
        checkModuleId(moduleId);
        UUID uid = dispatchCall(
                "14", new Object[] { getRequestHwProfile(), address, moduleId }, 
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, BondedNode.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public Short[] bridge(SubDPARequest subRequest) {
        UUID uid = dispatchCall("15", new Object[] { getRequestHwProfile(), subRequest }, 
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, Short[].class, getDefaultWaitingTimeout());
    }
    
    
    private static int checkControl(int control) {
        if ( !DataTypesChecker.isByteValue(control) ) {
            throw new IllegalArgumentException("Control out of bounds");
        }
        return control;
    }
    
    /** User data length. */
    public static final int USER_DATA_LENGTH = 2;
    
    private static void checkUserData(short[] usedData) {
        if ( usedData == null ) {
            throw new IllegalArgumentException("User data cannot be null");
        }
        if ( usedData.length != USER_DATA_LENGTH ) {
            throw new IllegalArgumentException(
                    "Invalid user data length. Expected: " + USER_DATA_LENGTH
            );
        }
    }
    
    @Override
    public VoidType enableRemoteBonding(int bondingMask, int control, short[] userData) {
        checkBondingMask(bondingMask);
        checkControl(control);
        checkUserData(userData);
        UUID uid = dispatchCall(
                "16", new Object[] { getRequestHwProfile(), bondingMask, control, userData}, 
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public RemotelyBondedModuleId readRemotelyBondedModuleId() {
        UUID uid = dispatchCall("17", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, RemotelyBondedModuleId.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType clearRemotelyBondedModuleId() {
        UUID uid = dispatchCall("18", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
}
