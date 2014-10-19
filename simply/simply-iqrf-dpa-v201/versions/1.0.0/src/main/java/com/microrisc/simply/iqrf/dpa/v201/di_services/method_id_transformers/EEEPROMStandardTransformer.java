

package com.microrisc.simply.iqrf.dpa.v201.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v201.devices.EEEPROM;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for EEEPROM. 
 * 
 * @author Michal Konopa
 */
public final class EEEPROMStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<EEEPROM.MethodID, String> methodIdsMap = 
            new EnumMap<EEEPROM.MethodID, String>(EEEPROM.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(EEEPROM.MethodID.READ, "1");
        methodIdsMap.put(EEEPROM.MethodID.WRITE, "2");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final EEEPROMStandardTransformer instance = new EEEPROMStandardTransformer();
    
    
    /**
     * @return EEEPROMStandardTransformer instance 
     */
    static public EEEPROMStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof EEEPROM.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type EEEPROM.MethodID."
            );
        }
        return methodIdsMap.get((EEEPROM.MethodID) methodId);
    }
}
