

package com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.devices.GeneralMemory;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for general memory. 
 * 
 * @author Michal Konopa
 */
public final class GeneralMemoryStandardTransformer implements MethodIdTransformer {
    
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<GeneralMemory.MethodID, String> methodIdsMap = 
            new EnumMap<GeneralMemory.MethodID, String>(GeneralMemory.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(GeneralMemory.MethodID.READ, "1");
        methodIdsMap.put(GeneralMemory.MethodID.WRITE, "2");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final GeneralMemoryStandardTransformer instance = new GeneralMemoryStandardTransformer();
    
    
    /**
     * @return GeneralMemoryStandardTransformer instance 
     */
    static public GeneralMemoryStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof GeneralMemory.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type GeneralMemory.MethodID."
            );
        }
        return methodIdsMap.get((GeneralMemory.MethodID) methodId);
    }
    
}
