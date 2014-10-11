
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
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
            return dispatchCall( methodIdStr, new Object[] { getRequestHwProfile() } );
        }
        
        Object[] argsWithHwProfile = new Object[ args.length + 1 ];
        argsWithHwProfile[0] = getRequestHwProfile();
        System.arraycopy( args, 0, argsWithHwProfile, 1, args.length );
        return dispatchCall( methodIdStr, argsWithHwProfile);
    }
    
    @Override
    public String transform(Object methodId) {
        return FRCStandardTransformer.getInstance().transform(methodId);
    }
    
    
    private static FRC_Command checkFRC_Command(FRC_Command frcCmd) {
        if ( frcCmd == null ) {
            throw new IllegalArgumentException("FRC command cannot be null."); 
        }
        return frcCmd;
    }
    
    /**
     * @param frcCmd FRC command to send
     * @throws IllegalArgumentException if user data of specified FRC command is {@code null} 
     */
    @Override
    public FRC_Data send(FRC_Command frcCmd) {
        checkFRC_Command(frcCmd);
        UUID uid = dispatchCall("1", new Object[] { getRequestHwProfile(), frcCmd }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, FRC_Data.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public short[] extraResult() {
        UUID uid = dispatchCall("2", new Object[] { getRequestHwProfile() }, 
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout());
    }
    
    /**
     * @param frcCmd FRC command to send
     * @throws IllegalArgumentException if user data of specified FRC command is {@code null} 
     */
    @Override
    public UUID async_send(FRC_Command frcCmd) {
        checkFRC_Command(frcCmd);
        return dispatchCall("1", new Object[] { getRequestHwProfile(), frcCmd } );
    }
    
    @Override
    public UUID async_extraResult() {
        return dispatchCall("2", new Object[] { getRequestHwProfile() } );
    }
}
