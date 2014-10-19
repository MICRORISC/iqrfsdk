

package com.microrisc.simply.iqrf.dpa.v201.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v201.devices.PWM;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for PWM. 
 * 
 * @author Michal Konopa
 */
public final class PWMStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<PWM.MethodID, String> methodIdsMap = 
            new EnumMap<PWM.MethodID, String>(PWM.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(PWM.MethodID.SET, "1");
    }
    
    static  {
        initMethodIdsMap();
    }
   
    /** Singleton. */
    private static final PWMStandardTransformer instance = new PWMStandardTransformer();
    
    
    /**
     * @return PWMStandardTransformer instance 
     */
    static public PWMStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof PWM.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type PWM.MethodID."
            );
        }
        return methodIdsMap.get((PWM.MethodID) methodId);
    }
    
}
