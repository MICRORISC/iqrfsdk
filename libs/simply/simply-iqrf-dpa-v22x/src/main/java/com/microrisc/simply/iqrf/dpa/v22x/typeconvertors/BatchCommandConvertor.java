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

import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Request;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting batch commands to protocol packets.
 * 
 * @author Michal Konopa
 */
public final class BatchCommandConvertor extends AbstractConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BatchCommandConvertor.class);
    
    private BatchCommandConvertor() {
    }
    
    /** Singleton. */
    private static final BatchCommandConvertor instance = new BatchCommandConvertor();
    
     /**
     * @return {@code BatchCommandConvertor} instance
     */
    @ConvertorFactoryMethod
    static public BatchCommandConvertor getInstance() {
        return instance;
    }
    
    /**
     * Serializes specified serialized requests into one final sequence of bytes. 
     * @param serRequests source request to put into final sequence
     * @return serialized requests
     */
    private short[] serializeToArray(List<short[]> serRequests) {
        final byte BATCH_LAST_BYTE = 0;

        // last byte of all the sequence will be 0
        int finalArrayLen = 1;
        
        for ( short[] serRequest : serRequests ) {
            finalArrayLen += serRequest.length;
        }
        
        short[] finalArray = new short[finalArrayLen];
        int actPos = 0;
        for ( short[] serRequest : serRequests ) {
            System.arraycopy(serRequest, 0, finalArray, actPos, serRequest.length);
            actPos += serRequest.length;
        }
      
        finalArray[finalArrayLen-1] = BATCH_LAST_BYTE;
        return finalArray;
    }
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if ( !(value instanceof DPA_Request[]) ) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        DPA_Request[] dpaRequests = (DPA_Request[]) value;
        
        List<short[]> serRequests = new LinkedList<>();
        for ( DPA_Request dpaRequest : dpaRequests ) {
            short[] serRequest = null;
            try {
                serRequest = DPA_RequestConvertor.getInstance().toProtoValue(dpaRequest);
            } catch (ValueConversionException ex) {
                throw new ValueConversionException("Value to convert has not proper type.", ex);
            }
            serRequests.add(serRequest);
        }
        
        short[] protoValue = serializeToArray(serRequests);
        
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
