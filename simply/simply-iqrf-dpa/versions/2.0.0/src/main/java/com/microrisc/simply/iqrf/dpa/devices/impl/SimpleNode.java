
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.devices.Node;
import com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers.NodeStandardTransformer;
import com.microrisc.simply.iqrf.dpa.types.NodeStatusInfo;
import com.microrisc.simply.iqrf.dpa.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;
import org.apache.commons.lang.ArrayUtils;

/**
 * Simple {@code Node} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleNode 
extends DPA_DeviceObject implements Node {
   
    public SimpleNode(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((Node.MethodID) methodId);
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
        return NodeStandardTransformer.getInstance().transform(methodId);
    }
    
    @Override
    public NodeStatusInfo read() {
        UUID uid = dispatchCall("1", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, NodeStatusInfo.class, getDefaultWaitingTimeout());
    }

    @Override
    public VoidType removeBond() {
        UUID uid = dispatchCall("2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    
    private static int checkBondingMask(int bondingMask) {
        if ( !DataTypesChecker.isByteValue(bondingMask) ) {
            throw new IllegalArgumentException("Bonding mask out of bounds");
        }
        return bondingMask;
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
                "3", new Object[] { getRequestHwProfile(), bondingMask, control, userData}, 
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public RemotelyBondedModuleId readRemotelyBondedModuleId() {
        UUID uid = dispatchCall("4", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, RemotelyBondedModuleId.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType clearRemotelyBondedModuleId() {
        UUID uid = dispatchCall("5", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public VoidType removeBondAddress() {
        UUID uid = dispatchCall("6", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
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
                "7", new Object[] { getRequestHwProfile(), index }, getDefaultWaitingTimeout() 
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
                "8", new Object[] { getRequestHwProfile(), networkData }, getDefaultWaitingTimeout()
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
                "8", new Object[] { getRequestHwProfile(), networkData }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
}
