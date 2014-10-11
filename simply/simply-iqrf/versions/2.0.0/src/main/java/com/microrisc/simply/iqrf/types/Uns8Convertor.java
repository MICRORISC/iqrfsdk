package com.microrisc.simply.iqrf.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from and to IQRF uns8 type. 
 * Peer Java type: Short
 * 
 * @author Michal Konopa
 */
public final class Uns8Convertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(Uns8Convertor.class);
    
    private Uns8Convertor() {}
    
    /** Singleton. */
    private static final Uns8Convertor instance = new Uns8Convertor();
   
    /**
     * @return Uns8Convertor instance 
     */
    static public Uns8Convertor getInstance() {
        return instance;
    }
    
    /** Type size of uns8 IQRF type. */
    static public final int TYPE_SIZE = 1;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    @Override
    public short[] toProtoValue(Object valueToConv) throws ValueConversionException {
        logger.debug("toProtoValue - start: valueToConv={}", valueToConv);
        
        if (!(valueToConv instanceof Short)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] iqValue = new short[TYPE_SIZE];
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(((Short)valueToConv).shortValue());
        
        byteBuffer.position(0);
        for (int byteId = 0; byteId < TYPE_SIZE; byteId++) {
            iqValue[byteId] = (short)(byteBuffer.get() & 0xFF);
        }
        
        logger.debug("toProtoValue - end: {}", iqValue);
        return iqValue;
    }
  
    @Override
    public Object toObject(short[] iqValue) throws ValueConversionException {
        logger.debug("toObject - start: iqValue={}", iqValue);
        
        if (iqValue.length != TYPE_SIZE) {
            throw new ValueConversionException("Argument length doesn't match with "
                    + "type size");
        }
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int byteId = 0; byteId < TYPE_SIZE; byteId++) {
            byteBuffer.put((byte)iqValue[byteId]);
        }
        
        Short shortObj = byteBuffer.getShort(0);
        
        logger.debug("toObject - end: {}", shortObj);
        return shortObj;
    }   
}
