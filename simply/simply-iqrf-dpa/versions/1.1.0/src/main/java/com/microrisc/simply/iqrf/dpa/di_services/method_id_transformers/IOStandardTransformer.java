

package com.microrisc.simply.iqrf.dpa.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.devices.IO;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for IO. 
 * 
 * @author Michal Konopa
 */
public final class IOStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<IO.MethodID, String> methodIdsMap = 
            new EnumMap<IO.MethodID, String>(IO.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(IO.MethodID.SET_DIRECTION, "1");
        methodIdsMap.put(IO.MethodID.SET_OUTPUT_STATE, "2");
        methodIdsMap.put(IO.MethodID.GET, "3");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final IOStandardTransformer instance = new IOStandardTransformer();
    
    
    /**
     * @return IOStandardTransformer instance 
     */
    static public IOStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof IO.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type IO.MethodID."
            );
        }
        return methodIdsMap.get((IO.MethodID) methodId);
    }
    
}
