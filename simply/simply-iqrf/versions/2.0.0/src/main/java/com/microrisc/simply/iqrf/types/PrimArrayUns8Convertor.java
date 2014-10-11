
package com.microrisc.simply.iqrf.types;

import com.microrisc.simply.types.ArrayConvertor;
import com.microrisc.simply.types.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from and to IQRF arrays of uns8 type. 
 * Peer Java type: short[].<br>
 * 
 * @author Michal Konopa
 */
public final class PrimArrayUns8Convertor extends ArrayConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PrimArrayUns8Convertor.class);
    
    private PrimArrayUns8Convertor() {
        this.elemConvertor = Uns8Convertor.getInstance();
    }
    
    /** Singleton. */
    private static final PrimArrayUns8Convertor instance = new PrimArrayUns8Convertor();
    
    
    /**
     * @return PrimArrayUns8Convertor instance 
     */
    static public PrimArrayUns8Convertor getInstance() {
        return instance;
    }
    
    
    /**
     * The value to convert must have Short[] type, otherwise Exception is
     * thrown.
     * @param valueToConv value to convert
     * @return application protocol representation of converted value
     * @throws ValueConversionException if the converted value hasn't Short[] type
     */
    @Override
    public short[] toProtoValue(Object valueToConv) throws ValueConversionException {
        logger.debug("toIQValue - start: valueToConv={}", valueToConv);
        
        Short[] shortArr = null;
        if ( valueToConv instanceof short[] ) {
            shortArr = ArrayUtils.toObject( (short[]) valueToConv ); 
        } else if ( valueToConv instanceof Short[] ) {
            shortArr = (Short[]) valueToConv;
        } else {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
       
        int elemSize = elemConvertor.getGenericTypeSize();
        short[] finalIqValue = new short[elemSize * shortArr.length];
        int totalCopied = 0;
        for (short shortValue : shortArr) {
            short[] iqValue = elemConvertor.toProtoValue(shortValue);
            System.arraycopy(iqValue, 0, finalIqValue, totalCopied, elemSize);
            totalCopied += elemSize;
        }
        
        logger.debug("toIQValue - end: {}", finalIqValue);
        return finalIqValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        int elemSize = elemConvertor.getGenericTypeSize();
        if ( (protoValue.length) % elemSize != 0 ) {
            throw new ValueConversionException(
                "Base element size doesn't divide argument length"
            );
        }
        
        List<Short> retValues = new LinkedList<Short>();
        for ( int byteId = 0; byteId < protoValue.length; byteId += elemSize ) {
            short[] elem = new short[elemSize];
            System.arraycopy(protoValue, byteId, elem, 0, elemSize);
            retValues.add((Short)elemConvertor.toObject(elem));
        }
        
        Short[] retValuesArr = retValues.toArray( new Short[0] ); 
        
        logger.debug("toObject - end: {}", ArrayUtils.toPrimitive(retValuesArr));
        return retValuesArr;
    } 
}
