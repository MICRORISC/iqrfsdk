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

import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveryResult;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code discovery_result} type values 
 * to {@code DiscoveryResult} objects. 
 * 
 * @author Michal Konopa
 */
public final class DiscoveryResultConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryResultConvertor.class);
    
    private DiscoveryResultConvertor() {}
    
    /** Singleton. */
    private static final DiscoveryResultConvertor instance = new DiscoveryResultConvertor();
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 1;
    
    // postitions of fields
    static private final int DISC_DEV_NUM_POS = 0;
    
    
    /**
     * @return {@code DiscoveryResultConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public DiscoveryResultConvertor getInstance() {
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
        
        int discDevicesNum = protoValue[DISC_DEV_NUM_POS];
        DiscoveryResult discResult = new DiscoveryResult(discDevicesNum);
        
        logger.debug("toObject - end: {}", discResult);
        return discResult;
    }
}
