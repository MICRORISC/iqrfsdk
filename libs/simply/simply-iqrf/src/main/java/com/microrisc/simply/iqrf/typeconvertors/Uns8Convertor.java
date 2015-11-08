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

import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from and to IQRF uns8 type. 
 * Peer Java type: {@code Short}
 * 
 * @author Michal Konopa
 */
public final class Uns8Convertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(Uns8Convertor.class);
    
    private Uns8Convertor() {}
    
    /** Singleton. */
    private static final Uns8Convertor instance = new Uns8Convertor();
   
    /**
     * @return {@code Uns8Convertor} instance 
     */
    @ConvertorFactoryMethod
    static public Uns8Convertor getInstance() {
        return instance;
    }
    
    /** Type size of uns8 IQRF type. */
    static public final int TYPE_SIZE = 1;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    @Override
    public short[] toProtoValue(Object valueToConv) throws ValueConversionException {
        logger.debug("toProtoValue - start: valueToConv={}", valueToConv);
        
        if (!(valueToConv instanceof Short)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] iqValue = new short[TYPE_SIZE];
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(((Short)valueToConv));
        
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
        
        if (iqValue.length != TYPE_SIZE) {
            throw new ValueConversionException("Argument length doesn't match with "
                    + "type size");
        }
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int byteId = 0; byteId < TYPE_SIZE; byteId++) {
            byteBuffer.put((byte)iqValue[byteId]);
        }
        
        Short shortObj = byteBuffer.getShort(0);
        
        logger.debug("toObject - end: {}", shortObj);
        return shortObj;
    }   
}
