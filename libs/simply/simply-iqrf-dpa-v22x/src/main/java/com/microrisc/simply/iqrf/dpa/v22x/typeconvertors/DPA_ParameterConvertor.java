/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microrisc.simply.iqrf.dpa.v22x.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Parameter;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
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
    @ConvertorFactoryMethod
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
