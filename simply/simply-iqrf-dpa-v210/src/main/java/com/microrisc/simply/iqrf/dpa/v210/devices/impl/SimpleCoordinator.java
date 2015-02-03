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

package com.microrisc.simply.iqrf.dpa.v210.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.v210.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v210.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers.CoordinatorStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v210.types.AddressingInfo;
import com.microrisc.simply.iqrf.dpa.v210.types.BondedNode;
import com.microrisc.simply.iqrf.dpa.v210.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.v210.types.DPA_Parameter;
import com.microrisc.simply.iqrf.dpa.v210.types.DiscoveredNodes;
import com.microrisc.simply.iqrf.dpa.v210.types.DiscoveryParams;
import com.microrisc.simply.iqrf.dpa.v210.types.DiscoveryResult;
import com.microrisc.simply.iqrf.dpa.v210.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.dpa.v210.types.RoutingHops;
import com.microrisc.simply.iqrf.dpa.v210.types.SubDPARequest;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

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
    
    // checks specified arguments of a called method
    private void checkArgumentTypes(Object[] args, Class[] argTypes) {
        if ( args == null || args.length == 0 ) {
            if ( argTypes.length == 0 ) {
                return;
            } else {
                throw new IllegalArgumentException(
                        "Arguments number mismatch. "
                        + "Expected: " + argTypes.length +  ", got: 0" 
                );
            }
        }
        
        if ( args.length != argTypes.length ) {
            throw new IllegalArgumentException(
                    "Arguments number mismatch. "
                    + "Expected: " + argTypes.length + ", got: " + args.length
            );
        }
        
        for ( int argId = 0; argId < args.length; argId++ ) {
            if ( !(argTypes[argId].isAssignableFrom(args[argId].getClass())) ) {
                throw new IllegalArgumentException(
                    "Type mismatch by the " + argId + ". argument."
                    + "Expected: " + argTypes[argId].getClass().getName()
                    + ", got: " + args[argId].getClass()
                );
            }
        }
    }
    
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((Coordinator.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }
        
        switch ( (Coordinator.MethodID)methodId ) {
            case GET_ADDRESSING_INFO:
                checkArgumentTypes(args, new Class[] {} );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
                );
            case GET_DISCOVERED_NODES:
                checkArgumentTypes(args, new Class[] {} );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
                );
            case GET_BONDED_NODES:
                checkArgumentTypes(args, new Class[] {} );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
                );
            case CLEAR_ALL_BONDS:
                checkArgumentTypes(args, new Class[] {} );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
                );
            case BOND_NODE:
                checkArgumentTypes(args, new Class[] { Integer.class, Integer.class } );
                checkNodeAddress((Integer)args[0]);
                checkBondingMask((Integer)args[1]);
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0], args[1] }, 
                        getDefaultWaitingTimeout()
                );
            case REMOVE_BONDED_NODE:
                checkArgumentTypes(args, new Class[] { Integer.class } );
                checkNodeAddress((Integer)args[0]);
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case REBOND_NODE:
                checkArgumentTypes(args, new Class[] { Integer.class } );
                checkNodeAddress((Integer)args[0]);
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case RUN_DISCOVERY:
                checkArgumentTypes(args, new Class[] { DiscoveryParams.class } );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case SET_DPA_PARAM:
                checkArgumentTypes(args, new Class[] { DPA_Parameter.class } );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case SET_HOPS:
                checkArgumentTypes(args, new Class[] { RoutingHops.class } );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case DISCOVERY_DATA:
                checkArgumentTypes(args, new Class[] { Integer.class } );
                checkNodeAddress((Integer)args[0]);
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case BACKUP:
                checkArgumentTypes(args, new Class[] { Integer.class } );
                checkIndex((Integer)args[0]);
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case RESTORE:
                checkArgumentTypes(args, new Class[] { short[].class } );
                checkNetworkData((short[]) args[0]);
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case AUTHORIZE_BOND:
                checkArgumentTypes(args, new Class[] { Integer.class, short[].class } );
                checkNodeAddress((Integer)args[0]);
                checkModuleId((short[])args[1]);
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0], args[1] }, 
                        getDefaultWaitingTimeout()
                );
            case BRIDGE:
                checkArgumentTypes(args, new Class[] { SubDPARequest.class } );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case ENABLE_REMOTE_BONDING:
                checkArgumentTypes(args, new Class[] { Integer.class, Integer.class, short[].class } );
                checkBondingMask((Integer)args[0]);
                checkControl((Integer)args[1]);
                checkUserData((short[]) args[2]);
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile(), args[0], args[1], args[2] }, 
                    getDefaultWaitingTimeout()
                );
            case READ_REMOTELY_BONDED_MODULE_ID:
                checkArgumentTypes(args, new Class[] {} );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            case CLEAR_REMOTELY_BONDED_MODULE_ID:
                checkArgumentTypes(args, new Class[] {} );
                return dispatchCall(
                    methodIdStr, new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
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
    public short[] discoveryData(int address) {
        checkNodeAddress(address);
        UUID uid = dispatchCall(
                "11", new Object[] { getRequestHwProfile(), address }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout());
    }
    
    
    private static int checkIndex(int index) {
        if ( !DataTypesChecker.isByteValue(index) ) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return index;
    }
    
    @Override
    public short[] backup(int index) {
        checkIndex(index);
        UUID uid = dispatchCall(
                "12", new Object[] { getRequestHwProfile(), index }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout());
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
    public short[] bridge(SubDPARequest subRequest) {
        UUID uid = dispatchCall("15", new Object[] { getRequestHwProfile(), subRequest }, 
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout());
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
