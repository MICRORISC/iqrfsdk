/*
 * Copyright 2014 MICRORISC s.r.o..
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

import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Confirmation;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Parameter;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code DPA Confirmation} type values 
 * to {@link com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Confirmation} objects.
 * 
 * @author Michal Konopa
 */
public final class DPA_ConfirmationConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_ConfirmationConvertor.class);
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 5;
    
    static private final int DPA_VALUE_POS = 1;
    static private final int HOPS_POS = 2;
    static private final int TIMESLOT_LENGTH_POS = 3;
    static private final int HOPS_RESPONSE_POS = 4;
    
    
    private DPA_ConfirmationConvertor() {
    }
    
    /** Singleton. */
    private static final DPA_ConfirmationConvertor instance = new DPA_ConfirmationConvertor();
    
    
    /**
     * @return {@code DPA_ConfirmationConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public DPA_ConfirmationConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // returns DPA value
    private DPA_Parameter getDpaValue(short[] protoValue) throws ValueConversionException 
    {
        short[] dpaValueSrc = new short[DPA_ParameterConvertor.TYPE_SIZE];
        System.arraycopy(protoValue, DPA_VALUE_POS, dpaValueSrc, 0, DPA_ParameterConvertor.TYPE_SIZE);
        return (DPA_Parameter)DPA_ParameterConvertor.getInstance().toObject(dpaValueSrc);
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
        
        DPA_Parameter dpaValue = getDpaValue(protoValue);
        int hops = protoValue[HOPS_POS];
        int timeslotLength = protoValue[TIMESLOT_LENGTH_POS];
        int hopsResponse = protoValue[HOPS_RESPONSE_POS];
        
        DPA_Confirmation dpaConfirmation = new DPA_Confirmation(
                dpaValue, hops, timeslotLength, hopsResponse
        );
        
        logger.debug("toObject - end: {}", dpaConfirmation);
        return dpaConfirmation;
    }
}
