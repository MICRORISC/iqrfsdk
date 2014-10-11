
package com.microrisc.simply.iqrf.dpa.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code led_state} type values 
 * to {@code LED_State} enums. 
 * 
 * @author Michal Konopa
 */
public class LED_StateConvertor extends PrimitiveConvertor {
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    // postitions of fields
    static private final int STATE_POS = 0;
    
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(LED_StateConvertor.class);
    
    /** Singleton. */
    private static final LED_StateConvertor instance = new LED_StateConvertor();
    
    
    /**
     * @return LED_StateConvertor instance 
     */
    static public LED_StateConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }

    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @param value
     * @return
     * @throws ValueConversionException 
     */
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
