
package com.microrisc.simply.iqrf.dpa.types;

/**
 * States of IO.
 * 
 * @author Michal Konopa
 */
public enum IO_State {
    LOW_OUTPUT      (0, 0),
    LOW_INPUT       (0, 1),
    HIGH_OUTPUT     (1, 0),
    HIGH_INPUT      (1, 1);
    
    // low or high
    private int state;
    
    // input or output
    private int direction;
    
    
    private IO_State(int state, int direction) {
        this.state = state;
        this.direction = direction;
    }
    
    /**
     * @return integer value of IO state ( low or high ).
     */
    public int getStateValue() {
        return state;
    }
    
    /**
     * @return integer value of IO direction ( input or output ).
     */
    public int getDirectionValue() {
        return direction;
    }
}
