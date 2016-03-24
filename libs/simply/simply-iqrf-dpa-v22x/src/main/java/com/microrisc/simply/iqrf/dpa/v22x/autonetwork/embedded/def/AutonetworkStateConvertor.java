/*
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def;

import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides services for converting data between {@link AutonetworkState} and
 * IQRF proto value.
 * 
 * @author Martin Strouhal
 */
public class AutonetworkStateConvertor extends AbstractConvertor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static AutonetworkStateConvertor instance = new AutonetworkStateConvertor();
    
    private AutonetworkStateConvertor(){}

    @ConvertorFactoryMethod
    public static AutonetworkStateConvertor getInstance(){
        return instance;
    }

    /** Minimal size of this type. */
    static public final int MINIMAL_TYPE_SIZE = 1;
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);

        if (protoValue.length < MINIMAL_TYPE_SIZE ) {
            throw new ValueConversionException(
                "Argument length doesn't match with type size"
            );
        }
        
        // creating state with properly type
        AutonetworkStateType type = AutonetworkStateType.getState(protoValue[0]);
        AutonetworkState state = new AutonetworkState(type);
        
        // adding additional parameters
        if(protoValue.length > MINIMAL_TYPE_SIZE){
            for (int i = 1; i < protoValue.length; i++) {
               state.addAdditionalData(protoValue[i]);
               logger.debug(" toObject - addedAdditionalData ({})", protoValue[i]);
            }
        }
        
        logger.debug("toObject - end: {}", state);
        return state;
    }
}