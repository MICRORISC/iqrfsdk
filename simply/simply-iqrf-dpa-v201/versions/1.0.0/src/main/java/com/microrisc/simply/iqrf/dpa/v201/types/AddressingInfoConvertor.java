
package com.microrisc.simply.iqrf.dpa.v201.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code addressing_info} type values 
 * to {@code AddressingInfo} objects. 
 * 
 * @author Michal Konopa
 */
public final class AddressingInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AddressingInfoConvertor.class);
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 2;
    
    // postitions of fields
    static private final int BONDED_DEV_NUM_POS = 0;
    
    private AddressingInfoConvertor() {
    }
    
    /** Singleton. */
    private static final AddressingInfoConvertor instance = new AddressingInfoConvertor();
    
    
    /**
     * @return AddressingInfoConvertor instance 
     */
    static public AddressingInfoConvertor getInstance() {
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        int bondedDevicesNum = protoValue[BONDED_DEV_NUM_POS];
        AddressingInfo addrInfo = new AddressingInfo(bondedDevicesNum);
        
        logger.debug("toObject - end: {}", addrInfo);
        return addrInfo;
    }
}
