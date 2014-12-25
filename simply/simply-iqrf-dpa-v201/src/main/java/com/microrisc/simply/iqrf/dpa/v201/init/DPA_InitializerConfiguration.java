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

package com.microrisc.simply.iqrf.dpa.v201.init;

/**
 * Configuration of DPA initializer.
 * 
 * @author Michal Konopa
 */
public class DPA_InitializerConfiguration {
    /** Configuration of enumeration process. */
    private final EnumerationConfiguration enumConfig;
    
    /** Configuration of processing of bonded nodes. */
    private final BondedNodesConfiguration bondedNodesConfig;
    
    /** Configuration of discovery process. */
    private final DiscoveryConfiguration discoConfig;

    
    public static class Builder {
        private EnumerationConfiguration enumConfig;
        private BondedNodesConfiguration bondedNodesConfig;
        private DiscoveryConfiguration discoConfig;
        
        
        public Builder enumerationConfiguration(EnumerationConfiguration enumConfig) {
            this.enumConfig = enumConfig;
            return this;
        }
        
        public Builder bondedNodesConfiguration(BondedNodesConfiguration bondedNodesConfig) {
            this.bondedNodesConfig = bondedNodesConfig;
            return this;
        }
        
        public Builder discoveryConfiguration(DiscoveryConfiguration discoConfig) {
            this.discoConfig = discoConfig;
            return this;
        }
        
        public DPA_InitializerConfiguration build() {
            return new DPA_InitializerConfiguration(this);
        }
    }
    
    /**
     * Creates configuration of DPA initializer.
     */
    private DPA_InitializerConfiguration(Builder builder) {
        this.enumConfig = builder.enumConfig;
        this.bondedNodesConfig = builder.bondedNodesConfig;
        this.discoConfig = builder.discoConfig;
    }
    
    
    /**
     * @return enumeration configuration
     */
    public EnumerationConfiguration getEnumerationConfiguration() {
        return enumConfig;
    }

    /**
     * @return processing of bonded nodes configuration
     */
    public BondedNodesConfiguration getBondedNodesConfiguration() {
        return bondedNodesConfig;
    }
    
    /**
     * @return discovery configuration
     */
    public DiscoveryConfiguration getDiscoveryConfiguration() {
        return discoConfig;
    }
}
