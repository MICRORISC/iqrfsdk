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

import com.microrisc.simply.iqrf.dpa.v22x.types.ExtPerCharacteristic;
import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralInfo;
import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralType;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code peripheral_info} type values 
 * to {@code PeripheralInfo} objects. 
 * 
 * @author Michal Konopa
 */
public final class PeripheralInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PeripheralInfoConvertor.class);
    
    private PeripheralInfoConvertor() {}
    
    /** Singleton. */
    private static final PeripheralInfoConvertor instance = new PeripheralInfoConvertor();
    
    
    /**
     * @return {@code PeripheralInfoConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public PeripheralInfoConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 4;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int PER_TYPE_POS = 1;
    static private final int EXT_PER_TYPE_POS = 0;
    static private final int PARAM1_POS = 2;
    static private final int PARAM2_POS = 3;
    

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
        
        PeripheralType perType = null;
        for ( PeripheralType perTypeIter : PeripheralType.values() ) {
            if ( protoValue[PER_TYPE_POS] == perTypeIter.getTypeValue() ) {
                perType = perTypeIter;
            }
        }
        
        if ( perType == null ) {
            throw new ValueConversionException(
                    "Unknown value of peripheral type: " + protoValue[PER_TYPE_POS]
            );
        }
        
        ExtPerCharacteristic extPerChar = null;
        for ( ExtPerCharacteristic extCharIter : ExtPerCharacteristic.values() ) {
            if ( protoValue[EXT_PER_TYPE_POS] == extCharIter.getCharacteristicValue() ) {
                extPerChar = extCharIter;
            }
        }
        
        if ( extPerChar == null ) {
            throw new ValueConversionException(
                    "Unknown value of extended peripheral type: " + protoValue[EXT_PER_TYPE_POS]
            );
        }
        
        PeripheralInfo perInfo = new PeripheralInfo(
            perType, extPerChar, protoValue[PARAM1_POS], protoValue[PARAM2_POS]
        );
        
        logger.debug("toObject - end: {}", perInfo);
        return perInfo;
    }
}
