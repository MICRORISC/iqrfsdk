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

import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNode;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code bonded_device} type values 
 * to {@code BondedNode} objects. 
 * 
 * @author Michal Konopa
 */
public final class BondedDeviceConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BondedDeviceConvertor.class);
    
    private BondedDeviceConvertor() {
    }
    
    /** Singleton. */
    private static final BondedDeviceConvertor instance = new BondedDeviceConvertor();
    
    
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 2;
    
    // postitions of fields
    static private final int BONDED_ADR_POS = 0;
    static private final int BONDED_DEV_NUM_POS = 1;
    
    
    /**
     * @return {@code BondedDeviceConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public BondedDeviceConvertor getInstance() {
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
        
        short bondedAdress = protoValue[BONDED_ADR_POS];
        short bondedDevicesNum = protoValue[BONDED_DEV_NUM_POS];
        BondedNode bondedDevice = new BondedNode(bondedAdress, bondedDevicesNum);
        
        logger.debug("toObject - end: {}", bondedDevice);
        return bondedDevice;
    }
}
