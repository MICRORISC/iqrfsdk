/* 
 * Copyright 2014-2015 MICRORISC s.r.o.
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

import com.microrisc.simply.iqrf.dpa.v22x.types.IntegerFastQueryList;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion from IQRF DPA bit array to
 * {@code IntegerFastQueryList} values.
 * <p>
 * @author Michal Konopa
 * @athor Martin Strouhal
 */
// October 2015 - implemented toProtoValue
public final class IntegerFastQueryListConvertor extends AbstractConvertor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IntegerFastQueryListConvertor.class);

    /** Singleton. */
    private static final IntegerFastQueryListConvertor instance = new IntegerFastQueryListConvertor();

    private IntegerFastQueryListConvertor() {
    }

    /**
     * @return {@code IntegerFastQueryListConvertor} instance
     */
    @ConvertorFactoryMethod
    static public IntegerFastQueryListConvertor getInstance() {
        return instance;
    }

    private IntegerFastQueryList checkIntegerFastQueryList(Object list) {
        if (!(list instanceof IntegerFastQueryList)) {
            throw new IllegalArgumentException("Object to convert must be type of IntegerFastQueryList.");
        }
        if (list == null) {
            throw new IllegalArgumentException("IntegerFastQueryList cannot be null.");
        }
        return (IntegerFastQueryList) list;
    }

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: protoValue={}", value);

        IntegerFastQueryList intQueryList = checkIntegerFastQueryList(value);
        List<Integer> rawList = intQueryList.getList();
        // if size of list is 0, max Id is 0, otherwise is used first value from list
        int maxPeripheralId = rawList.size() < 1 ? 0 : rawList.get(0);
        // finds max value
        for (Integer actualValue : rawList) {
            if (actualValue > maxPeripheralId) {
                maxPeripheralId = actualValue;
            }
        }
        maxPeripheralId++;
        int size = maxPeripheralId / 8 + (maxPeripheralId % 8 > 0 ? 1 : 0);
        
        short[] protoValue = new short[size];
        for (Integer peripheral : rawList) {
            // for each peripheral increase one bit on 
            short byteIndex, bitIndex, protoBinValue = 1;
            byteIndex = (short) (peripheral / 8);
            bitIndex = (short) (peripheral % 8);
            protoBinValue <<= bitIndex;
            protoValue[byteIndex] += protoBinValue;
        }

        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);

        List<Integer> membersList = new LinkedList<Integer>();
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
