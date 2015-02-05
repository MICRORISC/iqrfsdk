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

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.v210.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v210.devices.EEEPROM;
import com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers.EEEPROMStandardTransformer;
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
        
        switch ( (EEEPROM.MethodID)methodId ) {
            case READ:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { Integer.class, Integer.class } );
                checkBlockNumber((Integer)args[0]);
                checkDataLenToRead((Integer)args[1]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (Integer)args[0], (Integer)args[1] },
                        getDefaultWaitingTimeout()
                );
            case WRITE:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { Integer.class, short.class } );
                checkBlockNumber((Integer)args[0]);
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
