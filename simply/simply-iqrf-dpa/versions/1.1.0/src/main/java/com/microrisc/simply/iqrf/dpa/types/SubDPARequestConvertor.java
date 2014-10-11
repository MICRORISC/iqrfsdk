
package com.microrisc.simply.iqrf.dpa.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code sub_dpa_request} type values 
 * to {@code SubDPARequest} objects. 
 * 
 * @author Rostislav Spinar
 */
public class SubDPARequestConvertor extends PrimitiveConvertor {
    /** Size of returned response. */
    static public final int TYPE_SIZE = 4;
    
    // postitions of fields
    static private final int SUB_NADR_POS = 0;
    static private final int SUB_NADR_LENGTH = 2;
    
    static private final int SUB_PNUM_POS = 2;
    static private final int SUB_PCMD_POS = 3;
    
    static private final int SUB_PDATA_POS = 4;

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SubDPARequestConvertor.class);
    
    /** Singleton. */
    private static final SubDPARequestConvertor instance = new SubDPARequestConvertor();
    
    
    /**
     * @return SubDPARequestConvertor instance 
     */
    static public SubDPARequestConvertor getInstance() {
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

        //2B NADR, 1B PNUM, 1B PCMD
        final int HEADER = 4;
        final int SUB_PDATA_LENGTH = protoValue.length - HEADER;
        
        short[] subNAdrSA = new short[SUB_NADR_LENGTH];
        System.arraycopy(protoValue, SUB_NADR_POS, subNAdrSA, 0, SUB_NADR_LENGTH);
        int subNAdr = subNAdrSA[0] + subNAdrSA[1]; 
        
        int subPNum = protoValue[SUB_PNUM_POS];
        int subPCmd = protoValue[SUB_PCMD_POS];
        
        short[] subPData = new short[protoValue.length - HEADER];
        System.arraycopy(protoValue, SUB_PDATA_POS, subPData, 0, SUB_PDATA_LENGTH);
        
        SubDPARequest subDPARequest = new SubDPARequest(subNAdr, subPNum, subPCmd, subPData); 
                
        logger.debug("toObject - end: {}", subDPARequest);
        return subDPARequest;
    }
}
