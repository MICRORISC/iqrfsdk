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

package com.microrisc.simply.iqrf.dpa.v21x.devices.impl;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.v21x.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v21x.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v21x.di_services.method_id_transformers.FRCStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v21x.types.FRC_Command;
import com.microrisc.simply.iqrf.dpa.v21x.types.FRC_Data;
import java.util.UUID;

/**
 * Simple FRC implementation.
 * 
 * @author Rostislav Spinar
 */
public final class SimpleFRC
extends DPA_DeviceObject implements FRC {
    
    private static FRC_Command checkCommand(FRC_Command frcCmd) {
        if ( frcCmd == null ) {
            throw new IllegalArgumentException("FRC command cannot be null."); 
        }
        return frcCmd;
    }
    
    
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
        
        switch ( (FRC.MethodID)methodId ) {
            case SEND:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { FRC_Command.class } );
                checkCommand((FRC_Command)args[0]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (FRC_Command)args[0] },
                        getDefaultWaitingTimeout()
                );
            case EXTRA_RESULT:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr,
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }
    
    @Override
    public String transform(Object methodId) {
        return FRCStandardTransformer.getInstance().transform(methodId);
    }
    
    
    // ASYNCHRONOUS METHODS IMPLEMENTATIONS
    
    /**
     * @param frcCmd FRC command to send
     * @throws IllegalArgumentException if user data of specified FRC command is {@code null} 
     */
    @Override
    public UUID async_send(FRC_Command frcCmd) {
        checkCommand(frcCmd);
        return dispatchCall(
                "1", new Object[] { getRequestHwProfile(), frcCmd }, getDefaultWaitingTimeout()
        );
    }
    
    @Override
    public UUID async_extraResult() {
        return dispatchCall(
                "2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
    }
    
    
    
    // SYNCHRONOUS WRAPPERS IMPLEMENTATIONS
    
    /**
     * @param frcCmd FRC command to send
     * @throws IllegalArgumentException if user data of specified FRC command is {@code null} 
     */
    @Override
    public FRC_Data send(FRC_Command frcCmd) {
        checkCommand(frcCmd);
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
}
