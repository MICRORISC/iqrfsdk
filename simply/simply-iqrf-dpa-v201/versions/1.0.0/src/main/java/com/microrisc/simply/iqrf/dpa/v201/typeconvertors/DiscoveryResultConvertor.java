
package com.microrisc.simply.iqrf.dpa.v201.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v201.types.DiscoveryResult;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code discovery_result} type values 
 * to {@code DiscoveryResult} objects. 
 * 
 * @author Michal Konopa
 */
public final class DiscoveryResultConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryResultConvertor.class);
    
    private DiscoveryResultConvertor() {}
    
    /** Singleton. */
    private static final DiscoveryResultConvertor instance = new DiscoveryResultConvertor();
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    // postitions of fields
    static private final int DISC_DEV_NUM_POS = 0;
    
    
    /**
     * @return {@code DiscoveryResultConvertor} instance 
     */
    static public DiscoveryResultConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }

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
        
        int discDevicesNum = protoValue[DISC_DEV_NUM_POS];
        DiscoveryResult discResult = new DiscoveryResult(discDevicesNum);
        
        logger.debug("toObject - end: {}", discResult);
        return discResult;
    }
}
