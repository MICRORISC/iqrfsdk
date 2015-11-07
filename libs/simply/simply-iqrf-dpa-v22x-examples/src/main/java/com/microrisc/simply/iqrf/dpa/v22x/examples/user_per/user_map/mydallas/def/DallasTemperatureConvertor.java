/*
 * Copyright 2015 Martin Strouhal
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
package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.user_map.mydallas.def;

import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * Provides functionality for converting from IQRF data to temperature.
 * 
 * @author Martin Strouhal
 */
public final class DallasTemperatureConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DallasTemperatureConvertor.class);
    
    private DallasTemperatureConvertor() {}
    
    /** Singleton. */
    private static final DallasTemperatureConvertor instance = new DallasTemperatureConvertor();
    
    
    /**
     * @return {@code DallasTemperatureConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public DallasTemperatureConvertor getInstance() {
        return instance;
    }
    
    /** Type size of IQRF data with temperature. */
    static public final int TYPE_SIZE = 2;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    @Override
    public short[] toProtoValue(Object valueToConv) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported");
    }
  
    @Override
    public Object toObject(short[] iqValue) throws ValueConversionException {
        logger.debug("toObject - start: iqValue={}", iqValue);
        
        if ( iqValue.length != TYPE_SIZE ) {
            throw new ValueConversionException(
                "Argument length doesn't match with type size");
        }
        
        int all = iqValue[1];
        all <<= 8;
        all += iqValue[0];
        
        float decimal = all & (0xFFF0);
        decimal *= 0.0001f;
        
        all >>=4;
        
        decimal += all;        
        
        logger.debug("toObject - end: {}", decimal);
        return decimal;
    }   
}