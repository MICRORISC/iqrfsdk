
package com.microrisc.simply.iqrf.dpa.init;

import org.apache.commons.configuration.Configuration;

/**
 * DPA initializer configuration factory.
 * 
 * @author Michal Konopa
 */
public class DPA_InitializerConfigurationFactory {
    
    private static EnumerationConfiguration createEnumerationConfiguration(
            Configuration configuration
    ) {
        int getPerAttemptsNum = configuration.getInt(
                "initialization.type.dpa.enumeration.getPeripheral.num_attempts",
                EnumerationConfiguration.DEFAULT_GET_PER_ATTEMPTS_NUM
        );
        
        long getPerTimeout = configuration.getLong(
                "initialization.type.dpa.enumeration.getPeripheral.timeout",
                EnumerationConfiguration.DEFAULT_GET_PER_TIMEOUT
        );
        
        return new EnumerationConfiguration(getPerAttemptsNum, getPerTimeout);
    }
    
    private static BondedNodesConfiguration createBondedNodesConfiguration(
            Configuration configuration
    ) {
        
        int processBondedNodes = configuration.getInt("initialization.type.dpa.getBondedNodes", 0);
        if (processBondedNodes == 0) {
            return null;
        }
        
        int getBondedNodesAttemptsNum = configuration.getInt(
                "initialization.type.dpa.getBondedNodes.num_attempts", -1
        );
        
        long getBondeNodesTimeout = configuration.getLong(
                "initialization.type.dpa.getBondedNodes.timeout", -1
        );
        
        // if user explicitly sets to process bonded nodes
        if (processBondedNodes > 0) {
            if (getBondedNodesAttemptsNum == -1) {
                getBondedNodesAttemptsNum = BondedNodesConfiguration.DEFAULT_GET_BONDED_NODES_ATTEMPTS_NUM;
            }
            
            if (getBondeNodesTimeout == -1) {
                getBondeNodesTimeout = BondedNodesConfiguration.DEFAULT_GET_BONDED_NODES_TIMEOUT;
            }
        }
        
        return new BondedNodesConfiguration(
                getBondedNodesAttemptsNum, getBondeNodesTimeout
        );
    }
    
    private static DiscoveryConfiguration createDiscoveryConfiguration(
            Configuration configuration
    ) {
        int doDiscovery = configuration.getInt("initialization.type.dpa.discovery", 0);
        if (doDiscovery == 0) {
            return null;
        }
        
        long discoveryTimeout = configuration.getLong(
                "initialization.type.dpa.discovery.timeout", -1
        );
        
        int discoveryTxPower = configuration.getInt(
                "initialization.type.dpa.discovery.txPower", -1
        );
       
        // if user explicitly sets to do discovery
        if (doDiscovery > 0) {
            if (discoveryTimeout == -1) {
                discoveryTimeout = DiscoveryConfiguration.DEFAULT_DISCOVERY_TIMEOUT;
            }
            
            if (discoveryTxPower == -1) {
                discoveryTxPower = DiscoveryConfiguration.DEFAULT_DISCOVERY_TX_POWER;
            }
        }
        
        return new DiscoveryConfiguration(discoveryTimeout, discoveryTxPower);
    }
    
    /**
     * Returns DPA initializer configuration settings.
     * @param configuration source configuration
     * @return DPA initializer configuration settings
     */
    public static DPA_InitializerConfiguration getDPA_InitializerConfiguration(
            Configuration configuration
    ) {
        EnumerationConfiguration enumConfig = createEnumerationConfiguration(configuration);
        BondedNodesConfiguration bondedNodesConfig = createBondedNodesConfiguration(configuration);
        DiscoveryConfiguration discConfig = createDiscoveryConfiguration(configuration);
        
        return new DPA_InitializerConfiguration.Builder().
                enumerationConfiguration(enumConfig).
                bondedNodesConfiguration(bondedNodesConfig).
                discoveryConfiguration(discConfig).
        build();
    }
    
}
