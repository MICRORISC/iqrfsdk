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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * DPA initializer configuration factory.
 * 
 * @author Michal Konopa
 */
public final class DPA_InitializerConfigurationFactory {
    
    // parses and returns type of initialization 
    private static InitializationType getInitType(Configuration configuration) {
        String initTypeStr = configuration.getString("initialization.type");
        if ( initTypeStr == null ) {
            throw new IllegalArgumentException("Undefined type of initialization.");
        }
        
        switch ( initTypeStr ) {
            case "dpa.enumeration":
                return InitializationType.ENUMERATION;
            case "dpa.fixed":
                return InitializationType.FIXED;
            default:
                throw new IllegalArgumentException("Unsupported type of initialization.");
        }
    }
    
    private static GettingPeripheralsConfiguration createGettingPeripheralsConfiguration(
            Configuration configuration
    ) {
        int getPerAttemptsNum = configuration.getInt(
                "initialization.type.dpa.enumeration.getPeripheral.num_attempts",
                GettingPeripheralsConfiguration.DEFAULT_GET_PER_ATTEMPTS_NUM
        );
        
        long getPerTimeout = configuration.getLong(
                "initialization.type.dpa.enumeration.getPeripheral.timeout",
                GettingPeripheralsConfiguration.DEFAULT_GET_PER_TIMEOUT
        );
        
        return new GettingPeripheralsConfiguration(getPerAttemptsNum, getPerTimeout);
    }
    
    private static BondedNodesConfiguration createBondedNodesConfiguration(
            int processBondedNodes, int getBondedNodesAttemptsNum, long getBondeNodesTimeout
    ) { 
        // if user explicitly sets to process bonded nodes
        if ( processBondedNodes > 0 ) {
            if ( getBondedNodesAttemptsNum == -1 ) {
                getBondedNodesAttemptsNum = BondedNodesConfiguration.DEFAULT_GET_BONDED_NODES_ATTEMPTS_NUM;
            }
            
            if ( getBondeNodesTimeout == -1 ) {
                getBondeNodesTimeout = BondedNodesConfiguration.DEFAULT_GET_BONDED_NODES_TIMEOUT;
            }
        }
        
        return new BondedNodesConfiguration(
                getBondedNodesAttemptsNum, getBondeNodesTimeout
        );
    }
    
    private static BondedNodesConfiguration createBondedNodesConfigurationForEnum(
            Configuration configuration
    ) {
        int processBondedNodes = configuration.getInt("initialization.type.dpa.enumeration.involveBondedNodes", 0);
        if ( processBondedNodes == 0 ) {
            return null;
        }
        
        int getBondedNodesAttemptsNum = configuration.getInt(
                "initialization.type.dpa.enumeration.involveBondedNodes.num_attempts", -1
        );
        
        long getBondeNodesTimeout = configuration.getLong(
                "initialization.type.dpa.enumeration.involveBondedNodes.timeout", -1
        );
        
        return createBondedNodesConfiguration(
                processBondedNodes, getBondedNodesAttemptsNum, getBondeNodesTimeout 
        );
    }
    
    private static BondedNodesConfiguration createBondedNodesConfigurationForFixed(
            Configuration configuration
    ) {
        int processBondedNodes = configuration.getInt("initialization.type.dpa.fixed.involveBondedNodes", 0);
        if ( processBondedNodes == 0 ) {
            return null;
        }
        
        int getBondedNodesAttemptsNum = configuration.getInt(
                "initialization.type.dpa.fixed.involveBondedNodes.num_attempts", -1
        );
        
        long getBondeNodesTimeout = configuration.getLong(
                "initialization.type.dpa.fixed.involveBondedNodes.timeout", -1
        );
        
        return createBondedNodesConfiguration(
                processBondedNodes, getBondedNodesAttemptsNum, getBondeNodesTimeout 
        );
    }
    
    private static DiscoveryConfiguration createDiscoveryConfiguration(
            Configuration configuration
    ) {
        int doDiscovery = configuration.getInt("initialization.type.dpa.discovery", 0);
        if ( doDiscovery == 0 ) {
            return null;
        }
        
        long discoveryTimeout = configuration.getLong(
                "initialization.type.dpa.discovery.timeout", -1
        );
        
        int discoveryTxPower = configuration.getInt(
                "initialization.type.dpa.discovery.txPower", -1
        );
       
        // if user explicitly sets to do discovery
        if ( doDiscovery > 0 ) {
            if ( discoveryTimeout == -1 ) {
                discoveryTimeout = DiscoveryConfiguration.DEFAULT_DISCOVERY_TIMEOUT;
            }
            
            if ( discoveryTxPower == -1 ) {
                discoveryTxPower = DiscoveryConfiguration.DEFAULT_DISCOVERY_TX_POWER;
            }
        }
        
        return new DiscoveryConfiguration(discoveryTimeout, discoveryTxPower);
    }
    
    private static EnumerationConfiguration createEnumerationConfiguration(
            Configuration configuration
    ) {
        return new EnumerationConfiguration(
                createGettingPeripheralsConfiguration(configuration), 
                createBondedNodesConfigurationForEnum(configuration)
        );
    }
    
    private static FixedInitConfiguration createFixedInitConfiguration(
        Configuration configuration
    ) throws ConfigurationException {
        String sourceFileName = configuration.getString(
                "initialization.type.dpa.fixed.sourceFile",
                "PeripheralDistribution.xml"
        );
        
        NetworksFunctionalityToSimplyMapping networkFuncMapping 
                = NetworksFunctionalityToSimplyMappingParser.parse(sourceFileName);
        
        return new FixedInitConfiguration(
                networkFuncMapping, 
                createBondedNodesConfigurationForFixed(configuration)
        );
    }
    
    
    /**
     * Returns DPA initializer configuration settings.
     * @param configuration source configuration
     * @return DPA initializer configuration settings
     * @throws org.apache.commons.configuration.ConfigurationException if an
     *         error has occured during creation of intitializer configuration
     */
    public static DPA_InitializerConfiguration getDPA_InitializerConfiguration(
            Configuration configuration
    ) throws ConfigurationException {
        InitializationType initType = getInitType(configuration);
        switch ( initType ) {
            case ENUMERATION:
                return new DPA_InitializerConfiguration.Builder(initType)
                        .enumerationConfiguration(createEnumerationConfiguration(configuration))
                        .discoveryConfiguration(createDiscoveryConfiguration(configuration))
                        .build();
            case FIXED:
                return new DPA_InitializerConfiguration.Builder(initType)
                        .fixedInitConfiguration(createFixedInitConfiguration(configuration))
                        .discoveryConfiguration(createDiscoveryConfiguration(configuration))
                        .build();
            default:
                throw new IllegalArgumentException("Unsupported type of initialization.");
        }
    }
    
}
