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
package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.user_map.myadc.def;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_DeviceObject;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple {@link MyPhotoResistor} implementation.
 * <p>
 * @author Martin Strouhal
 */
public class SimpleMyPhotoResistor
        extends DPA_DeviceObject implements MyPhotoResistor {

    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<MyPhotoResistor.MethodID, String> methodIdsMap
            = new EnumMap<>(MyPhotoResistor.MethodID.class);

    private static void initMethodIdsMap() {
        methodIdsMap.put(MyPhotoResistor.MethodID.GET, "0");
    }

    static {
        initMethodIdsMap();
    }

    public SimpleMyPhotoResistor(
            String networkId, String nodeId, ConnectorService connector,
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }

    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((MyPhotoResistor.MethodID) methodId);
        if (methodIdStr == null) {
            return null;
        }

        switch ((MyPhotoResistor.MethodID) methodId) {
            case GET:
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
        if (!(methodId instanceof MyPhotoResistor.MethodID)) {
            throw new IllegalArgumentException(
                    "Method ID must be of type MyPhotoResistor.MethodID."
            );
        }
        return methodIdsMap.get((MyPhotoResistor.MethodID) methodId);
    }

    @Override
    public int get() {
        UUID uid = dispatchCall(
                "0", new Object[]{getRequestHwProfile()}, getDefaultWaitingTimeout()
        );
        if (uid == null) {
            return Integer.MAX_VALUE;
        }

        Integer result = getCallResult(uid, int.class, getDefaultWaitingTimeout());
        if (result == null) {
            return Integer.MAX_VALUE;
        }
        return result;
    }
}
