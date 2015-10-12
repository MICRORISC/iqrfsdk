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
import com.microrisc.simply.iqrf.dpa.v22x.devices.GeneralMemory;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.GeneralMemoryStandardTransformer;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple {@code GeneralMemory} implementation.
 * 
 * @author Michal Konopa
 */
class SimpleGeneralMemory 
extends DPA_DeviceObject implements GeneralMemory {
    private static int checkAddress(int address) {
        if ( !DataTypesChecker.isByteValue(address) ) {
            throw new IllegalArgumentException("Address out of bounds.");
        }
        return address;
    }
    
    private static int checkDataLenToRead(int dataLen) {
        if ( !DataTypesChecker.isByteValue(dataLen) ) {
            throw new IllegalArgumentException("Data length out of bounds.");
        }
        return dataLen;
    }
    
    private static void checkDataToWrite(short[] dataToWrite) {
        if ( dataToWrite == null ) {
            throw new IllegalArgumentException("Data to write cannot be null.");
        }
        if ( dataToWrite.length < 1 ) {
            throw new IllegalArgumentException("Data to write cannot be empty.");
        }
    }
    
    
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
        
        switch ( (GeneralMemory.MethodID)methodId ) {
            case READ:
                MethodArgumentsChecker.checkArgumentTypes(
                        args, new Class[] { Integer.class, Integer.class } 
                );
                checkAddress((Integer)args[0]);
                checkDataLenToRead((Integer)args[1]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (Integer)args[0], (Integer)args[1] },
                        getDefaultWaitingTimeout()
                );
            case WRITE:
                MethodArgumentsChecker.checkArgumentTypes(
                        args, new Class[] { Integer.class, short.class } 
                );
                checkAddress((Integer)args[0]);
                checkDataToWrite((short[])args[1]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { 
                            getRequestHwProfile(), (Integer)args[0], (short[])args[1],
                        }, 
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }
    
    @Override
    public String transform(Object methodId) {
        return GeneralMemoryStandardTransformer.getInstance().transform(methodId);
    }
    
    
    
    // ASYNCHRONOUS METHODS IMPLEMENTATIONS
    
    @Override
    public UUID async_read(int address, int length) {
        checkAddress(address);
        checkDataLenToRead(length);
        return dispatchCall(
                "1", new Object[] { getRequestHwProfile(), address, length },
                getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_write(int address, short[] data) {
        checkAddress(address);
        checkDataToWrite(data);
        return dispatchCall(
                "2", new Object[] { getRequestHwProfile(), address, data },
                getDefaultWaitingTimeout()
        );
    }
    
    
    // SYNCHRONOUS WRAPPERS IMPLEMENTATIONS
    
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
