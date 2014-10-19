

package com.microrisc.simply.iqrf.dpa.v201.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v201.devices.OS;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for OS. 
 * 
 * @author Michal Konopa
 */
public final class OSStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<OS.MethodID, String> methodIdsMap = 
            new EnumMap<OS.MethodID, String>(OS.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(OS.MethodID.READ, "1");
        methodIdsMap.put(OS.MethodID.RESET, "2");
        methodIdsMap.put(OS.MethodID.READ_HWP_CONFIGURATION, "3");
        methodIdsMap.put(OS.MethodID.RUN_RFPGM, "4");
        methodIdsMap.put(OS.MethodID.SLEEP, "5");
        methodIdsMap.put(OS.MethodID.BATCH, "6");
        methodIdsMap.put(OS.MethodID.SET_USEC_USER_ADDRESS, "7");
        methodIdsMap.put(OS.MethodID.SET_MID, "8");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final OSStandardTransformer instance = new OSStandardTransformer();
    
    
    /**
     * @return OSStandardTransformer instance 
     */
    static public OSStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof OS.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type OS.MethodID."
            );
        }
        return methodIdsMap.get((OS.MethodID) methodId);
    }
    
}
