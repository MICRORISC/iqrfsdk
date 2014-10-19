

package com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.devices.PeripheralInfoGetter;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for Peripheral Info Getter. 
 * 
 * @author Michal Konopa
 */
public final class PeripheralInfoGetterStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<PeripheralInfoGetter.MethodID, String> methodIdsMap = 
            new EnumMap<PeripheralInfoGetter.MethodID, String>(PeripheralInfoGetter.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(PeripheralInfoGetter.MethodID.GET_PERIPHERAL_ENUMERATION, "1");
        methodIdsMap.put(PeripheralInfoGetter.MethodID.GET_PERIPHERAL_INFO, "2");
        methodIdsMap.put(PeripheralInfoGetter.MethodID.GET_MORE_PERIPHERALS_INFO, "3");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final PeripheralInfoGetterStandardTransformer instance = 
            new PeripheralInfoGetterStandardTransformer();
    
    
    /**
     * @return PeripheralInfoGetterStandardTransformer instance 
     */
    static public PeripheralInfoGetterStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof PeripheralInfoGetter.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type PeripheralInfoGetter.MethodID."
            );
        }
        return methodIdsMap.get((PeripheralInfoGetter.MethodID) methodId);
    }
    
}
