
package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * States of LED.
 * 
 * @author Michal Konopa
 */
public enum LED_State {
    OFF    (0x00),
    ON     (0x01);
    
    // state
    private final int state;
    
    
    private LED_State(int state) {
        this.state = state;
    }
    
    /**
     * @return integer value of the LED state.
     */
    public int getStateValue() {
        return state;
    }
}
