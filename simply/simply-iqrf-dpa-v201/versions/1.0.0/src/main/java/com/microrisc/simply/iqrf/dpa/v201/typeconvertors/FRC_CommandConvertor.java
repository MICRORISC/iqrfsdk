
package com.microrisc.simply.iqrf.dpa.v201.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v201.types.FRC_Command;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between Java {@code FRC_Command} type 
 * and IQRF DPA protocol bytes.
 * 
 * @author Rostislav Spinar
 */
public final class FRC_CommandConvertor extends AbstractConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FRC_CommandConvertor.class);
    
    private FRC_CommandConvertor() {}
    
    /** Singleton. */
    private static final FRC_CommandConvertor instance = new FRC_CommandConvertor();
    
    
    /**
     * @return {@code FRC_CommandConvertor} instance 
     */
    static public FRC_CommandConvertor getInstance() {
        return instance;
    }
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if ( !(value instanceof FRC_Command) ) {
            throw new ValueConversionException("Value to convert is not of FRC_Command type.");
        }
        
        FRC_Command frcCmd = (FRC_Command)value;
        
        short[] protoValue = new short[1 + frcCmd.getUserData().length];
        protoValue[0] = (short)frcCmd.getId();
        System.arraycopy(frcCmd.getUserData(), 0, protoValue, 1, frcCmd.getUserData().length);
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported.");
    }         
}
