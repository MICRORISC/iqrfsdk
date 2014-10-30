
package com.microrisc.simply.iqrf.dpa.v201.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v201.types.Thermometer_values;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code Thermometer} type values 
 * to {@code Thermometer} objects. 
 * 
 * @author Michal Konopa
 */
public final class ThermometerValueConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ThermometerValueConvertor.class);
    
    private ThermometerValueConvertor() {}
    
    /** Singleton. */
    private static final ThermometerValueConvertor instance = new ThermometerValueConvertor();
    
    
    /**
     * @return {@code ThermometerValueConvertor} instance 
     */
    static public ThermometerValueConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 3;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int INT_VALUE_POS = 0;
    
    static private final int FULL_VALUE_POS = 1;
    static private final int FULL_VALUE_LENGTH = 2;
    

    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        short value = protoValue[INT_VALUE_POS];
        
        short[] fullValue = new short[FULL_VALUE_LENGTH];
        System.arraycopy(protoValue, FULL_VALUE_POS, fullValue, 0, FULL_VALUE_LENGTH);
        byte fractialPart = (byte)(fullValue[0] & 0x0F);
        
        Thermometer_values thermometerValues = new Thermometer_values(value, fractialPart);
        
        logger.debug("toObject - end: {}", thermometerValues);
        return thermometerValues;
    }
}
