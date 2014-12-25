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

package com.microrisc.simply.iqrf.dpa.v201.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.v201.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v201.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v201.di_services.method_id_transformers.FRCStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v201.types.FRC_Command;
import com.microrisc.simply.iqrf.dpa.v201.types.FRC_Data;
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
    
    /** Length of user data in {@code send} methods.  */
    public static final int USER_DATA_LENGTH = 2;
    
    
    // checks specified user data length
    private static FRC_Command checkCommand(FRC_Command command) {
        if ( command == null ) {
            throw new IllegalArgumentException("FRC command cannot be null");
        }
        return command;
    }
   
    
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
        UUID uid = dispatchCall("2", new Object[] { getRequestHwProfile() } , getDefaultWaitingTimeout());
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
        checkCommand(frcCmd);
        return dispatchCall("1", new Object[] { getRequestHwProfile(), frcCmd } );
    }
    
    @Override
    public UUID async_extraResult() {
        return dispatchCall("2", new Object[] { getRequestHwProfile() } );
    }
}
