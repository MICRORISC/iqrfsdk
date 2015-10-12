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

package com.microrisc.simply.iqrf.dpa.v22x.devices.impl;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Node;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.NodeStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.types.NodeStatusInfo;
import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple {@code Node} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleNode 
extends DPA_DeviceObject implements Node {
   
    /** User data length. */
    public static final int USER_DATA_LENGTH = 2;
    
    /** Network data length. */
    public static final int NETWORK_DATA_LENGTH = 18;
    
    
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
    
    private static int checkIndex(int index) {
        if ( !DataTypesChecker.isByteValue(index) ) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return index;
    }
    
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
        
        switch ( (Node.MethodID)methodId ) {
            case READ:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() },
                        getDefaultWaitingTimeout()
                );
            case REMOVE_BOND:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            case ENABLE_REMOTE_BONDING:
                MethodArgumentsChecker.checkArgumentTypes(
                        args, new Class[] { Integer.class, Integer.class, short[].class } 
                );
                checkBondingMask((Integer)args[0]);
                checkControl((Integer)args[1]);
                checkUserData((short[])args[2]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { 
                            getRequestHwProfile(), (Integer)args[0], 
                            (Integer)args[1], (short[])args[2] 
                        }, 
                        getDefaultWaitingTimeout()
                );
            case READ_REMOTELY_BONDED_MODULE_ID:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            case CLEAR_REMOTELY_BONDED_MODULE_ID:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            case REMOVE_BOND_ADDRESS:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            case BACKUP:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { Integer.class } );
                checkIndex((Integer)args[0]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (Integer)args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case RESTORE:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { short[].class } );
                checkNetworkData((short[])args[0]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (short[])args[0] }, 
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }
    
    @Override
    public String transform(Object methodId) {
        return NodeStandardTransformer.getInstance().transform(methodId);
    }
    
    
    // ASYNCHRONOUS METHODS IMPLEMENTATIONS
    
    @Override
    public UUID async_read() {
        return dispatchCall(
                "1", new Object[] { getRequestHwProfile(), getDefaultWaitingTimeout() }
        );
    }
    
    @Override
    public UUID async_removeBond() {
        return dispatchCall(
                "2", new Object[] { getRequestHwProfile(), getDefaultWaitingTimeout() }
        );
    }
    
    @Override
    public UUID async_enableRemoteBonding(int bondingMask, int control, short[] userData) {
        checkBondingMask(bondingMask);
        checkControl(control);
        checkUserData(userData);
        return dispatchCall(
                "3", new Object[] { getRequestHwProfile(), bondingMask, control, userData}, 
                getDefaultWaitingTimeout()
        );
    }
    
    @Override
    public UUID async_readRemotelyBondedModuleId() {
        return dispatchCall(
                "4", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
        );
    }
    
    @Override
    public UUID async_clearRemotelyBondedModuleId() {
        return dispatchCall(
                "5", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
        );
    }
    
    @Override
    public UUID async_removeBondAddress() {
        return dispatchCall(
                "6", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
        );
    }
    
    @Override
    public UUID async_backup(int index) {
        checkIndex(index);
        return dispatchCall(
                "7", new Object[] { getRequestHwProfile(), index }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_restore(short[] networkData) {
        checkNetworkData(networkData);
        return dispatchCall(
                "8", new Object[] { getRequestHwProfile(), networkData }, getDefaultWaitingTimeout()
        );
    }
    
    
    
    // SYNCHRONOUS WRAPPERS IMPLEMENTATIONS
    
    @Override
    public NodeStatusInfo read() {
        UUID uid = dispatchCall(
                "1", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, NodeStatusInfo.class, getDefaultWaitingTimeout());
    }

    @Override
    public VoidType removeBond() {
        UUID uid = dispatchCall(
                "2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
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
        UUID uid = dispatchCall(
                "4", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, RemotelyBondedModuleId.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType clearRemotelyBondedModuleId() {
        UUID uid = dispatchCall(
                "5", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public VoidType removeBondAddress() {
        UUID uid = dispatchCall(
                "6", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }

    @Override
    public short[] backup(int index) {
        checkIndex(index);
        UUID uid = dispatchCall(
                "7", new Object[] { getRequestHwProfile(), index }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout());
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
    
}
