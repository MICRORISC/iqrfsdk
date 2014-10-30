
package com.microrisc.simply.iqrf.dpa.v201.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v201.types.IO_Command;
import com.microrisc.simply.typeconvertors.ArrayConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion between array of bytes and array of
 * {@code IO_Command} objects.
 * 
 * @author Michal Konopa
 */
public final class ArrayIO_CommandConvertor extends ArrayConvertor {
    /** Logger. */
    private static final Logger logger = 
            LoggerFactory.getLogger(ArrayIO_CommandConvertor.class);
    
    private ArrayIO_CommandConvertor() {
        this.elemConvertor = IO_CommandConvertor.getInstance();
    }
    
    /** Singleton. */
    private static final ArrayIO_CommandConvertor instance = new ArrayIO_CommandConvertor();
    
    
    /**
     * @return ArrayIO_CommandConvertor instance 
     */
    static public ArrayIO_CommandConvertor getInstance() {
        return instance;
    }
    
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toIQValue - start: valueToConv={}", value);
        
        if (!(value instanceof IO_Command[])) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        IO_Command[] ioCommandArr = (IO_Command[]) value;
        int elemSize = elemConvertor.getGenericTypeSize();
        short[] finalIqValue = new short[elemSize * ioCommandArr.length];
        int totalCopied = 0;
        for (IO_Command ioSettings : ioCommandArr) {
            short[] iqValue = elemConvertor.toProtoValue(ioSettings);
            System.arraycopy(iqValue, 0, finalIqValue, totalCopied, elemSize);
            totalCopied += elemSize;
        }
        
        logger.debug("toIQValue - end: {}", finalIqValue);
        return finalIqValue;
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
