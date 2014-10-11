
package com.microrisc.simply.iqrf.dpa.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion between array of bytes and 
 * {@code IO_DirectionSettings} objects.
 * 
 * @author Michal Konopa
 */
public class IO_DirectionSettingsConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IO_DirectionSettingsConvertor.class);
    
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 3;
    
    // postitions of fields
    static private final int PORT_POS = 0;
    static private final int MASK_POS = 1;
    static private final int VALUE_POS = 2;
    
    
    /** Singleton. */
    private static final IO_DirectionSettingsConvertor instance = new IO_DirectionSettingsConvertor();
    
    
    /**
     * @return IO_DirectionSettingsConvertor instance 
     */
    static public IO_DirectionSettingsConvertor getInstance() {
        return instance;
    }
    
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }

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

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported");
    }
}
