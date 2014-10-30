
package com.microrisc.simply.iqrf.dpa.v210.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v210.types.BondedNode;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code bonded_device} type values 
 * to {@code BondedNode} objects. 
 * 
 * @author Michal Konopa
 */
public final class BondedDeviceConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BondedDeviceConvertor.class);
    
    private BondedDeviceConvertor() {
    }
    
    /** Singleton. */
    private static final BondedDeviceConvertor instance = new BondedDeviceConvertor();
    
    
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 2;
    
    // postitions of fields
    static private final int BONDED_ADR_POS = 0;
    static private final int BONDED_DEV_NUM_POS = 1;
    
    
    /**
     * @return {@code BondedDeviceConvertor} instance 
     */
    static public BondedDeviceConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
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
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        short bondedAdress = protoValue[BONDED_ADR_POS];
        short bondedDevicesNum = protoValue[BONDED_DEV_NUM_POS];
        BondedNode bondedDevice = new BondedNode(bondedAdress, bondedDevicesNum);
        
        logger.debug("toObject - end: {}", bondedDevice);
        return bondedDevice;
    }
}
