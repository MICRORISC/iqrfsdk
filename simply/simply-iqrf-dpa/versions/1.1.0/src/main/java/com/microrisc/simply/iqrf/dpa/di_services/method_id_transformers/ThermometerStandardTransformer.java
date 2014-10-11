

package com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.devices.Thermometer;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for Thermometer. 
 * 
 * @author Michal Konopa
 */
public final class ThermometerStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<Thermometer.MethodID, String> methodIdsMap = 
            new EnumMap<Thermometer.MethodID, String>(Thermometer.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(Thermometer.MethodID.GET, "1");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final ThermometerStandardTransformer instance = 
            new ThermometerStandardTransformer();
    
    
    /**
     * @return ThermometerStandardTransformer instance 
     */
    static public ThermometerStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof Thermometer.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type Thermometer.MethodID."
            );
        }
        return methodIdsMap.get((Thermometer.MethodID) methodId);
    }
    
}
