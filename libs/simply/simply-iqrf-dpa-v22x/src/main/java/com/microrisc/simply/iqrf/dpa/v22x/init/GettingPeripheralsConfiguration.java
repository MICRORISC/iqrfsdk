/*
 * Copyright 2014 MICRORISC s.r.o..
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

package com.microrisc.simply.iqrf.dpa.v22x.init;

/**
 * Configuration of process of getting peripherals on nodes.
 * 
 * @author Michal Konopa
 */
public final class GettingPeripheralsConfiguration {
    /** Default value of number of attempts of getting peripherals from node. */
    public static int DEFAULT_GET_PER_ATTEMPTS_NUM = 3;
    
    /** Default value of timeout [in ms] of operation of getting peripherals from node. */
    public static int DEFAULT_GET_PER_TIMEOUT = 10000;
    
    
    /** Number of attempts of getting peripherals from node. */
    private final int getPerAttemptsNum;

    /** Timeout [in ms] of operation of getting peripherals from node. */
    private final long getPerTimeout;
    
    
    private int checkGetPerAttemptsNum(int getPerAttemptsNum) {
        if (getPerAttemptsNum <= 0) {
            throw new IllegalArgumentException(
                "Value of number of attempts of getting peripherals from node must be positive"
            );
        }
        return getPerAttemptsNum;
    } 
    
    private long checkGetPerTimeout(long getPerTimeout) {
        if (getPerTimeout < 0) {
            throw new IllegalArgumentException(
                "Value of timeout [in ms] of operation of getting peripherals from node must be nonnegative"
            );
        }
        return getPerTimeout;
    }
    
    
    /**
     * Creates new object of configuration.
     * @param getPerAttemptsNum number of attempts of getting peripherals from node
     * @param getPerTimeout timeout [in ms] of operation of getting peripherals from node
     */
    public GettingPeripheralsConfiguration(int getPerAttemptsNum, long getPerTimeout) {
        this.getPerAttemptsNum = checkGetPerAttemptsNum(getPerAttemptsNum);
        this.getPerTimeout = checkGetPerTimeout(getPerTimeout);
    }
    
    /**
     * @return number of attempts of getting peripherals from node
     */
    public int getPerAttemptsNum() {
        return getPerAttemptsNum;
    }

    /**
     * @return timeout [in ms] of operation of getting peripherals from node
     */
    public long getPerTimeout() {
        return getPerTimeout;
    }
}
