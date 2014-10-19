
package com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.devices.GeneralLED;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for general LED. 
 * 
 * @author Michal Konopa
 */
public final class GeneralLEDStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<GeneralLED.MethodID, String> methodIdsMap = 
            new EnumMap<GeneralLED.MethodID, String>(GeneralLED.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(GeneralLED.MethodID.SET, "1");
        methodIdsMap.put(GeneralLED.MethodID.GET, "2");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final GeneralLEDStandardTransformer instance = new GeneralLEDStandardTransformer();
    
    
    /**
     * @return GeneralLEDStandardTransformer instance 
     */
    static public GeneralLEDStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof GeneralLED.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type GeneralLED.MethodID."
            );
        }
        return methodIdsMap.get((GeneralLED.MethodID) methodId);
    }
    
}
