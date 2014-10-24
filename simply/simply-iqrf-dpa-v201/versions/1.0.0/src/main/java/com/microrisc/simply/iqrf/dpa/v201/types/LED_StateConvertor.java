
package com.microrisc.simply.iqrf.dpa.v201.types;

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
public final class LED_StateConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(LED_StateConvertor.class);
    
    private LED_StateConvertor() {}
    
    /** Singleton. */
    private static final LED_StateConvertor instance = new LED_StateConvertor();
    
    /**
     * @return {@code LED_StateConvertor} instance 
     */
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
