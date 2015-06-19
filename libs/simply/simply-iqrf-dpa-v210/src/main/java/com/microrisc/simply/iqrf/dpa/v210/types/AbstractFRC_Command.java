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

package com.microrisc.simply.iqrf.dpa.v210.types;

/**
 * Abstract base class of FRC command classes 
 * 
 * @author Michal Konopa
 */
public abstract class AbstractFRC_Command implements FRC_Command {
    /** User data. */
    protected short[] userData = null;
    
    /** Minimal length of user data. */ 
    public static final int USER_DATA_MIN_LENGHT = 2;
    
    /** Maximal length of user data. */ 
    public static final int USER_DATA_MAX_LENGHT = 30;
    
    /**
     * Checks validity of specified user data.
     * @param userData user data to check
     * @return specified user data if the validity checking was allright
     * @throws IllegalArgumentException if the validity check failed. This would be
     * in these cases:
     *          - {@code userData} is {@code null} <br>
     *          - length of {@code userData} is out of USER_DATA_MIN_LENGHT..USER_DATA_MAX_LENGHT interval
     */
    private static short[] checkUserData(short[] userData) {
        if ( userData == null ) {
            throw new IllegalArgumentException("User data cannot be null.");
        }
        
        if ( (userData.length < USER_DATA_MIN_LENGHT) || (userData.length > USER_DATA_MAX_LENGHT) ) {
            throw new IllegalArgumentException(
                    "Length of user data must be within the interval of: " + USER_DATA_MIN_LENGHT 
                    + ".." + USER_DATA_MAX_LENGHT
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
     * - length of {@code userData} must be in the USER_DATA_MIN_LENGHT..USER_DATA_MAX_LENGHT interval
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
     * User data is initialized to an array of {@code USER_DATA_MIN_LENGHT} length
     * where each data item is equal to {@code 0} - <b>default user data</b>.
     */
    protected AbstractFRC_Command() {
        this.userData = new short[USER_DATA_MIN_LENGHT];
    }
}
