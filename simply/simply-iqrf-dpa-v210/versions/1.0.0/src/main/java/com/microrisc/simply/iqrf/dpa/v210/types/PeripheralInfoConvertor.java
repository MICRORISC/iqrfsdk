
package com.microrisc.simply.iqrf.dpa.v210.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code peripheral_info} type values 
 * to {@code PeripheralInfo} objects. 
 * 
 * @author Michal Konopa
 */
public final class PeripheralInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PeripheralInfoConvertor.class);
    
    private PeripheralInfoConvertor() {}
    
    /** Singleton. */
    private static final PeripheralInfoConvertor instance = new PeripheralInfoConvertor();
    
    
    /**
     * @return PeripheralInfoConvertor instance 
     */
    static public PeripheralInfoConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 4;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int PER_TYPE_POS = 1;
    static private final int EXT_PER_TYPE_POS = 0;
    static private final int PARAM1_POS = 2;
    static private final int PARAM2_POS = 3;
    
    
    

    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @param value
     * @return
     * @throws ValueConversionException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        PeripheralType perType = null;
        for ( PeripheralType perTypeIter : PeripheralType.values() ) {
            if ( protoValue[PER_TYPE_POS] == perTypeIter.getTypeValue() ) {
                perType = perTypeIter;
            }
        }
        
        if ( perType == null ) {
            throw new ValueConversionException(
                    "Unknown value of peripheral type: " + protoValue[PER_TYPE_POS]
            );
        }
        
        ExtPerCharacteristic extPerChar = null;
        for ( ExtPerCharacteristic extCharIter : ExtPerCharacteristic.values() ) {
            if ( protoValue[EXT_PER_TYPE_POS] == extCharIter.getCharacteristicValue() ) {
                extPerChar = extCharIter;
            }
        }
        
        if ( extPerChar == null ) {
            throw new ValueConversionException(
                    "Unknown value of extended peripheral type: " + protoValue[EXT_PER_TYPE_POS]
            );
        }
        
        PeripheralInfo perInfo = new PeripheralInfo(
            perType, extPerChar, protoValue[PARAM1_POS], protoValue[PARAM2_POS]
        );
        
        logger.debug("toObject - end: {}", perInfo);
        return perInfo;
    }
}
