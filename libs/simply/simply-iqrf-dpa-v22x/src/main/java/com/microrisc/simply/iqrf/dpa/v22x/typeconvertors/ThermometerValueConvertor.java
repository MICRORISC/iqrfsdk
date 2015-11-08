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

import com.microrisc.simply.iqrf.dpa.v22x.types.Thermometer_values;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
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
    @ConvertorFactoryMethod
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
