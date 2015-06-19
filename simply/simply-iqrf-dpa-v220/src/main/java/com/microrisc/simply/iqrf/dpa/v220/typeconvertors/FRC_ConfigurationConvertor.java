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
package com.microrisc.simply.iqrf.dpa.v220.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v220.types.FRC_Configuration;
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
        
        String stringTime;
        switch(config.getResponseTime()){
            case TIME_40_MS:
                stringTime = "000";
                break;
            case TIME_320_MS:
                stringTime = "001";
                break;
            case TIME_640_MS:
                stringTime = "010";
                break;
            case TIME_1280_MS:
                stringTime = "011";
                break;
            case TIME_2560_MS:
                stringTime = "100";
                break;
            case TIME_5120_MS:
                stringTime = "101";
                break;
            case TIME_10240_MS:
                stringTime = "110";
                break;
            case TIME_20480_MS:
                stringTime = "111";
                break;
            default:
                throw new ValueConversionException("FRC response time is incorrect to conversion.");
        }
        stringTime = "0" + stringTime + "0000";
        
        short[] protoValue = new short[]{Short.parseShort(stringTime, 2)};
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
