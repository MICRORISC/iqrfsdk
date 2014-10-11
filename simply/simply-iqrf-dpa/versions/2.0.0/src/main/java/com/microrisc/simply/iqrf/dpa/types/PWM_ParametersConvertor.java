
package com.microrisc.simply.iqrf.dpa.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
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
     * @return PWM_ParametersConvertor instance 
     */
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
     * Returns serialized prescaler value. 
     * @param prescaler
     * @param dutyCycle
     * @return 
     */
    private short getSerializedPrescaler(PWM_Parameters.Prescaler prescaler, int dutyCycle) {
        short serPrescaler = (short)prescaler.getPrescalerValue();
        serPrescaler |= (dutyCycle & 0x01) << 4;
        serPrescaler |= (dutyCycle & 0x02) << 4;
        return serPrescaler;
    }
    
    /**
     * Returns serialized duty cycle value. 
     * @param dutyCycle
     * @return 
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

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported");
    }
}
