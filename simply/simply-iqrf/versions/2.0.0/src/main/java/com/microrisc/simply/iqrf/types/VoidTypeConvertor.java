
package com.microrisc.simply.iqrf.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from and to IQRF void type. 
 * Peer Java type: VoidType (Simply specific class)
 * 
 * @author Michal Konopa
 */
public final class VoidTypeConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(VoidTypeConvertor.class);
    
    private VoidTypeConvertor() {}
    
    /** Singleton. */
    private static final VoidTypeConvertor instance = new VoidTypeConvertor();
    
    
    /**
     * @return VoidTypeConvertor instance 
     */
    static public VoidTypeConvertor getInstance() {
        return instance;
    }
    
    /** Type size of 'void' IQRF type. */
    static public final int TYPE_SIZE = 0;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    @Override
    public short[] toProtoValue(Object valueToConv) throws ValueConversionException {
        logger.debug("toIQValue - start: valueToConv={}", valueToConv);
        
        if (!(valueToConv instanceof VoidType)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] iqValue = new short[TYPE_SIZE];
        
        logger.debug("toIQValue - end: {}", iqValue);
        return iqValue;
    }
  
    @Override
    public Object toObject(short[] iqValue) throws ValueConversionException {
        logger.debug("toObject - start: iqValue={}", iqValue);
        
        if (iqValue.length != TYPE_SIZE) {
            throw new ValueConversionException(
                "Argument length doesn't match with type size"
            );
        }
        
        VoidType voidType = new VoidType();
        
        logger.debug("toIQValue - end: {}", voidType);
        return voidType;
    }
}
