
package com.microrisc.simply.iqrf.dpa.types;

import com.microrisc.simply.types.ValueConversionException;
import com.microrisc.simply.types.PrimitiveConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code discovery_result} type values 
 * to {@code DiscoveryResult} objects. 
 * 
 * @author Michal Konopa
 */
public class DiscoveryResultConvertor extends PrimitiveConvertor {
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    // postitions of fields
    static private final int DISC_DEV_NUM_POS = 0;
    
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryResultConvertor.class);
    
    
    /** Singleton. */
    private static final DiscoveryResultConvertor instance = new DiscoveryResultConvertor();
    
    
    /**
     * @return DiscoveryResultConvertor instance 
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
     * @param value
     * @return
     * @throws ValueConversionException 
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
