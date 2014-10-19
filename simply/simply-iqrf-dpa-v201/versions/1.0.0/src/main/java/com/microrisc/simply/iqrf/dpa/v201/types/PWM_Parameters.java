
package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * Encapsulates PWM parameters.
 * 
 * @author Michal Konopa
 */
public final class PWM_Parameters {
    
    /**
     * Prescaler type.
     */
    public static enum Prescaler {
        PRESCALER_1     (0),
        PRESCALER_4     (1),
        PRESCALER_16    (2),
        PRESCALER_64    (4);
        
        // value of prescaler
        private final int presValue;
        
        private Prescaler(int value) {
            this.presValue = value;
        }
        
        public int getPrescalerValue() {
            return presValue;
        }
    }
    
    /**
     * Returns enum value corresponding to specified integer value.
     * @param prescalerValue
     * @return 
     */
    private Prescaler getPrescaler(int prescalerValue) {
        for (Prescaler prescEnum : Prescaler.values()) {
            if (prescalerValue == prescEnum.getPrescalerValue()) {
                return prescEnum;
            }
        }
        throw new IllegalArgumentException("Uknown prescaler value: " + prescalerValue);
    }
    
    /** Prescaler. */
    private final Prescaler prescaler;
    
    /** Period. */
    private final int period;
    
    /** Hops lower bound. */
    public static final int PERIOD_LOWER_BOUND = 0x00; 
    
    /** Hops upper bound. */
    public static final int PERIOD_UPPER_BOUND = 0xFF; 
    
    private static int checkPeriod(int period) {
        if ( (period < PERIOD_LOWER_BOUND) || (period > PERIOD_UPPER_BOUND) ) {
            throw new IllegalArgumentException("Period out of bounds");
        }
        return period;
    }
    
    
    /** Duty cycle. */
    private final int dutyCycle;
    
    /** Hops lower bound. */
    public static final int DUTY_CYCLE_LOWER_BOUND = 0x00; 
    
    /** Hops upper bound. */
    public static final int DUTY_CYCLE_UPPER_BOUND = 0xFF; 
    
    private static int checkDutyCycle(int dutyCycle) {
        if ( (dutyCycle < DUTY_CYCLE_LOWER_BOUND) || (dutyCycle > DUTY_CYCLE_UPPER_BOUND) ) {
            throw new IllegalArgumentException("Duty cycle out of bounds");
        }
        return dutyCycle;
    }
    
    
    /**
     * Creates new PWM parameters object.
     * When all 3 parameters equal to 0, PWM is stopped.
     * @param prescaler prescaler
     * @param period period
     * @param dutyCycle duty cycle
     * @throws IllegalArgumentException if: <br> 
     *         specified period is out of [{@code PERIOD_LOWER_BOUND}..{@code PERIOD_UPPER_BOUND}] interval <br>
     *         specified duty cycle is out of [{@code DUTY_CYCLE_LOWER_BOUND}..{@code DUTY_CYCLE_UPPER_BOUND}] interval
     */
    public PWM_Parameters(Prescaler prescaler, int period, int dutyCycle) {
        this.prescaler = prescaler;
        this.period = checkPeriod(period);
        this.dutyCycle = checkDutyCycle(dutyCycle);
    }
    
    /**
     * Creates new PWM parameters object.
     * When all 3 parameters equal to 0, PWM is stopped.
     * @param prescalerValue prescaler value
     * @param period period
     * @param dutyCycle duty cycle
     * @throws IllegalArgumentException if {@code prescalerValue} is unknown
     */
    public PWM_Parameters(int prescalerValue, int period, int dutyCycle) {
        this.prescaler = getPrescaler(prescalerValue);
        this.period = period;
        this.dutyCycle = dutyCycle;
    }
    
    /**
     * @return the prescaler
     */
    public Prescaler getPrescaler() {
        return prescaler;
    }

    /**
     * @return the period
     */
    public int getPeriod() {
        return period;
    }

    /**
     * @return the duty cycle
     */
    public int getDutyCycle() {
        return dutyCycle;
    }
    
}
