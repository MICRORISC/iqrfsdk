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

import com.microrisc.simply.iqrf.dpa.v22x.types.PWM_Parameters;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion between array of bytes and 
 * {@code PWM_Parameters} objects.
 * 
 * @author Michal Konopa
 */
public final class PWM_ParametersConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PWM_ParametersConvertor.class);
    
    private PWM_ParametersConvertor() {}
    
    /** Singleton. */
    private static final PWM_ParametersConvertor instance = new PWM_ParametersConvertor();
    
    
    /**
     * @return {@code PWM_ParametersConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public PWM_ParametersConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 3;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int PRESCALER_POS = 0;
    static private final int PERIOD_POS = 1;
    static private final int DUTY_CYCLE_POS = 2;
    
    
    /**
     * @return serialized prescaler value
     */
    private short getSerializedPrescaler(PWM_Parameters.Prescaler prescaler, int dutyCycle) {
        short serPrescaler = (short)prescaler.getPrescalerValue();
        serPrescaler |= (dutyCycle & 0x01) << 4;
        serPrescaler |= (dutyCycle & 0x02) << 4;
        return serPrescaler;
    }
    
    /**
     * @return serialized duty cycle value
     */
    private short getSerializedDutyCycle(int dutyCycle) {
        short serDutyCycle = 0;
        short bitValue = 0x4;
        for (int bitPos = 0; bitPos < 8; bitPos++) {
            serDutyCycle |= (dutyCycle & bitValue) >> 2;
            bitValue *= 2;
        }
        return serDutyCycle;
    }
    
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if ( !(value instanceof PWM_Parameters) ) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        PWM_Parameters pwmParams = (PWM_Parameters)value;
        
        short[] protoValue = new short[TYPE_SIZE];
        protoValue[PRESCALER_POS] = getSerializedPrescaler(
                pwmParams.getPrescaler(), pwmParams.getDutyCycle()
        );
        protoValue[PERIOD_POS] = (short)pwmParams.getPeriod();
        protoValue[DUTY_CYCLE_POS] = getSerializedDutyCycle(pwmParams.getDutyCycle());
               
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported");
    }
}
