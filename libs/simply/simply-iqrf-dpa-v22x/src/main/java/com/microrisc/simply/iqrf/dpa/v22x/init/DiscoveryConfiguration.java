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

package com.microrisc.simply.iqrf.dpa.v22x.init;

/**
 * Configuration of discovery process.
 * 
 * @author Michal Konopa
 */
public class DiscoveryConfiguration {
    
    /** Default value of timeout [in ms] of operation of running discovery process. */
    public static long DEFAULT_DISCOVERY_TIMEOUT = 60000;
    
    /** Default value of TX power used in discovery process[0-7]. */
    public static int DEFAULT_DISCOVERY_TX_POWER = 7;
    
    
    /** Timeout [in ms] of operation of running discovery process. */
    private long discoveryTimeout;
    
    /** TX power used in discovery process[0-7]. */
    private int discoveryTxPower;
    
    
    private long checkDiscoveryTimeout(long discoveryTimeout) {
        if (discoveryTimeout < 0) {
            throw new IllegalArgumentException(
                "Value of timeout [in ms] of discovery must be nonnegative"
            );
        }
        return discoveryTimeout;
    }
    
    private int checkDiscoveryTxPower(int discoveryTxPower) {
        if (discoveryTxPower < 0 || discoveryTxPower > 7) {
            throw new IllegalArgumentException(
                "Value of TX power [in ms] of discovery must be in interval [0-7]"
            );
        }
        return discoveryTxPower;
    }
    
    
    /**
     * Creates new object of discovery configuration.
     * @param discoveryTimeout timeout of discovery operation
     * @param discoveryTxPower TX power used in discovery process
     */
    public DiscoveryConfiguration(long discoveryTimeout, int discoveryTxPower) {
        this.discoveryTimeout = checkDiscoveryTimeout(discoveryTimeout);
        this.discoveryTxPower = checkDiscoveryTxPower(discoveryTxPower);
    }
    
    /**
     * @return timeout [in ms] of operation of running discovery process.
     */
    public long discoveryTimeout() {
        return discoveryTimeout;
    }

    /**
     * @return TX power used in discovery process[0-7].
     */
    public int dicoveryTxPower() {
        return discoveryTxPower;
    }
}
