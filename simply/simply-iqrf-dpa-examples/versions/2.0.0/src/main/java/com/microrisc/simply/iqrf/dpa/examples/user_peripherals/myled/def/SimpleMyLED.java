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

package com.microrisc.simply.iqrf.dpa.examples.user_peripherals.myled.def;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.iqrf.dpa.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple {@code MyLED} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleMyLED 
extends DPA_DeviceObject implements MyLED {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<MyLED.MethodID, String> methodIdsMap = 
            new EnumMap<>(MyLED.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(MyLED.MethodID.SET, "1");
        methodIdsMap.put(MyLED.MethodID.GET, "2");
        methodIdsMap.put(MyLED.MethodID.PULSE, "3");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    
    public SimpleMyLED(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((MyLED.MethodID) methodId);
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
        if ( !(methodId instanceof MyLED.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type MyDevice.MethodID."
            );
        }
        return methodIdsMap.get((MyLED.MethodID) methodId);
    }
    
    @Override
    public VoidType set(LED_State state) {
        UUID uid = dispatchCall("1", new Object[] { getRequestHwProfile(), state }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }

    @Override
    public LED_State get() {
        UUID uid = dispatchCall("2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout());
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, LED_State.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType pulse() {
        UUID uid = dispatchCall("3", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout());
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
}
