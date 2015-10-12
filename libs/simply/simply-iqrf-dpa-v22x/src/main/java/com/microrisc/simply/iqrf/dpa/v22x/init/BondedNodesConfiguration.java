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
 * Configuration of bonded nodes.
 * 
 * @author Michal Konopa
 */
public class BondedNodesConfiguration {
    /** Default value of number of attempts of getting bonded nodes from coordinator. */
    public static int DEFAULT_GET_BONDED_NODES_ATTEMPTS_NUM = 3;
    
    /** Default value of timeout [in ms] of operation of getting bonded nodes from coordinator. */
    public static int DEFAULT_GET_BONDED_NODES_TIMEOUT = 5000;
    
    
    /** Number of attempts of getting bonded nodes from coordinator. */
    private int getBondedNodesAttemptsNum;

    /** Timeout [in ms] of operation of getting bonded nodes from coordinator. */
    private long getBondedNodesTimeout;
    
    
    private int checkGetBondedNodesAttemptsNum(int getBondedNodesAttemptsNum) {
        if (getBondedNodesAttemptsNum <= 0) {
            throw new IllegalArgumentException(
                "Value of number of attempts of getting bonded nodes from coordinator must be positive"
            );
        }
        return getBondedNodesAttemptsNum;
    } 
    
    private long checkGetBondedNodesTimeout(long getBondedNodesTimeout) {
        if (getBondedNodesTimeout < 0) {
            throw new IllegalArgumentException(
                "Value of timeout [in ms] of operation of getting bonded nodes from coordinator must be nonnegative"
            );
        }
        return getBondedNodesTimeout;
    }
    
    /**
     * Creates new object of enumeration configuration.
     * @param getBondedNodesAttemptsNum number of attempts of getting bonded nodes from coordinator
     * @param getBondedNodesTimeout timeout [in ms] of operation of getting bonded nodes from coordinator
     */
    public BondedNodesConfiguration(int getBondedNodesAttemptsNum, 
            long getBondedNodesTimeout
    ) {
        this.getBondedNodesAttemptsNum = checkGetBondedNodesAttemptsNum(getBondedNodesAttemptsNum);
        this.getBondedNodesTimeout = checkGetBondedNodesTimeout(getBondedNodesTimeout);
    }
    
    /**
     * @return number of attempts of getting bonded nodes from coordinator
     */
    public int getBondedNodesAttemptsNum() {
        return getBondedNodesAttemptsNum;
    }

    /**
     * @return timeout [in ms] of operation of getting bonded nodes from coordinator
     */
    public long getBondedNodesTimeout() {
        return getBondedNodesTimeout;
    }
}
