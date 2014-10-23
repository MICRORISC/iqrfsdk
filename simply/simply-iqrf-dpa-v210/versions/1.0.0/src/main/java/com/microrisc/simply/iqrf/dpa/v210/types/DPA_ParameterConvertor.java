
package com.microrisc.simply.iqrf.dpa.v210.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between Java {@code DPA_Parameter} type 
 * and IQRF DPA protocol bytes.
 * 
 * @author Michal Konopa
 */
public final class DPA_ParameterConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_ParameterConvertor.class);
    
    private DPA_ParameterConvertor() {}
    
    /** Singleton. */
    private static final DPA_ParameterConvertor instance = new DPA_ParameterConvertor();
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    
    /**
     * @return {@code DPA_ParameterConvertor} instance 
     */
    static public DPA_ParameterConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    private DPA_Parameter.DPA_ValueType getDPAValue(short sourceByte) 
            throws ValueConversionException {
        int dpaBits = sourceByte & 0x01;
        dpaBits |= sourceByte & 0x02;
        
        for (DPA_Parameter.DPA_ValueType dpaValue : DPA_Parameter.DPA_ValueType.values()) {
            if (dpaBits == dpaValue.getValueType()) {
                return dpaValue;
            }
        }
        throw new ValueConversionException("Unknown DPA value: " + dpaBits);
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof DPA_Parameter)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        DPA_Parameter dpaParams = (DPA_Parameter)value;
        short dpaParamsShort = (short)dpaParams.getDpaValueType().getValueType();
        
        if (dpaParams.isLedActivityOn()) {
            dpaParamsShort |= (short)Math.pow(2, 2);
        } 
        
        if (dpaParams.isFixedTimeslotUsed()) {
            dpaParamsShort |= (short)Math.pow(2, 3);
        }
        
        short[] protoValue = new short[TYPE_SIZE];
        protoValue[0] = dpaParamsShort;
               
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        DPA_Parameter.DPA_ValueType dpaValue = getDPAValue(protoValue[0]);
        boolean isLedActivityOn = ((protoValue[0] & 0x04) == 0x04);
        boolean isFixedTimeslotUsed = ((protoValue[0] & 0x08) == 0x08); 
        
        DPA_Parameter dpaParam = new DPA_Parameter(dpaValue, isLedActivityOn, isFixedTimeslotUsed);
        
        logger.debug("toObject - end: {}", dpaParam);
        return dpaParam;
    }
}
