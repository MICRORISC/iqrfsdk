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

import com.microrisc.simply.iqrf.dpa.v22x.types.SleepInfo;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between {@code SleepInfoConvertor} objects 
 * and protocol packets.
 * 
 * @author Michal Konopa
 */
public final class SleepInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SleepInfoConvertor.class);
    
    private SleepInfoConvertor() {}
    
    /** Singleton. */
    private static final SleepInfoConvertor instance = new SleepInfoConvertor();
    
    
    /**
     * @return {@code SleepInfoConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public SleepInfoConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 3;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int TIME_POS = 0;
    static private final int TIME_LENGTH = 2;
    static private final int CONTROL_POS = 2;
    
    
    // puts specified time value into specified protocol message 
    private void putTimeValue(short[] protoValue, int time) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(time);
        
        for (int byteId = 0; byteId < TIME_LENGTH; byteId++) {
            protoValue[TIME_POS + byteId] = byteBuffer.get(byteId);
        }
    }
    

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof SleepInfo)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        SleepInfo sleepInfo = (SleepInfo)value;
        short[] protoValue = new short[TYPE_SIZE];
        
        putTimeValue(protoValue, sleepInfo.getTime());
        protoValue[CONTROL_POS] = (short)sleepInfo.getControl();
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
