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

import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Command;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between Java {@code FRC_Command} type 
 * and IQRF DPA protocol bytes.
 * 
 * @author Rostislav Spinar
 */
public final class FRC_CommandConvertor extends AbstractConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FRC_CommandConvertor.class);
    
    private FRC_CommandConvertor() {}
    
    /** Singleton. */
    private static final FRC_CommandConvertor instance = new FRC_CommandConvertor();
    
    
    /**
     * @return {@code FRC_CommandConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public FRC_CommandConvertor getInstance() {
        return instance;
    }
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if ( !(value instanceof FRC_Command) ) {
            throw new ValueConversionException("Value to convert is not of FRC_Command type.");
        }
        
        FRC_Command frcCmd = (FRC_Command)value;
        
        short[] protoValue = new short[1 + frcCmd.getUserData().length];
        protoValue[0] = (short)frcCmd.getId();
        System.arraycopy(frcCmd.getUserData(), 0, protoValue, 1, frcCmd.getUserData().length);
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported.");
    }         
}
