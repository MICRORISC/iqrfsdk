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
 * Configuration of the enumeration process.
 * 
 * @author Michal Konopa
 */
public final class EnumerationConfiguration {
    // configuration of the process of getting peripherals from nodes
    private final GettingPeripheralsConfiguration gettingPerConfig;
    
    // configuration of processing of bonded nodes
    private final BondedNodesConfiguration bondedNodesConfig;

    
    private static GettingPeripheralsConfiguration checkGettingPeripheralsConfiguration(
        GettingPeripheralsConfiguration gettingPerConfig
    ) {
        if ( gettingPerConfig == null ) {
            throw new IllegalArgumentException("Getting peripherals configuration cannot be null.");
        }
        return gettingPerConfig;
    }
    
    
    /**
     * Creates configuration of DPA initializer.
     * @param gettingPerConfig getting peripherals configuration
     * @throws IllegalArgumentException if {@code gettingPerConfig} is {@code null}
     */
    public EnumerationConfiguration(
            GettingPeripheralsConfiguration gettingPerConfig
    ) {
        this.gettingPerConfig = checkGettingPeripheralsConfiguration(gettingPerConfig);
        this.bondedNodesConfig = null;
    }
    
    /**
     * Creates configuration of DPA initializer.
     * @param gettingPerConfig getting peripherals configuration
     * @param bondedNodesConfig bonded nodes configuration
     * @throws IllegalArgumentException if {@code gettingPerConfig} is {@code null}
     */
    public EnumerationConfiguration(
            GettingPeripheralsConfiguration gettingPerConfig,
            BondedNodesConfiguration bondedNodesConfig
    ) {
        this.gettingPerConfig = checkGettingPeripheralsConfiguration(gettingPerConfig);
        this.bondedNodesConfig = bondedNodesConfig;
    }
    
    
    /**
     * @return getting peripherals configuration
     */
    public GettingPeripheralsConfiguration getGettingPeripheralsConfiguration() {
        return gettingPerConfig;
    }

    /**
     * @return processing of bonded nodes configuration
     */
    public BondedNodesConfiguration getBondedNodesConfiguration() {
        return bondedNodesConfig;
    }
    
}
