
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.devices.Thermometer;
import com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers.ThermometerStandardTransformer;
import com.microrisc.simply.iqrf.dpa.types.Thermometer_values;
import java.util.UUID;

/**
 * Simple {@code Thermometer} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleThermometer 
extends DPA_DeviceObject implements Thermometer {
    
    public SimpleThermometer(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((Thermometer.MethodID) methodId);
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
        return ThermometerStandardTransformer.getInstance().transform(methodId);
    }
    
    @Override
    public UUID async_get() {
        return dispatchCall("1", new Object[] { getRequestHwProfile() } );
    }

    @Override
    public Thermometer_values get() {
        UUID uid = dispatchCall("1", new Object[] { getRequestHwProfile() } , getDefaultWaitingTimeout() );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, Thermometer_values.class, getDefaultWaitingTimeout() );
    }
}
