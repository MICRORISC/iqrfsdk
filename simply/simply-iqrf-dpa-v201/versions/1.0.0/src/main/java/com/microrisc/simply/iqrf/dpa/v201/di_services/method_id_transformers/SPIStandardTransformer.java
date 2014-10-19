

package com.microrisc.simply.iqrf.dpa.v201.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v201.devices.SPI;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for SPI. 
 * 
 * @author Michal Konopa
 */
public final class SPIStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<SPI.MethodID, String> methodIdsMap = 
            new EnumMap<SPI.MethodID, String>(SPI.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(SPI.MethodID.WRITE_AND_READ, "1");
    }
    
    static  {
        initMethodIdsMap();
    }
    
    /** Singleton. */
    private static final SPIStandardTransformer instance = new SPIStandardTransformer();
    
    
    /**
     * @return SPIStandardTransformer instance 
     */
    static public SPIStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof SPI.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type SPI.MethodID."
            );
        }
        return methodIdsMap.get((SPI.MethodID) methodId);
    }
    
}
