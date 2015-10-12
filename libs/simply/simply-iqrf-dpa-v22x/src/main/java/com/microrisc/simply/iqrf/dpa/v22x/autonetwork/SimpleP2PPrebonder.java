/*
 * Copyright 2014 MICRORISC s.r.o..
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

package com.microrisc.simply.iqrf.dpa.v22x.autonetwork;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_DeviceObject;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple implementation of {@code P2PPrebonder} Device Interface.
 * 
 * @author Michal Konopa
 */
public final class SimpleP2PPrebonder 
extends DPA_DeviceObject implements P2PPrebonder {

    public SimpleP2PPrebonder(
            String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((P2PPrebonder.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }
        
        switch ( (P2PPrebonder.MethodID)methodId ) {
            case SEND_PREBONDING_DATA:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { short[].class } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (short[])args[0] },
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }
    
    @Override
    public String transform(Object methodId) {
        return P2PPrebonderStandardTransformer.getInstance().transform(methodId);
    }
    
    @Override
    public VoidType sendPrebondingData(short[] prebondingData) {
        UUID uid = dispatchCall("1", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout());
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
}
