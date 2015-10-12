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
import com.microrisc.simply.iqrf.dpa.v22x.devices.PeripheralInfoGetter;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.PeripheralInfoGetterStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralEnumeration;
import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralInfo;
import java.util.UUID;

/**
 * Simple {@code PeripheralInfoGetter} implementation. 
 * 
 * @author Michal Konopa
 */
public final class SimplePeripheralInfoGetter 
extends DPA_DeviceObject implements PeripheralInfoGetter {
    
    private static int checkPeripheralId(int peripheralId) {
        if ( !DataTypesChecker.isByteValue(peripheralId) ) {
            throw new IllegalArgumentException("Peripheral ID out of bounds");
        }
        return peripheralId;
    }
    
    
    public SimplePeripheralInfoGetter(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((PeripheralInfoGetter.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }
        
        switch ( (PeripheralInfoGetter.MethodID)methodId ) {
            case GET_PERIPHERAL_ENUMERATION:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() },
                        getDefaultWaitingTimeout()
                );
            case GET_PERIPHERAL_INFO:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { Integer.class } );
                checkPeripheralId((Integer)args[0]);
                return dispatchCall(
                        methodIdStr,
                        new Object[] { getRequestHwProfile(), (Integer)args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case GET_MORE_PERIPHERALS_INFO:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { Integer.class } );
                checkPeripheralId((Integer)args[0]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (Integer)args[0] }, 
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }
    
    @Override
    public String transform(Object methodId) {
        return PeripheralInfoGetterStandardTransformer.getInstance().transform(methodId);
    }
    
    
    
    // ASYNCHRONOUS METHODS IMPLEMENTATIONS
    
    @Override
    public UUID async_getPeripheralEnumeration() {
        return dispatchCall(
                "1", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_getPeripheralInfo(int peripheralId) {
        checkPeripheralId(peripheralId);
        return dispatchCall(
                "2", new Object[] { getRequestHwProfile(), peripheralId },
                getDefaultWaitingTimeout()
        );
    }
    
    @Override
    public UUID async_getMorePeripheralsInfo(int startPeripheralId) {
        checkPeripheralId(startPeripheralId);
        return dispatchCall(
                "3", new Object[] { getRequestHwProfile(), startPeripheralId },
                getDefaultWaitingTimeout()
        );
    }
    
    
    
    // SYNCHRONOUS WRAPPERS IMPLEMENTATIONS
    
    @Override
    public PeripheralEnumeration getPeripheralEnumeration() {
        UUID uid = dispatchCall(
                "1", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, PeripheralEnumeration.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public PeripheralInfo getPeripheralInfo(int peripheralId) {
        checkPeripheralId(peripheralId);
        UUID uid = dispatchCall(
                "2", new Object[] { getRequestHwProfile(), peripheralId }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, PeripheralInfo.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public PeripheralInfo[] getMorePeripheralsInfo(int startPeripheralId) {
        checkPeripheralId(startPeripheralId);
        UUID uid = dispatchCall(
                "3", new Object[] { getRequestHwProfile(), startPeripheralId }, 
                getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, PeripheralInfo[].class, getDefaultWaitingTimeout() );
    }
}
