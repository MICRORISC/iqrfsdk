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

package com.microrisc.simply.iqrf.typeconvertors;

import com.microrisc.simply.typeconvertors.ArrayConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from and to IQRF arrays of uns8 type. 
 * Peer Java type: {@code Short[]}.<br>
 * 
 * @author Michal Konopa
 */
public final class ArrayUns8Convertor extends ArrayConvertor{
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ArrayUns8Convertor.class);
    
    private ArrayUns8Convertor() {
        this.elemConvertor = Uns8Convertor.getInstance();
    }
    
    /** Singleton. */
    private static final ArrayUns8Convertor instance = new ArrayUns8Convertor();
    
    
    /**
     * @return {@code ArrayUns8Convertor} instance 
     */
    static public ArrayUns8Convertor getInstance() {
        return instance;
    }
    
    
    /**
     * The value to convert must have the {@code Short[]} type, otherwise Exception is
     * thrown.
     * @param valueToConv value to convert
     * @return application protocol representation of converted value
     * @throws ValueConversionException if the converted value doesn't have 
     *         the {@code Short[]} type
     */
    @Override
    public short[] toProtoValue(Object valueToConv) throws ValueConversionException {
        logger.debug("toProtoValue - start: valueToConv={}", valueToConv);
        
        Short[] shortArr = null;
        if ( valueToConv instanceof short[] ) {
            shortArr = ArrayUtils.toObject( (short[]) valueToConv ); 
        } else if ( valueToConv instanceof Short[] ) {
            shortArr = (Short[]) valueToConv;
        } else {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
       
        int elemSize = elemConvertor.getGenericTypeSize();
        short[] finalIqValue = new short[elemSize * shortArr.length];
        int totalCopied = 0;
        for (short shortValue : shortArr) {
            short[] iqValue = elemConvertor.toProtoValue(shortValue);
            System.arraycopy(iqValue, 0, finalIqValue, totalCopied, elemSize);
            totalCopied += elemSize;
        }
        
        logger.debug("toProtoValue - end: {}", finalIqValue);
        return finalIqValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        int elemSize = elemConvertor.getGenericTypeSize();
        if ((protoValue.length) % elemSize != 0) {
            throw new ValueConversionException("Base element size doesn't divide "
                    + "argument length");
        }
        
        List<Short> retValues = new LinkedList<>();
        for (int byteId = 0; byteId < protoValue.length; byteId+= elemSize) {
            short[] elem = new short[elemSize];
            System.arraycopy(protoValue, byteId, elem, 0, elemSize);
            retValues.add((Short)elemConvertor.toObject(elem));
        }
        
        Short[] retValuesArr = retValues.toArray(new Short[0]); 
        
        logger.debug("toObject - end: {}", retValuesArr);
        return retValuesArr;
    } 
}
