
package com.microrisc.simply.iqrf.dpa.v201.types;

import com.microrisc.simply.types.AbstractConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code sub_dpa_request} type values 
 * to {@code SubDPARequest} objects. 
 * 
 * @author Rostislav Spinar
 */
public final class SubDPARequestConvertor extends AbstractConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SubDPARequestConvertor.class);
    
    private SubDPARequestConvertor() {}
    
    /** Singleton. */
    private static final SubDPARequestConvertor instance = new SubDPARequestConvertor();
    
    
    /**
     * @return {@code SubDPARequestConvertor} instance 
     */
    static public SubDPARequestConvertor getInstance() {
        return instance;
    }
    
    // postitions of fields
    static private final int SUB_NADR_POS = 0;
    static private final int SUB_NADR_LENGTH = 2;
    
    static private final int SUB_PNUM_POS = 2;
    static private final int SUB_PCMD_POS = 3;
    
    static private final int SUB_HWPID_POS = 4;
    static private final int SUB_HWPID_LENGTH = 2;
    
    static private final int SUB_PDATA_POS = 6;


    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if ( !(value instanceof SubDPARequest) ) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        SubDPARequest subDpaRequest = (SubDPARequest) value;
        short[] protoValue = new short[SUB_PDATA_POS + subDpaRequest.getSubPData().length];
        
        protoValue[SUB_NADR_POS] = (short) (subDpaRequest.getSubNAdr()& 0xFF);
        protoValue[SUB_NADR_POS + 1] = (short)((subDpaRequest.getSubNAdr() & 0xFF00) >> 8);
        protoValue[SUB_PNUM_POS] = (short) (subDpaRequest.getSubPNum() & 0xFF);
        protoValue[SUB_PCMD_POS] = (short) (subDpaRequest.getSubPCmd() & 0xFF);
        protoValue[SUB_HWPID_POS] = (short) (subDpaRequest.getSubHwProfile() & 0xFF);
        protoValue[SUB_HWPID_POS + 1] = (short)((subDpaRequest.getSubHwProfile()& 0xFF00) >> 8);
        
        System.arraycopy(
                subDpaRequest.getSubPData(), 0, protoValue, SUB_PDATA_POS, 
                subDpaRequest.getSubPData().length
        );
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
