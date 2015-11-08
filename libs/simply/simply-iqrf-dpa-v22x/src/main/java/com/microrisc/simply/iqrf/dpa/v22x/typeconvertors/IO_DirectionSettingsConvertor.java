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

import com.microrisc.simply.iqrf.dpa.v22x.types.IO_DirectionSettings;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion between array of bytes and 
 * {@code IO_DirectionSettings} objects.
 * 
 * @author Michal Konopa
 */
public final class IO_DirectionSettingsConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IO_DirectionSettingsConvertor.class);
    
    /** Singleton. */
    private static final IO_DirectionSettingsConvertor instance = new IO_DirectionSettingsConvertor();
    
    private IO_DirectionSettingsConvertor() {}
    
    /**
     * @return {@code IO_DirectionSettingsConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public IO_DirectionSettingsConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 3;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int PORT_POS = 0;
    static private final int MASK_POS = 1;
    static private final int VALUE_POS = 2;
    

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof IO_DirectionSettings)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] protoValue = new short[TYPE_SIZE];
        IO_DirectionSettings ioSettings = (IO_DirectionSettings)value;
        
        protoValue[PORT_POS] = (short)ioSettings.getPort();
        protoValue[MASK_POS] = (short)ioSettings.getMask();
        protoValue[VALUE_POS] = (short)ioSettings.getValue();
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported");
    }
}
