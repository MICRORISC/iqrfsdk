
package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * Encapsulates information for putting device into sleep mode.
 * 
 * @author Michal Konopa
 */
public final class SleepInfo {
    /** 
     * Sleep time in 2.097s (i.e. 2048 * 1.024 ms) units. 0 specifies endless 
     * sleep (except Control.bit1 is set to run calibration process without 
     * performing sleep). Maximum sleep time is 38 hours 10 minutes 38.95
     * seconds. 
     */
    private final int time;
    
    /** Time lower bound. */
    public static final int TIME_LOWER_BOUND = 0x00;
    
    /** Time upper bound. */
    public static final int TIME_UPPER_BOUND = 0xFFFF;
    
    private static int checkTime(int time) {
        if ( (time < TIME_LOWER_BOUND) || (time > TIME_UPPER_BOUND) ) {
            throw new IllegalArgumentException("Time out of bounds");
        }
        return time;
    }
    
    /** 
     * Control	
     * bit 0 – wake up on PIN change.
     * bit 1 - runs calibration process before going to sleep.
     * bit 2 - if set, then when the device wakes up after the sleep period, 
     *          a green LED once shortly flashes.
     */
    private final int control;
    
    /** Control lower bound. */
    public static final int CONTROL_LOWER_BOUND = 0x00;
    
    /** Control upper bound. */
    public static final int CONTROL_UPPER_BOUND = 0xFF;
    
    private static int checkControl(int control) {
        if ( (control < CONTROL_LOWER_BOUND) || (control > CONTROL_UPPER_BOUND) ) {
            throw new IllegalArgumentException("Control out of bounds");
        }
        return control;
    }
    
    
    /**
     * Creates new {@code SleepInfo} object.
     * @param time sleep time in 2.097s (i.e. 2048 * 1.024 ms) units.
     * @param control 
     *        bit 0 – wake up on PIN change.
     *        bit 1 - runs calibration process before going to sleep
     *        bit 2 - if set, then when the device wakes up after the sleep period, 
     *          a green LED once shortly flashes
     * @throws IllegalArgumentException if: <br> 
     *         specified sleep time is out of [{@code TIME_LOWER_BOUND}..{@code TIME_UPPER_BOUND}] interval <br> 
     *         specified control is out of [{@code CONTROL_LOWER_BOUND}..{@code CONTROL_UPPER_BOUND}] interval
     */
    public SleepInfo(int time, int control) {
        this.time = checkTime(time);
        this.control = checkControl(control);
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @return the control
     */
    public int getControl() {
        return control;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Time: " + time + NEW_LINE);
        strBuilder.append(" Control bit: " + control + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
