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

import com.microrisc.simply.iqrf.dpa.v22x.types.BaudRate;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code BaudRate} enum values 
 * to IQRF DPA protocol representation. 
 * 
 * @author Michal Konopa
 */
public final class BaudRateConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BaudRateConvertor.class);
    
    private BaudRateConvertor() {
    }
    
    /** Singleton. */
    private static final BaudRateConvertor instance = new BaudRateConvertor();
    
    
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    // postitions of fields
    static private final int STATE_POS = 0;
    
    /**
     * @return {@code BaudRateConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public BaudRateConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof BaudRate)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        BaudRate baudRate = (BaudRate)value;
        short[] protoValue = new short[TYPE_SIZE];
        protoValue[STATE_POS] = (short)baudRate.getBaudRateConstant();
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
