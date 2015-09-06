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

package com.microrisc.simply.iqrf.dpa.v220.examples.user_peripherals.mycustom.def;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.v220.DPA_DeviceObject;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple {@code MyDallas18B20} implementation.
 *
 * @author Martin Strouhal
 */
public final class SimpleMyCustom 
extends DPA_DeviceObject implements MyCustom {
    
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<MyCustom.MethodID, String> methodIdsMap 
            = new EnumMap<>(MyCustom.MethodID.class);

    private static void initMethodIdsMap() {
        methodIdsMap.put(MyCustom.MethodID.SEND, "0");
    }

    static {
        initMethodIdsMap();
    }

    public SimpleMyCustom(
            String networkId, String nodeId, ConnectorService connector,
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }

    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((MyCustom.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }

        switch ( (MyCustom.MethodID)methodId ) {
            case SEND:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] {short.class, short[].class } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), args[0], args[1] },
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }

    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof MyCustom.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type MyCustom.MethodID."
            );
        }
        return methodIdsMap.get((MyCustom.MethodID) methodId);
    }

    @Override
    public Short[] send(short cmdId, short[] data) {
        UUID uid = dispatchCall(
                "0", new Object[]{getRequestHwProfile(), cmdId, data}, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        Short[] result = getCallResult(uid, Short[].class, getDefaultWaitingTimeout());
        if ( result == null ) {
            return null;
        }
        return result;
    }
}
