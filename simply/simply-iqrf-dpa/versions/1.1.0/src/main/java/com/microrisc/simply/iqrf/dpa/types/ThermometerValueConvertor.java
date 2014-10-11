
package com.microrisc.simply.iqrf.dpa.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code Thermometer} type values 
 * to {@code Thermometer} objects. 
 * 
 * @author Michal Konopa
 */
public final class ThermometerValueConvertor extends PrimitiveConvertor {
    /** Size of returned response. */
    static public final int TYPE_SIZE = 3;
    
    // postitions of fields
    static private final int INT_VALUE_POS = 0;
    
    static private final int FULL_VALUE_POS = 1;
    static private final int FULL_VALUE_LENGTH = 2;
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ThermometerValueConvertor.class);
    
    /** Singleton. */
    private static final ThermometerValueConvertor instance = new ThermometerValueConvertor();
    
    
    /**
     * @return ThermometerValueConvertor instance 
     */
    static public ThermometerValueConvertor getInstance() {
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
        
        short value = protoValue[INT_VALUE_POS];
        
        short[] arrayValue = new short[FULL_VALUE_LENGTH];
        System.arraycopy(protoValue, FULL_VALUE_POS, arrayValue, 0, FULL_VALUE_LENGTH);
        
        byte fractialPart = (byte)(arrayValue[0] & 0x0F);
        
        short highNibble = (short) (arrayValue[1] << 4);
        short lowNibble = (short)(arrayValue[0] >> 4);
        //redundant information, see value
        short integerPart = (short)( highNibble | lowNibble);
        
        arrayValue[0] = integerPart;
        arrayValue[1] = fractialPart;
        
        Thermometer_values thermometerValues = new Thermometer_values(value, fractialPart);
        
        logger.debug("toObject - end: {}", thermometerValues);
        return thermometerValues;
    }
}
