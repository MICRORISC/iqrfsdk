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
import com.microrisc.simply.iqrf.dpa.v22x.devices.SPI;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.SPIStandardTransformer;
import java.util.UUID;

/**
 * Simple {@code SPI} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleSPI 
extends DPA_DeviceObject implements SPI {
    
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
    
    
    public SimpleSPI(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((SPI.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }
        
        switch ( (SPI.MethodID)methodId ) {
            case WRITE_AND_READ:
                MethodArgumentsChecker.checkArgumentTypes(
                        args, new Class[] { Integer.class, short[].class } 
                );
                checkReadTimeout((Integer)args[0]);
                checkDataToWrite((short[])args[1]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (Integer)args[0], (short[])args[1] },
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }
    
    @Override
    public String transform(Object methodId) {
        return SPIStandardTransformer.getInstance().transform(methodId);
    }
    
    
    
    // ASYNCHRONOUS METHODS IMPLEMENTATIONS
    
    @Override
    public UUID async_writeAndRead(int readTimeout, short[] data) {
        checkReadTimeout(readTimeout);
        checkDataToWrite(data);
        return dispatchCall(
                "2", new Object[] { getRequestHwProfile(), readTimeout, data }, 
                getDefaultWaitingTimeout() 
        );
    }
    
    
    
    // SYNCHRONOUS WRAPPERS IMPLEMENTATIONS
    
    @Override
    public short[] writeAndRead(int readTimeout, short[] data) {
        checkReadTimeout(readTimeout);
        checkDataToWrite(data);
        UUID uid = dispatchCall(
                "2", new Object[] { getRequestHwProfile(), readTimeout, data }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout() );
    }
}
