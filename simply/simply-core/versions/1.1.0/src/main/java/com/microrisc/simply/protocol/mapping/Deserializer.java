
package com.microrisc.simply.protocol.mapping;

import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates process of deserialization of some packet fragment into Java value.
 * 
 * @author Michal Konopa
 */
public final class Deserializer {
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);
    
    /**
     * Deserialize and returns Java value.
     * @param packetToValueMapping parameteters of deserialization
     * @param protoMsg source packet to deserialize
     * @return deserialized Java value.
     */
    static public Object deserialize(PacketToValueMapping packetToValueMapping, 
            short[] protoMsg
    ) throws ValueConversionException {
        logger.debug("deserialize - start: packetToValueMapping={}, protoMsg={}", 
                packetToValueMapping, protoMsg);
        
        short[] valueToConv = null;
        if ( packetToValueMapping.isUpToEnd() ) {
            int valueLength = protoMsg.length - packetToValueMapping.getStartingPosition();
            valueToConv = new short[valueLength];
            System.arraycopy(protoMsg, packetToValueMapping.getStartingPosition(), 
                valueToConv, 0, valueLength);
        } else {
            valueToConv = new short[packetToValueMapping.getLength()];
            System.arraycopy(protoMsg, packetToValueMapping.getStartingPosition(), 
                valueToConv, 0, packetToValueMapping.getLength());
        }
        
        Object result = packetToValueMapping.getConvertor().toObject(valueToConv);
        
        logger.debug("deserialize - end: {}", result);
        return result;
    }
}
