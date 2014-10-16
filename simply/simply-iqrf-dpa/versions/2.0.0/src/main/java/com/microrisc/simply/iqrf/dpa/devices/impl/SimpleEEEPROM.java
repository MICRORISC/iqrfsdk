
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.devices.EEEPROM;
import com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers.EEEPROMStandardTransformer;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple EEEPROM implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleEEEPROM
extends DPA_DeviceObject implements EEEPROM {   
    
    public SimpleEEEPROM(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((EEEPROM.MethodID) methodId);
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
        return EEEPROMStandardTransformer.getInstance().transform(methodId);
    }
    
    
    private static int checkBlockNumber(int blockNumber) {
        if ( !DataTypesChecker.isByteValue(blockNumber) ) {
            throw new IllegalArgumentException("Block number out of bounds.");
        }
        return blockNumber;
    }
    
    private static int checkDataLenToRead(int dataLen) {
        if ( !DataTypesChecker.isByteValue(dataLen) ) {
            throw new IllegalArgumentException("Data length out of bounds.");
        }
        return dataLen;
    }
    
    @Override
    public UUID async_read(int blockNumber, int length) {
        checkBlockNumber(blockNumber);
        checkDataLenToRead(length);
        return dispatchCall(
                "1", new Object[] { getRequestHwProfile(), blockNumber, length }
        );
    }
    
    @Override
    public short[] read(int blockNumber, int length) {
        checkBlockNumber(blockNumber);
        checkDataLenToRead(length);
        UUID uid = dispatchCall(
                "1", new Object[] { getRequestHwProfile(), blockNumber, length },
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout());
    }
    
    private static void checkDataToWrite(short[] dataToWrite) {
        if ( dataToWrite == null ) {
            throw new IllegalArgumentException("Data to write cannot be null.");
        }
        if ( dataToWrite.length < 1 ) {
            throw new IllegalArgumentException("Data to write cannot be empty.");
        }
    }
    
    @Override
    public UUID async_write(int blockNumber, short[] data) {
        checkBlockNumber(blockNumber);
        checkDataToWrite(data);
        return dispatchCall(
                "2", new Object[] { getRequestHwProfile(), blockNumber, 
                data }
        );
    }
    
    @Override
    public VoidType write(int blockNumber, short[] data) {
        checkBlockNumber(blockNumber);
        checkDataToWrite(data);
        UUID uid = dispatchCall(
                "2", new Object[] { getRequestHwProfile(), blockNumber, 
                data }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
}
