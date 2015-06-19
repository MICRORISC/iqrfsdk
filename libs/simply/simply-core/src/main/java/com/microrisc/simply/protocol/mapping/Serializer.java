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

package com.microrisc.simply.protocol.mapping;

import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates process of serialization Java values to protocol packets.
 * 
 * @author Michal Konopa
 */
public final class Serializer {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(Serializer.class);
    
    /**
     * Serializes specified value according to specified mapping and returns it.
     * @param valueMapping value mapping, according to which serialize the value 
     * @param value value to serialize
     * @return serialized value
     * @throws ValueConversionException if an error has occurred during conversion
     */
    static public short[] serialize(ValueToPacketMapping valueMapping, Object value) 
            throws ValueConversionException {
        logger.debug("serialize - start: valueMapping={}, value={}", 
                valueMapping, value);
        
        short[] serValue = valueMapping.getConvertor().toProtoValue(value); 
        
        logger.debug("serialize - end: {}", serValue);
        return serValue;
    }
}
