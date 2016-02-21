/*
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_DeviceObject;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple {@link AutonetworkPeripheral} implementation.
 * <p>
 * @author Martin Strouhal
 */
public class SimpleAutonetworkPeripheral extends DPA_DeviceObject implements AutonetworkPeripheral {

    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<AutonetworkPeripheral.MethodID, String> methodIdsMap
            = new EnumMap<>(AutonetworkPeripheral.MethodID.class);

    private static void initMethodIdsMap() {
        methodIdsMap.put(AutonetworkPeripheral.MethodID.DISAPPROVE, "0");
        methodIdsMap.put(AutonetworkPeripheral.MethodID.APPROVE, "1");        
    }

    static {
        initMethodIdsMap();
    }

    public SimpleAutonetworkPeripheral(
            String networkId, String nodeId, ConnectorService connector,
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((AutonetworkPeripheral.MethodID) methodId);
        if (methodIdStr == null) {
            return null;
        }

        switch ((AutonetworkPeripheral.MethodID) methodId) {
            case APPROVE:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[]{});
                return dispatchCall(
                        methodIdStr,
                        new Object[]{getRequestHwProfile()},
                        getDefaultWaitingTimeout()
                );
            case DISAPPROVE:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[]{});
                return dispatchCall(
                        methodIdStr,
                        new Object[]{getRequestHwProfile()},
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }

    @Override
    public String transform(Object methodId) {
        if (!(methodId instanceof AutonetworkPeripheral.MethodID)) {
            throw new IllegalArgumentException(
                    "Method ID must be of type AutonetworkPeripheral.MethodID."
            );
        }
        return methodIdsMap.get((AutonetworkPeripheral.MethodID) methodId);
    }

    @Override
    public VoidType approve() {
        UUID uid = dispatchCall(
                "1", new Object[]{getRequestHwProfile()}, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }

        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public UUID async_approve(){
        UUID uid = dispatchCall(
                "1", new Object[]{getRequestHwProfile()}, getDefaultWaitingTimeout()
        );
        return uid;       
    }
    
    @Override
    public VoidType disapprove() {
        UUID uid = dispatchCall(
                "0", new Object[]{getRequestHwProfile()}, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }

        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public UUID async_disapprove() {
        UUID uid = dispatchCall(
                "0", new Object[]{getRequestHwProfile()}, getDefaultWaitingTimeout()
        );
        return uid;
    }
    
}
