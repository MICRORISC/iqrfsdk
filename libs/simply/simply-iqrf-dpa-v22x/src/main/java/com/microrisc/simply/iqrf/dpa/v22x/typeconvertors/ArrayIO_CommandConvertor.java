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

import com.microrisc.simply.iqrf.dpa.v22x.types.IO_Command;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.ArrayConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion between array of bytes and array of
 * {@code IO_Command} objects.
 * 
 * @author Michal Konopa
 */
public final class ArrayIO_CommandConvertor extends ArrayConvertor {
    /** Logger. */
    private static final Logger logger = 
            LoggerFactory.getLogger(ArrayIO_CommandConvertor.class);
    
    private ArrayIO_CommandConvertor() {
        this.elemConvertor = IO_CommandConvertor.getInstance();
    }
    
    /** Singleton. */
    private static final ArrayIO_CommandConvertor instance = new ArrayIO_CommandConvertor();
    
    
    /**
     * @return {@code ArrayIO_CommandConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public ArrayIO_CommandConvertor getInstance() {
        return instance;
    }
    
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toIQValue - start: valueToConv={}", value);
        
        if (!(value instanceof IO_Command[])) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        IO_Command[] ioCommandArr = (IO_Command[]) value;
        int elemSize = elemConvertor.getGenericTypeSize();
        short[] finalIqValue = new short[elemSize * ioCommandArr.length];
        int totalCopied = 0;
        for (IO_Command ioSettings : ioCommandArr) {
            short[] iqValue = elemConvertor.toProtoValue(ioSettings);
            System.arraycopy(iqValue, 0, finalIqValue, totalCopied, elemSize);
            totalCopied += elemSize;
        }
        
        logger.debug("toIQValue - end: {}", finalIqValue);
        return finalIqValue;
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
