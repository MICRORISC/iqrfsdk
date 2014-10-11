
package com.microrisc.simply.iqrf.dpa.types;

/**
 * FRC commands common functionality.
 * 
 * @author Michal Konopa
 */
public interface FRC_Command {
    /**
     * Returns ID of command. Specifies data to be collected.
     * @return ID of FRC command
     */
    int getId();
    
    /**
     * User data that are available at IQRF OS array variable DataOutBeforeResponseFRC 
     * at FRC Value event. The length is from 2 to 30 bytes.
     * @return user data
     */
    short[] getUserData();
}
