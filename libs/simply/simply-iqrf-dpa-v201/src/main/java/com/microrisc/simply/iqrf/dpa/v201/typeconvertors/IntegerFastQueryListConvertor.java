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

package com.microrisc.simply.iqrf.dpa.v201.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v201.types.IntegerFastQueryList;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion from IQRF DPA bit array 
 * to {@code IntegerFastQueryList} values. 
 * 
 * @author Michal Konopa
 */
public final class IntegerFastQueryListConvertor extends AbstractConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IntegerFastQueryListConvertor.class);
    
    /** Singleton. */
    private static final IntegerFastQueryListConvertor instance = new IntegerFastQueryListConvertor();
    
    private IntegerFastQueryListConvertor() {}
    
    /**
     * @return {@code IntegerFastQueryListConvertor} instance 
     */
    static public IntegerFastQueryListConvertor getInstance() {
        return instance;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        List<Integer> membersList = new LinkedList<>();
        for (int byteId = 0; byteId < protoValue.length; byteId++) {
            if (protoValue[byteId] == 0) {
                continue;
            }
            
            int bitComp = 1;
            for (int bitId = 0; bitId < 8; bitId++) {
                if ((protoValue[byteId] & bitComp) == bitComp) {
                    membersList.add(byteId * 8 + bitId);
                }
                bitComp *= 2;
            }
        }
        IntegerFastQueryList intQueryList = new IntegerFastQueryList(membersList);
        
        logger.debug("toObject - end: {}", intQueryList);
        return intQueryList;
    }
}
