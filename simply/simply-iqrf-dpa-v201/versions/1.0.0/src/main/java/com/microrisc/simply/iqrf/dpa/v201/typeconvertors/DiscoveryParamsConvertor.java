
package com.microrisc.simply.iqrf.dpa.v201.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v201.types.DiscoveryParams;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion between array of bytes and 
 * {@code DiscoveryParams} objects.
 * 
 * @author Michal Konopa
 */
public final class DiscoveryParamsConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryParamsConvertor.class);
    
    private DiscoveryParamsConvertor() {}
    
    /** Singleton. */
    private static final DiscoveryParamsConvertor instance = new DiscoveryParamsConvertor();
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 2;
    
    // postitions of fields
    static private final int TX_POWER_POS = 0;
    static private final int ADDR_POS = 1;    
    
    
    /**
     * @return {@code DiscoveryParamsConvertor} instance 
     */
    static public DiscoveryParamsConvertor getInstance() {
        return instance;
    }
    
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof DiscoveryParams)) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] protoValue = new short[TYPE_SIZE];
        DiscoveryParams discoveryParams = (DiscoveryParams)value;
        protoValue[TX_POWER_POS] = (short)discoveryParams.getTxPower();
        protoValue[ADDR_POS] = (short)discoveryParams.getMaxAddr();
               
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        int txPower = protoValue[TX_POWER_POS];
        int addr = protoValue[ADDR_POS];
        DiscoveryParams discoveryParams = new DiscoveryParams(txPower, addr);
        
        logger.debug("toObject - end: {}", discoveryParams);
        return discoveryParams;
    }
}
