/*
 * Copyright 2016 MICRORISC s.r.o.
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

import com.microrisc.simply.iqrf.dpa.v22x.types.HWP_ConfigurationByte;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code hwp_configurationByte} type
 * values to {@code HWP_ConfigurationByte} objects. 
 * 
 * @author Martin Strouhal
 */
public class HWP_ConfigurationByteConvertor extends PrimitiveConvertor{
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(HWP_ConfigurationByteConvertor.class);
    
    /** Singleton. */
    private static final HWP_ConfigurationByteConvertor instance = new HWP_ConfigurationByteConvertor();
    
    private HWP_ConfigurationByteConvertor() {}
    
    /**
     * @return {@code HWP_ConfigurationByteConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public HWP_ConfigurationByteConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 3;
    
    // postitions of fields
    static private final int ADDRESS_POS = 0;
    static private final int VALUE_POS = 1;
    static private final int MASK_POS = 2;

    

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if (!(value instanceof HWP_ConfigurationByte[])) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        HWP_ConfigurationByte[] configByte = (HWP_ConfigurationByte[])value;
        short[] protoValue = new short[TYPE_SIZE * configByte.length];
        
       for (int i = 0; i < configByte.length; i++) {
          protoValue[ADDRESS_POS] = (short) configByte[i].getAddress();
          protoValue[VALUE_POS] = (short) configByte[i].getValue();
          protoValue[MASK_POS] = (short) configByte[i].getMask();
       }
               
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

   @Override
   public Object toObject(short[] protoValue) throws ValueConversionException {
      throw new UnsupportedOperationException("Currently not supported.");
   }
}
