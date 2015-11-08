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

import com.microrisc.simply.iqrf.dpa.v22x.types.LED_State;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code led_state} type values 
 * to {@code LED_State} enums. 
 * 
 * @author Michal Konopa
 */
public final class LED_StateConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(LED_StateConvertor.class);
    
    private LED_StateConvertor() {}
    
    /** Singleton. */
    private static final LED_StateConvertor instance = new LED_StateConvertor();
    
    /**
     * @return {@code LED_StateConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public LED_StateConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int STATE_POS = 0;
    
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof LED_State)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] protoValue = new short[TYPE_SIZE];
        LED_State ledState = (LED_State)value;
        protoValue[STATE_POS] = (short)ledState.getStateValue();
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        LED_State ledState = null;
        for (LED_State ledStateIt : LED_State.values()) {
            if (protoValue[STATE_POS] == ledStateIt.getStateValue()) {
                ledState = ledStateIt;
            }
        }
        
        if (ledState == null) {
            throw new ValueConversionException("Unknown LED state value: " + protoValue);
        }
        
        logger.debug("toObject - end: {}", ledState);
        return ledState;
    }
}
