

package com.microrisc.simply.iqrf.dpa.v201.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v201.devices.FRC;
import java.util.EnumMap;
import java.util.Map;

/**
    * Standard method ID transformer for FRC. 
 * 
 * @author Michal Konopa
 */
public final class FRCStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<FRC.MethodID, String> methodIdsMap = 
            new EnumMap<FRC.MethodID, String>(FRC.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(FRC.MethodID.SEND, "1");
        methodIdsMap.put(FRC.MethodID.EXTRA_RESULT, "2");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final FRCStandardTransformer instance = new FRCStandardTransformer();
    
    
    /**
     * @return FRCStandardTransformer instance 
     */
    static public FRCStandardTransformer getInstance() {
        return instance;
    }
    
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof FRC.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type FRC.MethodID."
            );
        }
        return methodIdsMap.get((FRC.MethodID) methodId);
    }
    
}
