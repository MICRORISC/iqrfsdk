/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microrisc.simply.iqrf.dpa.v22x.typeconvertors;

import com.microrisc.simply.iqrf.dpa.DPA_ResponseCode;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_AdditionalInfo;
import com.microrisc.simply.iqrf.typeconvertors.Uns16Convertor;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
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
     * @return {@code DPA_AdditionalInfoConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public DPA_AdditionalInfoConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }

    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }
     * @throws UnsupportedOperationException 
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