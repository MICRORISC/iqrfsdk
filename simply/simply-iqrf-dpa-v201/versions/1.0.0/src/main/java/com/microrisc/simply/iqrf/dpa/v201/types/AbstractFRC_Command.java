
package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * Abstract base class of FRC command classes 
 * 
 * @author Michal Konopa
 */
public abstract class AbstractFRC_Command implements FRC_Command {
    /** User data. */
    protected short[] userData = null;
    
    /** Required length of user data. */ 
    public static final int USER_DATA_LENGHT = 2;
    
    /**
     * Checks validity of specified user data.
     * @param userData user data to check
     * @return specified user data if the validity checking was allright
     * @throws IllegalArgumentException if the validity check failed. This would be
     * in these cases:
     *          - {@code userData} is {@code null} <br>
     *          - length of {@code userData} is differernt from USER_DATA_LENGHT
     */
    private static short[] checkUserData(short[] userData) {
        if ( userData == null ) {
            throw new IllegalArgumentException("User data cannot be null.");
        }
        
        if ( (userData.length == USER_DATA_LENGHT) ) {
            throw new IllegalArgumentException(
                    "Length of user data must be of: " + USER_DATA_LENGHT + " bytes."
            );
        }
        return userData;
    }
    
    /**
     * Protected constructor.
     * First of all, validity checking on {@code userData} is performed.
     * <p>
     * The rules to check for are as follows: <br>
     * - {@code userData} cannot be {@code null} <br>
     * - length of {@code userData} must be USER_DATA_LENGHT
     * @param userData user data
     * @throws IllegalArgumentException if {@code userData} validity checking of 
     *         user data has failed
     */
    protected AbstractFRC_Command(short[] userData) {
        checkUserData(userData);
        this.userData = new short[userData.length];
        System.arraycopy(userData, 0, this.userData, 0, userData.length);
    }
    
    /**
     * Protected constructor.
     * User data is initialized to an array of {@code USER_DATA_LENGHT} length
     * where each data item is equal to {@code 0} - <b>default user data</b>.
     */
    protected AbstractFRC_Command() {
        this.userData = new short[USER_DATA_LENGHT];
    }
}
