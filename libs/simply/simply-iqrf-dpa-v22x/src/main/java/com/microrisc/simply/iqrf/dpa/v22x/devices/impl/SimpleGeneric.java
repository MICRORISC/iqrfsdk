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
package com.microrisc.simply.iqrf.dpa.v22x.devices.impl;

import com.microrisc.simply.iqrf.dpa.v22x.devices.Generic;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_DeviceObject;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple {@code Generic} implementation.
 * <p>
 * @author Rostislav Spinar
 */
public final class SimpleGeneric
        extends DPA_DeviceObject implements Generic {

    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<Generic.MethodID, String> methodIdsMap
            = new EnumMap<>(Generic.MethodID.class);

    private static void initMethodIdsMap() {
        methodIdsMap.put(Generic.MethodID.SEND, "0");
    }

    static {
        initMethodIdsMap();
    }

    private short checkPeripheralId(short peripheralId) {
        if (!DPA_ProtocolProperties.PNUM_Properties.isReservedForStandard(peripheralId)) {
            throw new IllegalArgumentException("Peripheral ID must be in the range of standard peripherals.");
        }
        return peripheralId;
    }
    
    private short checkCmdId(short cmdId) {
        if (cmdId < DPA_ProtocolProperties.PCMD_VALUE_MIN
                || cmdId > DPA_ProtocolProperties.PCMD_VALUE_MAX) {
            throw new IllegalArgumentException("Command ID must be in range from "
                    + DPA_ProtocolProperties.PCMD_VALUE_MIN + " to " + DPA_ProtocolProperties.PCMD_VALUE_MAX);
        }
        return cmdId;
    }
    
    private short[] checkData(short[] data){
        if(data == null){
            throw new IllegalArgumentException("Data cannot be null.");
        }else if(data.length > DPA_ProtocolProperties.PDATA_MAX_LENGTH){
            throw new IllegalArgumentException("Data cannot be greater than " +
                    DPA_ProtocolProperties.PDATA_MAX_LENGTH);
        }
        return data;
    }

    public SimpleGeneric(
            String networkId, String nodeId, ConnectorService connector,
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }

    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((Generic.MethodID) methodId);
        if (methodIdStr == null) {
            return null;
        }

        switch ((Generic.MethodID) methodId) {
            case SEND:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[]{short.class, short.class, short[].class});
                checkPeripheralId((short)args[0]);
                checkCmdId((short)args[1]);
                checkData((short[])args[2]);
                return dispatchCall(
                        methodIdStr,
                        new Object[]{args[0], getRequestHwProfile(), args[1], args[2]},
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }

    @Override
    public String transform(Object methodId) {
        if (!(methodId instanceof Generic.MethodID)) {
            throw new IllegalArgumentException(
                    "Method ID must be of type Generic.MethodID."
            );
        }
        return methodIdsMap.get((Generic.MethodID) methodId);
    }

    // ASYNCHRONOUS METHODS IMPLEMENTATIONS

    @Override
    public UUID async_send(short peripheralId, short cmdId, short[] data) {
        checkPeripheralId(peripheralId);
        checkCmdId(cmdId);
        checkData(data);
        return dispatchCall(
                "0", new Object[]{peripheralId, cmdId, getRequestHwProfile(), data},
                getDefaultWaitingTimeout()
        );
    }
    
    // SYNCHRONOUS WRAPPERS IMPLEMENTATIONS
    
    @Override
    public Short[] send(short peripheralId, short cmdId, short[] data) {
        checkPeripheralId(peripheralId);
        checkCmdId(cmdId);
        checkData(data);
        UUID uid = dispatchCall(
                "0", new Object[]{peripheralId, cmdId, getRequestHwProfile(), data},
                getDefaultWaitingTimeout()
        );
        if (uid == null) {
            return null;
        }
        Short[] result = getCallResult(uid, Short[].class, getDefaultWaitingTimeout());
        if (result == null) {
            return null;
        }
        return result;
    }
}