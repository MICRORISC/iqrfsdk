
package com.microrisc.simply.iqrf.dpa.v210.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.v210.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v210.devices.GeneralLED;
import com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers.GeneralLEDStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v210.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * Simple implementation of {@code GeneralLED} Device Interface.
 * 
 * @author Michal Konopa
 */
class SimpleGeneralLED 
extends DPA_DeviceObject implements GeneralLED {
    
    public SimpleGeneralLED(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((GeneralLED.MethodID) methodId);
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
        return GeneralLEDStandardTransformer.getInstance().transform(methodId);
    }
    
    @Override
    public UUID async_set(LED_State state) {
        return dispatchCall("1", new Object[] { getRequestHwProfile(), state } );
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
    public UUID async_get() {
        return dispatchCall("2", new Object[] { getRequestHwProfile() } );
    }

    @Override
    public LED_State get() {
        UUID uid = dispatchCall(
                "2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, LED_State.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public UUID async_pulse() {
        return dispatchCall("3", new Object[] { getRequestHwProfile() } );
    }

    @Override
    public VoidType pulse() {
        UUID uid = dispatchCall(
                "3", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout()
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
}
