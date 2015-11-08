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

import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Data;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from FRC data values to {@code FRC_Data} 
 * objects. 
 * 
 * @author Michal Konopa
 */
public final class FRC_DataConvertor extends AbstractConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FRC_DataConvertor.class);
    
    private FRC_DataConvertor() {}
    
    /** Singleton. */
    private static final FRC_DataConvertor instance = new FRC_DataConvertor();

    // postitions of fields
    static private final int STATUS_POS = 0;
    static private final int DATA_POS = 1;
            
    
    /**
     * @return {@code FRC_DataConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public FRC_DataConvertor getInstance() {
        return instance;
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
        
        int status = protoValue[STATUS_POS];
        
        int dataLen = protoValue.length - DATA_POS;
        if ( dataLen < 0 ) {
            dataLen = 0;
        }
        short[] data = new short[ dataLen ];
        System.arraycopy(protoValue, DATA_POS, data, 0, dataLen);
        
        FRC_Data frcData = new FRC_Data(status, data);
      
        logger.debug("toObject - end: {}", frcData);
        return frcData;
    }
}
