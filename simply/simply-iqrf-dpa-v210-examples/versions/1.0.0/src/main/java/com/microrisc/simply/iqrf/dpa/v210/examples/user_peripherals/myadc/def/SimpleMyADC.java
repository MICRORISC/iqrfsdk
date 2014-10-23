package com.microrisc.simply.iqrf.dpa.v210.examples.user_peripherals.myadc.def;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.iqrf.dpa.v210.DPA_DeviceObject;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple {@code ADC} implementation.
 *
 * @author Martin Strouhal
 */
public final class SimpleMyADC extends DPA_DeviceObject implements MyADC {

    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<MyADC.MethodID, String> methodIdsMap = new EnumMap<>(
            MyADC.MethodID.class);

    private static void initMethodIdsMap() {
        methodIdsMap.put(MyADC.MethodID.GET, "0");
    }

    static {
        initMethodIdsMap();
    }

    public SimpleMyADC(String networkId, String nodeId,
            ConnectorService connector,
            CallRequestProcessingInfoContainer resultsContainer) {
        super(networkId, nodeId, connector, resultsContainer);
    }

    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((MyADC.MethodID) methodId);
        if (methodIdStr == null) {
            return null;
        }

        if (args == null) {
            return dispatchCall(methodIdStr, new Object[]{ getRequestHwProfile() });
        }

        Object[] argsWithHwProfile = new Object[args.length + 1];
        argsWithHwProfile[0] = getRequestHwProfile();
        System.arraycopy(args, 0, argsWithHwProfile, 1, args.length);
        return dispatchCall(methodIdStr, argsWithHwProfile);
    }

    @Override
    public String transform(Object methodId) {
        if (!(methodId instanceof MyADC.MethodID)) {
            throw new IllegalArgumentException(
                    "Method ID must be of type MyADC.MethodID."
            );
        }
        return methodIdsMap.get((MyADC.MethodID) methodId);
    }

    @Override
    public int get() {
        UUID uid = dispatchCall("0", new Object[]{ getRequestHwProfile() },
                getDefaultWaitingTimeout());
        if (uid == null) {
            return Integer.MAX_VALUE;
        }
        return getCallResult(uid, int.class, getDefaultWaitingTimeout());
    }
}
