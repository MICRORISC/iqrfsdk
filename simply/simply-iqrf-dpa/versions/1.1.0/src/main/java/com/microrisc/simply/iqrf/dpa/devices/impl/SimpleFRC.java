
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.devices.FRC;
import com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers.FRCStandardTransformer;
import com.microrisc.simply.iqrf.dpa.types.FRC_Command;
import com.microrisc.simply.iqrf.dpa.types.FRC_Data;
import java.util.UUID;

/**
 * Simple FRC implementation.
 * 
 * @author Rostislav Spinar
 */
public final class SimpleFRC
extends DPA_DeviceObject implements FRC {
    
    public SimpleFRC(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((FRC.MethodID) methodId);
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
        return FRCStandardTransformer.getInstance().transform(methodId);
    }
    
    /** Length of user data in {@code send} methods.  */
    public static final int USER_DATA_LENGTH = 2;
    
    
    // checks specified user data length
    private FRC_Command checkCommand(FRC_Command command) {
        if ( command == null ) {
            throw new IllegalArgumentException("FRC command cannot be null");
        }
        return command;
    }
    
    // checks specified user data length
    private short[] checkUserDataLength(short[] userData) {
        if ( userData == null ) {
            throw new IllegalArgumentException("User data cannot be null");
        }
        if ( userData.length != USER_DATA_LENGTH ) {
            throw new IllegalArgumentException(
                    "Invalid user data length. Expected: " + USER_DATA_LENGTH
            );
        }
        return userData;
    }
    
    /**
     * If {@code userData} argument is {@NULL} or its length is {0}, array of
     * {@code USER_DATA_LENGTH} with all elements set to {0} is used.
     * @param frcCmd FRC command
     * @param userData user data
     * @throws IllegalArgumentException if: <br>
     *         - {@code frcCmd} is {@code null} <br>
     *         - length of {@code userData} is distinct from {@code USER_DATA_LENGTH}
     */
    @Override
    public FRC_Data send(FRC_Command frcCmd, short[] userData) {
        checkCommand(frcCmd);
        checkUserDataLength(userData);
        UUID uid = dispatchCall("1", new Object[] { getHwProfile(), frcCmd, userData }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, FRC_Data.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public Short[] extraResult() {
        UUID uid = dispatchCall("2", new Object[] { getHwProfile() } , getDefaultWaitingTimeout());
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, Short[].class, getDefaultWaitingTimeout());
    }
    
    /**
     * If {@code userData} argument is {@NULL} or its length is {0}, array of
     * {@code USER_DATA_LENGTH} with all elements set to {0} is used.
     * @param frcCmd FRC command
     * @param userData user data
     * @throws IllegalArgumentException if: <br>
     *         - {@code frcCmd} is {@code null} <br>
     *         - length of {@code userData} is distinct from {@code USER_DATA_LENGTH}
     */
    @Override
    public UUID async_send(FRC_Command frcCmd, short[] userData) {
        checkCommand(frcCmd);
        checkUserDataLength(userData);
        return dispatchCall("1", new Object[] { getHwProfile(), frcCmd, userData } );
    }
    
    @Override
    public UUID async_extraResult() {
        return dispatchCall("2", new Object[] { getHwProfile() } );
    }
}
