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

package com.microrisc.simply.iqrf.dpa.v210.examples.user_peripherals.mydallas.def;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.iqrf.dpa.v210.DPA_DeviceObject;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple {@code MyDallas18B20} implementation.
 *
 * @author Martin Strouhal
 */
public final class SimpleMyDallas18B20 
extends DPA_DeviceObject implements MyDallas18B20 {

    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<MyDallas18B20.MethodID, String> methodIdsMap = 
            new EnumMap<>(MyDallas18B20.MethodID.class);

    private static void initMethodIdsMap() {
        methodIdsMap.put(MyDallas18B20.MethodID.GET, "0");
    }

    static {
        initMethodIdsMap();
    }

    public SimpleMyDallas18B20(
            String networkId, String nodeId, ConnectorService connector,
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }

    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((MyDallas18B20.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }

        if ( args == null ) {
            return dispatchCall(methodIdStr, new Object[]{getRequestHwProfile()});
        }

        Object[] argsWithHwProfile = new Object[args.length + 1];
        argsWithHwProfile[0] = getRequestHwProfile();
        System.arraycopy(args, 0, argsWithHwProfile, 1, args.length);
        return dispatchCall(methodIdStr, argsWithHwProfile);
    }

    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof MyDallas18B20.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type Dallas18B20.MethodID."
            );
        }
        return methodIdsMap.get((MyDallas18B20.MethodID) methodId);
    }

    @Override
    public short get() {
        UUID uid = dispatchCall("0", new Object[]{getRequestHwProfile()},
                getDefaultWaitingTimeout());
        if ( uid == null ) {
            return Short.MAX_VALUE;
        }
        Short result = getCallResult(uid, short.class, getDefaultWaitingTimeout());
        if ( result == null ) {
            return Short.MAX_VALUE;
        }
        return result;
    }
}
