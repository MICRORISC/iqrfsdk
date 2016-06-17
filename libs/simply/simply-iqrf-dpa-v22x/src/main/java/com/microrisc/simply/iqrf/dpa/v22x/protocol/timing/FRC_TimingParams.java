/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.protocol.timing;

import com.microrisc.simply.iqrf.RF_Mode;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Configuration;

/**
 * FRC timing parameters relating to one concrete network.
 * 
 * @author Michal Konopa
 */
public final class FRC_TimingParams extends TimingParams {
    
    // number of bonded nodes
    private final int bondedNodesNum;
    
    // RF mode
    private final RF_Mode rfMode;
    
    // response time on coordinator
    private final FRC_Configuration.FRC_RESPONSE_TIME reponseTime;
    
    
    // DEFAULT VALUES FOR PARAMETERS
    /** Default value for number of bonded nodes. */
    public static final int DEFAULT_BONDED_NODES_NUM = 0;
    
    /** Default value for RF mode. */
    public static final RF_Mode DEFAULT_RF_MODE = RF_Mode.LP;
    
    /** Default value for response time on coordinator. */
    public static final FRC_Configuration.FRC_RESPONSE_TIME DEFAULT_RESPONSE_TIME 
            = FRC_Configuration.FRC_RESPONSE_TIME.TIME_40_MS;
    
    
    /**
     * Creates new object of FRC timing parameters.
     * Timing parameters will be set to their default values, specifically: <br>
     * - number of bonded nodes = {@code DEFAULT_BONDED_NODES_NUM} <br>
     * - RF mode = {@code DEFAULT_RF_MODE} <br>
     * - response time = {@code DEFAULT_RESPONSE_TIME}
     */
    public FRC_TimingParams() {
        this.bondedNodesNum = DEFAULT_BONDED_NODES_NUM;
        this.rfMode = DEFAULT_RF_MODE;
        this.reponseTime = DEFAULT_RESPONSE_TIME;
    }
    
    /**
     * Creates new object of FRC timing parameters according to specified values.
     * @param bondedNodesNum number of bonded nodes
     * @param rfMode actual RF mode
     * @param responseTime response time
     */
    public FRC_TimingParams(
        int bondedNodesNum, RF_Mode rfMode, FRC_Configuration.FRC_RESPONSE_TIME responseTime
    ) {
        this.bondedNodesNum = bondedNodesNum;
        this.rfMode = rfMode;
        this.reponseTime = responseTime;
    }

    /**
     * @return number of bonded nodes
     */
    public int getBondedNodesNum() {
        return bondedNodesNum;
    }
    
    /**
     * @return RF mode
     */
    public RF_Mode getRfMode() {
        return rfMode;
    }

    /**
     * @return the response time
     */
    public FRC_Configuration.FRC_RESPONSE_TIME getResponseTime() {
        return this.reponseTime;
    }
    
}
