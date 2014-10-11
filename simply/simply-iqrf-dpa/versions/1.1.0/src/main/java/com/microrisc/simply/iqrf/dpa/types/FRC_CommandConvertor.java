
package com.microrisc.simply.iqrf.dpa.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between Java {@code FRC_Command} type 
 * and IQRF DPA protocol bytes.
 * 
 * @author Rostislav Spinar
 */
public class FRC_CommandConvertor extends PrimitiveConvertor {
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FRC_CommandConvertor.class);
    
    /** Singleton. */
    private static final FRC_CommandConvertor instance = new FRC_CommandConvertor();
    
    
    /**
     * @return FRC_ConnandConvertor instance 
     */
    static public FRC_CommandConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    private FRC_Command.CollectionType getFRCValueType(short sourceByte) 
            throws ValueConversionException {
        int frcBit = sourceByte & 0x80;
        
        for (FRC_Command.CollectionType frcValueType : FRC_Command.CollectionType.values()) {
            if (frcBit == frcValueType.getValue()) {
                return frcValueType;
            }
        }
        throw new ValueConversionException("Unknown FRC value type: " + frcBit);
    }
    
    private int getFRCCmd(short sourceByte) {
        int frcCmd = (sourceByte & 0x7F);
        return frcCmd;
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
        
        if ( !(value instanceof FRC_Command) ) {
            throw new ValueConversionException("Value to convert is not of FRC_Command type.");
        }
        
        FRC_Command frcCmd = (FRC_Command)value;
        
        short[] protoValue = new short[TYPE_SIZE];
        protoValue[0] = (short)frcCmd.getCommandValue();
               
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        FRC_Command.CollectionType frcValueType = getFRCValueType(protoValue[0]);
        FRC_Command frcCmd = new FRC_Command(frcValueType, getFRCCmd(protoValue[0]));
        
        logger.debug("toObject - end: {}", frcCmd);
        return frcCmd;
    }
}
