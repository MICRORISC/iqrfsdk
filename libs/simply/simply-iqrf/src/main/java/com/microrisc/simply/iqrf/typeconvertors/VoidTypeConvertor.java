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

package com.microrisc.simply.iqrf.typeconvertors;

import com.microrisc.simply.iqrf.types.VoidType;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from and to IQRF void type. 
 * Peer Java type: {@code VoidType} (Simply specific class)
 * 
 * @author Michal Konopa
 */
public final class VoidTypeConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(VoidTypeConvertor.class);
    
    private VoidTypeConvertor() {}
    
    /** Singleton. */
    private static final VoidTypeConvertor instance = new VoidTypeConvertor();
    
    
    /**
     * @return {@code VoidTypeConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public VoidTypeConvertor getInstance() {
        return instance;
    }
    
    /** Type size of 'void' IQRF type. */
    static public final int TYPE_SIZE = 0;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    @Override
    public short[] toProtoValue(Object valueToConv) throws ValueConversionException {
        logger.debug("toIQValue - start: valueToConv={}", valueToConv);
        
        if (!(valueToConv instanceof VoidType)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] iqValue = new short[TYPE_SIZE];
        
        logger.debug("toIQValue - end: {}", iqValue);
        return iqValue;
    }
  
    @Override
    public Object toObject(short[] iqValue) throws ValueConversionException {
        logger.debug("toObject - start: iqValue={}", iqValue);
        
        if ( iqValue.length != TYPE_SIZE ) {
            throw new ValueConversionException(
                "Argument length doesn't match with type size"
            );
        }
        
        VoidType voidType = new VoidType();
        
        logger.debug("toIQValue - end: {}", voidType);
        return voidType;
    }
}
