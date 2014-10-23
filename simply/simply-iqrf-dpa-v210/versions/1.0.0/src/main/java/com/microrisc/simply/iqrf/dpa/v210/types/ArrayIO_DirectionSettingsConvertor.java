
package com.microrisc.simply.iqrf.dpa.v210.types;

import com.microrisc.simply.types.ArrayConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion between array of bytes and array of
 * {@code IO_DirectionSettings} objects.
 * 
 * @author Michal Konopa
 */
public final class ArrayIO_DirectionSettingsConvertor extends ArrayConvertor {
    /** Logger. */
    private static final Logger logger = 
            LoggerFactory.getLogger(ArrayIO_DirectionSettingsConvertor.class);
    
    private ArrayIO_DirectionSettingsConvertor() {
        this.elemConvertor = IO_DirectionSettingsConvertor.getInstance();
    }
    
    /** Singleton. */
    private static final ArrayIO_DirectionSettingsConvertor instance = new ArrayIO_DirectionSettingsConvertor();
    
    
    /**
     * @return {@code ArrayIO_DirectionSettingsConvertor} instance 
     */
    static public ArrayIO_DirectionSettingsConvertor getInstance() {
        return instance;
    }
    
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toIQValue - start: valueToConv={}", value);
        
        if (!(value instanceof IO_DirectionSettings[])) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        IO_DirectionSettings[] ioSettingsArr = (IO_DirectionSettings[]) value;
        int elemSize = elemConvertor.getGenericTypeSize();
        short[] finalIqValue = new short[elemSize * ioSettingsArr.length];
        int totalCopied = 0;
        for ( IO_DirectionSettings ioSettings : ioSettingsArr ) {
            short[] iqValue = elemConvertor.toProtoValue(ioSettings);
            System.arraycopy(iqValue, 0, finalIqValue, totalCopied, elemSize);
            totalCopied += elemSize;
        }
        
        logger.debug("toIQValue - end: {}", finalIqValue);
        return finalIqValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
