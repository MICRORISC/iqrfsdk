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
import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Custom;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple {@code MyCustom} implementation.
 * <p>
 * @author Martin Strouhal
 */
public final class SimpleCustom
        extends DPA_DeviceObject implements Custom {

    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<Custom.MethodID, String> methodIdsMap
            = new EnumMap<>(Custom.MethodID.class);

    private static void initMethodIdsMap() {
        methodIdsMap.put(Custom.MethodID.SEND, "0");
    }

    static {
        initMethodIdsMap();
    }

    private short checkPeripheralId(short peripheralId) {
        if (!DPA_ProtocolProperties.PNUM_Properties.isUser(peripheralId)) {
            throw new IllegalArgumentException("Peripheral ID must be in use with Custom in range of user peripherals.");
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

    public SimpleCustom(
            String networkId, String nodeId, ConnectorService connector,
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }

    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((Custom.MethodID) methodId);
        if (methodIdStr == null) {
            return null;
        }

        switch ((Custom.MethodID) methodId) {
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
        if (!(methodId instanceof Custom.MethodID)) {
            throw new IllegalArgumentException(
                    "Method ID must be of type MyCustom.MethodID."
            );
        }
        return methodIdsMap.get((Custom.MethodID) methodId);
    }

    // ASYNCHRONOUS METHODS IMPLEMENTATIONS

    @Override
    public UUID async_send(short peripheralId, short cmdId, short[] data) {
        checkPeripheralId(peripheralId);
        checkCmdId(cmdId);
        checkData(data);
        return dispatchCall(
                "0", new Object[]{peripheralId, getRequestHwProfile(), cmdId, data},
                getDefaultWaitingTimeout()
        );
    }
    
    // SYNCHRONOUS WRAPPERS IMPLEMENTATIONS
    
    @Override
    public short[] send(short peripheralId, short cmdId, short[] data) {
        checkPeripheralId(peripheralId);
        checkCmdId(cmdId);
        checkData(data);
        UUID uid = dispatchCall(
                "0", new Object[]{peripheralId, getRequestHwProfile(), cmdId, data},
                getDefaultWaitingTimeout()
        );
        if (uid == null) {
            return null;
        }
        short[] result = getCallResult(uid, short[].class, getDefaultWaitingTimeout());
        if (result == null) {
            return null;
        }
        return result;
    }
}