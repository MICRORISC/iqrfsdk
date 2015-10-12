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
 * Configuration of DPA initializer.
 * 
 * @author Michal Konopa
 */
public final class DPA_InitializerConfiguration {
    // type of initialization
    private final InitializationType initType;
    
    // configuration of enumeration process
    private final EnumerationConfiguration enumConfig;
    
    // fixed initialization configuration
    private final FixedInitConfiguration fixedInitConfig;
    
    // discovery configuration
    private final DiscoveryConfiguration discoveryConfig;
    
    
    public static class Builder {
        private final InitializationType initType;
        private EnumerationConfiguration enumConfig;
        private FixedInitConfiguration fixedInitConfig;
        private DiscoveryConfiguration discoveryConfig;
        
        public Builder(InitializationType initType) {
            this.initType = initType;
        }
        
        public Builder enumerationConfiguration(EnumerationConfiguration enumConfig) {
            this.enumConfig = enumConfig;
            return this;
        }
        
        public Builder fixedInitConfiguration(FixedInitConfiguration fixedInitConfig) {
            this.fixedInitConfig = fixedInitConfig;
            return this;
        }
        
        public Builder discoveryConfiguration(DiscoveryConfiguration discoveryConfig) {
            this.discoveryConfig = discoveryConfig;
            return this;
        }
        
        public DPA_InitializerConfiguration build() {
            return new DPA_InitializerConfiguration(this);
        }
    }
    
    private static InitializationType checkInitType(InitializationType initType) {
        if ( initType == null ) {
            throw new IllegalArgumentException("Initialization type cannot be null.");
        }
        return initType;
    }
    
    /**
     * Creates configuration of DPA initializer.
     */
    private DPA_InitializerConfiguration(Builder builder) {
        this.initType = checkInitType(builder.initType);
        this.enumConfig = builder.enumConfig;
        this.fixedInitConfig = builder.fixedInitConfig;
        this.discoveryConfig = builder.discoveryConfig;
        
        switch ( initType ) {
            case ENUMERATION:
                if ( this.enumConfig == null ) {
                    throw new IllegalArgumentException("Enumeration configuration not defined.");
                }
                break;
            case FIXED:
                if ( this.fixedInitConfig == null ) {
                    throw new IllegalArgumentException("Fixed initialization configuration not defined.");
                }
                break;
            default:
                throw new IllegalStateException("Unsupported type of configuration: " + initType);
        }
    }
    
    
    /**
     * @return initialization type
     */
    public InitializationType getInitializationType() {
        return initType;
    }
    
    /**
     * @return enumeration configuration
     */
    public EnumerationConfiguration getEnumerationConfiguration() {
        return enumConfig;
    }
    
    /**
     * @return fixed initialization configuration
     */
    public FixedInitConfiguration getFixedInitConfiguration() {
        return fixedInitConfig;
    }
    
    /**
     * @return discovery process configuration
     */
    public DiscoveryConfiguration getDiscoveryConfiguration() {
        return discoveryConfig;
    }
}
