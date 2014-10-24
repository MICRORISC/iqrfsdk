
package com.microrisc.simply.iqrf.dpa.v201.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between {@code RemotelyBondedModuleId} 
 * objects and protocol packets.
 * 
 * @author Michal Konopa
 */
public final class RemotelyBondedModuleIdConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(RemotelyBondedModuleIdConvertor.class);
    
    private RemotelyBondedModuleIdConvertor() {}
    
    /** Singleton. */
    private static final RemotelyBondedModuleIdConvertor instance = 
            new RemotelyBondedModuleIdConvertor();
    
    
    /**
     * @return {@code RemotelyBondedModuleIdConvertor} instance 
     */
    static public RemotelyBondedModuleIdConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 6;
    
    // postitions of fields
    static private final int MODULE_ID_POS = 0;
    static private final int MODULE_ID_LENGTH = 4;
    static private final int USER_DATA_POS = 4;
    static private final int USER_DATA_LENGTH = 2;
    
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        short[] moduleId = new short[MODULE_ID_LENGTH];
        System.arraycopy(protoValue, MODULE_ID_POS, moduleId, 0, MODULE_ID_LENGTH);
        
        short[] userData = new short[USER_DATA_LENGTH];
        System.arraycopy(protoValue, USER_DATA_POS, userData, 0, USER_DATA_LENGTH);
        
        RemotelyBondedModuleId remoteBondedModuleId = new RemotelyBondedModuleId(
                moduleId, userData
        );
        
        logger.debug("toObject - end: {}", remoteBondedModuleId);
        return remoteBondedModuleId;
    }
}
