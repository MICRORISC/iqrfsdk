
package com.microrisc.simply.iqrf.dpa.v201.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.v201.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v201.devices.UART;
import com.microrisc.simply.iqrf.dpa.v201.di_services.method_id_transformers.UARTStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v201.types.BaudRate;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple UART implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleUART
extends DPA_DeviceObject implements UART {
    
    public SimpleUART(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((UART.MethodID) methodId);
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
        return UARTStandardTransformer.getInstance().transform(methodId);
    }
    
    @Override
    public VoidType open(BaudRate baudRate) {
        UUID uid = dispatchCall("1", new Object[] { getRequestHwProfile(), baudRate }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public VoidType close() {
        UUID uid = dispatchCall("2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    private static int checkReadTimeout(int readTimeout) {
        if ( !DataTypesChecker.isByteValue(readTimeout) ) {
            throw new IllegalArgumentException("Read timeout out of bounds.");
        }
        return readTimeout;
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
    public short[] writeAndRead(int readTimeout, short[] data) {
        checkReadTimeout(readTimeout);
        checkDataToWrite(data);
        UUID uid = dispatchCall(
                "3", new Object[] { getRequestHwProfile(), readTimeout, data }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout() );
    }
}
