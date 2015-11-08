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

import com.microrisc.simply.iqrf.dpa.v22x.types.IO_State;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code io_state} type values 
 * to {@code IO_State} enums. 
 * 
 * @author Michal Konopa
 */
public final class IO_StateConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IO_StateConvertor.class);
    
    /** Singleton. */
    private static final IO_StateConvertor instance = new IO_StateConvertor();
    
    private IO_StateConvertor() {}
    
    /**
     * @return {@code IO_StateConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public IO_StateConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 2;
    
    // postitions of fields
    static private final int STATE_POS = 0;
    static private final int DIRECTION_POS = 1;
    

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof IO_State)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] protoValue = new short[TYPE_SIZE];
        IO_State ioState = (IO_State)value;
        protoValue[STATE_POS] = (short)ioState.getStateValue();
        protoValue[DIRECTION_POS] = (short)ioState.getDirectionValue();
               
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        IO_State ioState = null;
        for (IO_State ioStateIt : IO_State.values()) {
            if ((protoValue[STATE_POS] == ioStateIt.getStateValue()) &&
                (protoValue[DIRECTION_POS] == ioStateIt.getDirectionValue())    
            ) {
                ioState = ioStateIt;
            }
        }
        
        if (ioState == null) {
            throw new ValueConversionException("Unknown IO state values: " + protoValue);
        }
        
        logger.debug("toObject - end: {}", ioState);
        return ioState;
    }
}
