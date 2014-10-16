
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.devices.GeneralMemory;
import com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers.GeneralMemoryStandardTransformer;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple {@code GeneralMemory} implementation.
 * 
 * @author Michal Konopa
 */
class SimpleGeneralMemory 
extends DPA_DeviceObject implements GeneralMemory {
    
    public SimpleGeneralMemory(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((GeneralMemory.MethodID) methodId);
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
        return GeneralMemoryStandardTransformer.getInstance().transform(methodId);
    }
    
    private static int checkAddress(int address) {
        if (!DataTypesChecker.isByteValue(address)) {
            throw new IllegalArgumentException("Address out of bounds.");
        }
        return address;
    }
    
    private static int checkDataLenToRead(int dataLen) {
        if (!DataTypesChecker.isByteValue(dataLen)) {
            throw new IllegalArgumentException("Data length out of bounds.");
        }
        return dataLen;
    }
    
    
    @Override
    public UUID async_read(int address, int length) {
        checkAddress(address);
        checkDataLenToRead(length);
        return dispatchCall(
                "1", new Object[] { getRequestHwProfile(), address, length }
        );
    }
    
    @Override
    public short[] read(int address, int length) {
        checkAddress(address);
        checkDataLenToRead(length);
        UUID uid = dispatchCall(
                "1", new Object[] { getRequestHwProfile(), address, length },
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
    public UUID async_write(int address, short[] data) {
        checkAddress(address);
        checkDataToWrite(data);
        return dispatchCall(
                "2", new Object[] { getRequestHwProfile(), address, data }
        );
    }
    
    @Override
    public VoidType write(int address, short[] data) {
        checkAddress(address);
        checkDataToWrite(data);
        UUID uid = dispatchCall(
                "2", new Object[] { getRequestHwProfile(), address, data }, 
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
}
