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

import com.microrisc.simply.iqrf.dpa.v22x.types.AddressingInfo;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code addressing_info} type values 
 * to {@code AddressingInfo} objects. 
 * 
 * @author Michal Konopa
 */
public final class AddressingInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AddressingInfoConvertor.class);
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 2;
    
    static private final int BONDED_DEV_NUM_POS = 0;
    static private final int DID_POS = 1;
    
    private AddressingInfoConvertor() {
    }
    
    /** Singleton. */
    private static final AddressingInfoConvertor instance = new AddressingInfoConvertor();
    
    
    /**
     * @return {@code AddressingInfoConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public AddressingInfoConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
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
        
        int bondedDevicesNum = protoValue[BONDED_DEV_NUM_POS];
        int did = protoValue[DID_POS];
        AddressingInfo addrInfo = new AddressingInfo(bondedDevicesNum, did);
        
        logger.debug("toObject - end: {}", addrInfo);
        return addrInfo;
    }
}
