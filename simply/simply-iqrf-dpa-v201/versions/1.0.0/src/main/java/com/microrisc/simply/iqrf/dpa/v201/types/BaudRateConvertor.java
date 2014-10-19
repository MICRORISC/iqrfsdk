
package com.microrisc.simply.iqrf.dpa.v201.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code BaudRate} enum values 
 * to IQRF DPA protocol representation. 
 * 
 * @author Michal Konopa
 */
public final class BaudRateConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BaudRateConvertor.class);
    
    private BaudRateConvertor() {
    }
    
    /** Singleton. */
    private static final BaudRateConvertor instance = new BaudRateConvertor();
    
    
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    // postitions of fields
    static private final int STATE_POS = 0;
    
    
    
    
    
    /**
     * @return BaudRateConvertor instance 
     */
    static public BaudRateConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof BaudRate)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        BaudRate baudRate = (BaudRate)value;
        short[] protoValue = new short[TYPE_SIZE];
        protoValue[STATE_POS] = (short)baudRate.getBaudRateConstant();
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
