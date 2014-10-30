
package com.microrisc.simply.iqrf.dpa.v201.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v201.types.PeripheralInfo;
import com.microrisc.simply.typeconvertors.ArrayConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code peripheral_info[]} type 
 * values to {@code PeripheralInfo[]} objects.
 * 
 * @author Michal Konopa
 */
public final class ArrayPeripheralInfoConvertor extends ArrayConvertor {
    /** Logger. */
    private static final Logger logger = 
            LoggerFactory.getLogger(ArrayPeripheralInfoConvertor.class);
    
    private ArrayPeripheralInfoConvertor() {
        this.elemConvertor = PeripheralInfoConvertor.getInstance();
    }
    
    /** Singleton. */
    private static final ArrayPeripheralInfoConvertor instance = new ArrayPeripheralInfoConvertor();
    
    
    /**
     * @return {@code ArrayPeripheralInfoConvertor} instance 
     */
    static public ArrayPeripheralInfoConvertor getInstance() {
        return instance;
    }
    
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: iqValue={}", protoValue);
        
        int elemSize = elemConvertor.getGenericTypeSize();
        if ((protoValue.length) % elemSize != 0) {
            throw new ValueConversionException("Base element size doesn't divide "
                    + "argument length");
        }
        
        List<PeripheralInfo> retValues = new LinkedList<>();
        for (int byteId = 0; byteId < protoValue.length; byteId+= elemSize) {
            short[] elem = new short[elemSize];
            System.arraycopy(protoValue, byteId, elem, 0, elemSize);
            retValues.add((PeripheralInfo)elemConvertor.toObject(elem));
        }
        
        PeripheralInfo[] retValuesArr = retValues.toArray(new PeripheralInfo[0]); 
        
        logger.debug("toObject - end: {}", retValuesArr);
        return retValuesArr;
    }
}
