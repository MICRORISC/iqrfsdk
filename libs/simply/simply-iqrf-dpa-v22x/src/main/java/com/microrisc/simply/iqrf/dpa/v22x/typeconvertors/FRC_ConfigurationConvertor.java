/*
 * Copyright 2015 MICRORISC s.r.o..
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

import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Configuration;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting {@link FRC_Configuratin} to protocol packets.
 *
 * @author Martin Strouhal
 */
public class FRC_ConfigurationConvertor extends AbstractConvertor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FRC_ConfigurationConvertor.class);

    private FRC_ConfigurationConvertor() {
    }

    /** Singleton. */
    private static final FRC_ConfigurationConvertor instance = new FRC_ConfigurationConvertor();

    /**
     * @return {@code FRC_ConfigurationConvertor} instance
     */
    @ConvertorFactoryMethod
    public static FRC_ConfigurationConvertor getInstance(){
        return instance;
    }
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        if ( !(value instanceof FRC_Configuration) ) {
            throw new ValueConversionException("Value to convert is not of FRC_Command type.");
        }
        FRC_Configuration config = (FRC_Configuration)value;
        
        short protoVal = (short)config.getResponseTime().getIdValue();
        
        short[] protoValue = new short[]{protoVal};
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
       if(protoValue.length != 1){
          throw new IllegalArgumentException("The length of converted data must be 1!");
       } 
       short protoVal = protoValue[0];
       
       FRC_Configuration.FRC_RESPONSE_TIME[] values = FRC_Configuration.FRC_RESPONSE_TIME.values();
       for (FRC_Configuration.FRC_RESPONSE_TIME responseTimeValue : values) {
          if(responseTimeValue.getIdValue() == protoVal){
             return new FRC_Configuration(responseTimeValue);
          }
       }
       
       throw new IllegalArgumentException("For specified value doesn't exist response time!");
    }
}
