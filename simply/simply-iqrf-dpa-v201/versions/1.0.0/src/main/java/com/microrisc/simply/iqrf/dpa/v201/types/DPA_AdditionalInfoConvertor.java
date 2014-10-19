

package com.microrisc.simply.iqrf.dpa.v201.types;

import com.microrisc.simply.iqrf.dpa.v201.DPA_ResponseCode;
import com.microrisc.simply.iqrf.types.Uns16Convertor;
import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from DPA additional information to 
 * to {@code DPA_AdditionalInfo} objects. 
 * 
 * @author Michal Konopa
 */
public final class DPA_AdditionalInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_AdditionalInfoConvertor.class);
    
    private DPA_AdditionalInfoConvertor() {}
    
    /** Singleton. */
    private static final DPA_AdditionalInfoConvertor instance = new DPA_AdditionalInfoConvertor();
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 4;
    
    // postitions of fields
    static private final int HW_PROFILE_POS = 0;
    static private final int HW_PROFILE_LEN = 2;
    static private final int RESPONSE_CODE_POS = 2;
    static private final int DPA_VALUE_POS = 3;
    
    
    
    private DPA_ResponseCode getResponseCode(short[] protoValue) throws ValueConversionException {
        int respCodeInt = protoValue[RESPONSE_CODE_POS];
        for (DPA_ResponseCode respCode : DPA_ResponseCode.values()) {
            if (respCode.getCodeValue() == respCodeInt) {
                return respCode;
            }
        }
        throw new ValueConversionException("Unknown response code value: " + respCodeInt);
    }
    
    
    /**
     * @return DPA_AdditionalInfoConvertor instance 
     */
    static public DPA_AdditionalInfoConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }

    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }
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
        
        short[] hwProfileBytes = new short[ HW_PROFILE_LEN ];
        System.arraycopy(protoValue, HW_PROFILE_POS, hwProfileBytes, 0, HW_PROFILE_LEN);
        int hwProfile = (Integer)Uns16Convertor.getInstance().toObject( hwProfileBytes );
        
        DPA_ResponseCode responseCode = getResponseCode(protoValue);
        int dpaValue = protoValue[DPA_VALUE_POS];
        
        DPA_AdditionalInfo additionalInfo = new DPA_AdditionalInfo(hwProfile, 
                responseCode, dpaValue
        );
        
        logger.debug("toObject - end: {}", additionalInfo);
        return additionalInfo;
    }
}
