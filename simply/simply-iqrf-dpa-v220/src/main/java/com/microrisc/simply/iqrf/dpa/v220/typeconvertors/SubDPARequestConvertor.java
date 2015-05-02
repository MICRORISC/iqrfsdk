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

package com.microrisc.simply.iqrf.dpa.v220.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v220.types.SubDPARequest;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
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
        
        protoValue[SUB_NADR_POS] = (short) (subDpaRequest.getSubNADR() & 0xFF);
        protoValue[SUB_NADR_POS + 1] = (short)((subDpaRequest.getSubNADR() & 0xFF00) >> 8);
        protoValue[SUB_PNUM_POS] = (short) (subDpaRequest.getSubPNUM() & 0xFF);
        protoValue[SUB_PCMD_POS] = (short) (subDpaRequest.getSubPCMD() & 0xFF);
        protoValue[SUB_HWPID_POS] = (short) (subDpaRequest.getSubHWPID() & 0xFF);
        protoValue[SUB_HWPID_POS + 1] = (short)((subDpaRequest.getSubHWPID()& 0xFF00) >> 8);
        
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
