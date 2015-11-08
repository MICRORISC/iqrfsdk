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

package com.microrisc.simply.typeconvertors;

import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between Java String, which represents
 * integer number in range of 0..255 in radix 10, and uns8 type.
 * 
 * @author Michal Konopa
 */
public final class StringToByteConvertor extends PrimitiveConvertor {
   /** Type size of uns8 IQRF type. */
    static public final int TYPE_SIZE = 1;
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(StringToByteConvertor.class);
    
    /** Singleton. */
    private static final StringToByteConvertor instance = new StringToByteConvertor();
    
    
    /**
     * @return StringToByteConvertor instance 
     */
    @ConvertorFactoryMethod
    static public StringToByteConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    @Override
    public short[] toProtoValue(Object valueToConv) throws ValueConversionException {
        logger.debug("toProtoValue - start: valueToConv={}", valueToConv);
        
        if (!(valueToConv instanceof String)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short numValue = 0;
        try {
            numValue = Short.parseShort((String)valueToConv);
        } catch (NumberFormatException e) {
            throw new ValueConversionException(e);
        }
        
        short[] iqValue = new short[TYPE_SIZE];
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(numValue);
        
        byteBuffer.position(0);
        for (int byteId = 0; byteId < TYPE_SIZE; byteId++) {
            iqValue[byteId] = (short)(byteBuffer.get() & 0xFF);
        }
        
        logger.debug("toProtoValue - end: {}", iqValue);
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
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int byteId = 0; byteId < TYPE_SIZE; byteId++) {
            byteBuffer.put((byte)iqValue[byteId]);
        }
        
        short numValue = byteBuffer.getShort(0);
        String numValueStr = String.valueOf(numValue);
        
        logger.debug("toObject - end: {}", numValueStr);
        return numValueStr;
    }    
}
