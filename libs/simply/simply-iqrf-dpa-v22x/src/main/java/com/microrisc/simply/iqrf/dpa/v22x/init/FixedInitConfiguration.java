/*
 * Copyright 2015 MICRORISC s.r.o..
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
 * Configuration of fixed type of initialization.
 * <p>
 * The word 'fixed' means here that in comparison to enumeration,
 * the fixed type of initialization does NOT communicate with connected IQRF
 * networks in the process of initialization, but it is externally defined by 
 * some other entity, e.g. by file.
 * 
 * @author Michal Konopa
 */
public final class FixedInitConfiguration {
    // networks functionality mapping
    private final NetworksFunctionalityToSimplyMapping networkFuncMapping; 
    
    // configuration of processing of bonded nodes
    private final BondedNodesConfiguration bondedNodesConfig;
    
    
    private static NetworksFunctionalityToSimplyMapping 
        checkNetworksFunctionalityToSimplyMapping(
                NetworksFunctionalityToSimplyMapping networkFuncMapping
        ) {
        if ( networkFuncMapping == null ) {
            throw new IllegalArgumentException("Networks functionality mapping cannot be null.");
        }
        
        return networkFuncMapping;
    }
    
    
    /**
     * Creates fixed initialization configuration object with specified 
     * networks functionality mapping.
     * @param networkFuncMapping networks functionality mapping
     * @throws IllegalArgumentException if {@code networkFuncMapping} is {@code null}
     */
    public FixedInitConfiguration(
            NetworksFunctionalityToSimplyMapping networkFuncMapping
    ) {
        this.networkFuncMapping = checkNetworksFunctionalityToSimplyMapping(networkFuncMapping);
        this.bondedNodesConfig = null;
    }
    
    /**
     * Creates fixed initialization configuration object with specified 
     * networks functionality mapping.
     * @param networkFuncMapping networks functionality mapping
     * @param bondedNodesConfig bonded nodes configuration
     * @throws IllegalArgumentException if {@code networkFuncMapping} is {@code null}
     */
    public FixedInitConfiguration(
            NetworksFunctionalityToSimplyMapping networkFuncMapping,
            BondedNodesConfiguration bondedNodesConfig
    ) {
        this.networkFuncMapping = checkNetworksFunctionalityToSimplyMapping(networkFuncMapping);
        this.bondedNodesConfig = bondedNodesConfig;
    }
    
    /**
     * @return networks functionality mapping
     */
    public NetworksFunctionalityToSimplyMapping getNetworksFunctionalityToSimplyMapping() {
        return networkFuncMapping;
    }
    
    /**
     * @return processing of bonded nodes configuration
     */
    public BondedNodesConfiguration getBondedNodesConfiguration() {
        return bondedNodesConfig;
    }
}
