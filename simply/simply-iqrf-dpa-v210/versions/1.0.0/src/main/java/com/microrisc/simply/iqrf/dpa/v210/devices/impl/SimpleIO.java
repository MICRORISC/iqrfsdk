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

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.v210.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v210.devices.IO;
import com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers.IOStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v210.types.IO_Command;
import com.microrisc.simply.iqrf.dpa.v210.types.IO_DirectionSettings;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple {@code IO} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleIO 
extends DPA_DeviceObject implements IO {
    
    public SimpleIO(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((IO.MethodID) methodId);
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
        return IOStandardTransformer.getInstance().transform(methodId);
    }
    
    @Override
    public UUID async_setDirection(IO_DirectionSettings[] directionSettings) {
        return dispatchCall("1", new Object[] { getRequestHwProfile(), directionSettings } );
    }
    
    @Override
    public VoidType setDirection(IO_DirectionSettings[] directionSettings) {
        UUID uid = dispatchCall("1", new Object[] { getRequestHwProfile(), directionSettings }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    
    @Override
    public UUID async_setOutputState(IO_Command[] ioCommands) {
        return dispatchCall("2", new Object[] { getRequestHwProfile(), ioCommands } );
    }

    @Override
    public VoidType setOutputState(IO_Command[] ioCommands) {
        UUID uid = dispatchCall("2", new Object[] { getRequestHwProfile(), ioCommands }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }

    
    @Override
    public UUID async_get() {
        return dispatchCall("3", new Object[] { getRequestHwProfile() } );
    }

    @Override
    public short[] get() {
        UUID uid = dispatchCall("3", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, short[].class, getDefaultWaitingTimeout() );
    }
}
