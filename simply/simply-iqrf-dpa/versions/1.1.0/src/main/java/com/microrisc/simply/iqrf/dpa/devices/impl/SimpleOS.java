
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.devices.OS;
import com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers.OSStandardTransformer;
import com.microrisc.simply.iqrf.dpa.types.HWP_Configuration;
import com.microrisc.simply.iqrf.dpa.types.OsInfo;
import com.microrisc.simply.iqrf.dpa.types.SleepInfo;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple {@code OS} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleOS 
extends DPA_DeviceObject implements OS {
    
    public SimpleOS(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((OS.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }
        
        if ( args == null ) {
            return dispatchCall( methodIdStr, new Object[] { getHwProfile() } );
        }
        
        Object[] argsWithHwProfile = new Object[ args.length + 1 ];
        argsWithHwProfile[0] = getHwProfile();
        System.arraycopy( args, 0, argsWithHwProfile, 1, args.length );
        return dispatchCall( methodIdStr, argsWithHwProfile);
    }
    
    @Override
    public String transform(Object methodId) {
        return OSStandardTransformer.getInstance().transform(methodId);
    }
    
    
    @Override
    public OsInfo read() {
        UUID uid = dispatchCall("1", new Object[] { getHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, OsInfo.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public VoidType reset() {
        UUID uid = dispatchCall("2", new Object[] { getHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public HWP_Configuration readHWPConfiguration() {
        UUID uid = dispatchCall("3", new Object[] { getHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, HWP_Configuration.class, getDefaultWaitingTimeout() );
    }
 
    @Override
    public VoidType runRFPGM() {
        UUID uid = dispatchCall("4", new Object[] { getHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }

    @Override
    public VoidType sleep(SleepInfo sleepInfo) {
        UUID uid = dispatchCall("5", new Object[] { getHwProfile(), sleepInfo }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    /**
     * NOT SUPPORTED. Throws {@code UnsupportedOperationException} exception. 
     * @throws UnsupportedOperationException
     */
    @Override
    public VoidType batch() {
        throw new UnsupportedOperationException("Currently not implemented");
    }
    
    private static final int USER_ADDR_LOWER_BOUND = 0x00;
    private static final int USER_ADDR_UPPER_BOUND = 0xFFFF;
    
    private static int checkUserAddress(int userAddress) {
        if ( (userAddress < USER_ADDR_LOWER_BOUND) || (userAddress > USER_ADDR_UPPER_BOUND) ) {
            throw new IllegalArgumentException("User address out of bounds");
        }
        return userAddress;
    }
    
    @Override
    public VoidType setUSEC_UserAddress(int value) {
        checkUserAddress(value);
        UUID uid = dispatchCall("7", new Object[] { getHwProfile(), value }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    // MID key length
    private static final int MID_KEY_LENGTH = 24; 
    
    private static void checkKey(short[] key) {
        if ( key == null ) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if ( key.length != MID_KEY_LENGTH ) {
            throw new IllegalArgumentException(
                    "Invalid key length. Expected: " + MID_KEY_LENGTH
            );
        }
    }
    
    @Override
    public VoidType setMID(short[] key) {
        checkKey(key);
        UUID uid = dispatchCall("8", new Object[] { getHwProfile(), key }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
}
