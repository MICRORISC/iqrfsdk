
package com.microrisc.simply.iqrf.dpa.v201.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v201.types.SleepInfo;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between {@code SleepInfoConvertor} objects 
 * and protocol packets.
 * 
 * @author Michal Konopa
 */
public final class SleepInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SleepInfoConvertor.class);
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 3;
    
    // postitions of fields
    static private final int TIME_POS = 0;
    static private final int TIME_LENGTH = 2;
    static private final int CONTROL_POS = 2;
    
    
    private SleepInfoConvertor() {}
    
    /** Singleton. */
    private static final SleepInfoConvertor instance = new SleepInfoConvertor();
    
    
    /**
     * @return {@code OsInfoConvertor} instance 
     */
    static public SleepInfoConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    // puts specified time value into specified protocol message 
    private void putTimeValue(short[] protoValue, int time) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(time);
        
        for (int byteId = 0; byteId < TIME_LENGTH; byteId++) {
            protoValue[TIME_POS + byteId] = byteBuffer.get(byteId);
        }
    }
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof SleepInfo)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        SleepInfo sleepInfo = (SleepInfo)value;
        short[] protoValue = new short[TYPE_SIZE];
        
        putTimeValue(protoValue, sleepInfo.getTime());
        protoValue[CONTROL_POS] = (short)sleepInfo.getControl();
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
