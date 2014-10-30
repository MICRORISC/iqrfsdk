
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
