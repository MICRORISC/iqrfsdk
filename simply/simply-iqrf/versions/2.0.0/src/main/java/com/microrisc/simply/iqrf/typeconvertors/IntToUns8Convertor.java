
package com.microrisc.simply.iqrf.typeconvertors;

import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs conversion between Java Integer to IQRF uns8 type.
 * Only the lowest 8 significant bits of Java {@code Integer} are converted, 
 * others are ignored.
 * 
 * @author Michal Konopa
 */
public class IntToUns8Convertor extends PrimitiveConvertor  {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IntToUns8Convertor.class);
    
    private IntToUns8Convertor() {}
    
    /** Singleton. */
    private static final IntToUns8Convertor instance = new IntToUns8Convertor();
    
    /**
     * @return {@code IntToUns8Convertor} instance 
     */
    static public IntToUns8Convertor getInstance() {
        return instance;
    }
    
    /** Type size of uns8 IQRF type. */
    static public final int TYPE_SIZE = 1;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    /**
     * @throws ValueConversionException if the converted value doesn't have 
     *         the {@code Integer} type
     */
    @Override
    public short[] toProtoValue(Object javaObj) throws ValueConversionException {
        logger.debug("toProtoValue - start: valueToConv={}", javaObj);
        
        if ( !(javaObj instanceof Integer) ) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] iqValue = new short[TYPE_SIZE];
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(((Integer)javaObj));
        
        byteBuffer.position(0);
        for (int byteId = 0; byteId < TYPE_SIZE; byteId++) {
            iqValue[byteId] = (short)(byteBuffer.get() & 0xFF);
        }
        
        logger.debug("toProtoValue - end: {}", iqValue);
        return iqValue;
    }

    @Override
    public Object toObject(short[] iqrfValue) throws ValueConversionException {
        logger.debug("toObject - start: iqValue={}", iqrfValue);
        
        if ( iqrfValue.length != TYPE_SIZE ) {
            throw new ValueConversionException(
                "Argument length doesn't match with type size"
            );
        }
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int byteId = 0; byteId < TYPE_SIZE; byteId++) {
            byteBuffer.put((byte)iqrfValue[byteId]);
        }
        Integer intObj = byteBuffer.getInt(0);
        
        logger.debug("toObject - end: {}", intObj);
        return intObj;
    }
}
