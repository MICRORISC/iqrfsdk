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

package com.microrisc.simply.network;

import com.microrisc.simply.SimplyException;
import com.microrisc.simply.network.comport.BaseCOMPortConnectionInfo;
import com.microrisc.simply.network.spi.BaseSPIPortConnectionInfo;
import com.microrisc.simply.network.udp.BaseUDPConnectionInfo;
import com.microrisc.simply.network.udp.UDPConnectionInfo;
import com.microrisc.simply.utilities.XMLConfigurationMappingReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * Simple implementation of network connection storage factory.
 * <p>
 * Supported connection types: <br>
 * - UDP<br>
 *      Configuration items: <br>
 *          - host: host address <br>
 *          - port: port number <br>
 *      Implementing class: {@link BaseUDPConnectionInfo} <br>
 * - COM, UART: <br>
 *      Configuration items: <br>
 *          - port: port number <br>
 *      Implementing class: {@link BaseCOMPortConnectionInfo} <br>
 * - SPI:
 *      Configuration items: <br>
 *          - port: port number <br>
 *      Implementing class: {@link BaseSPIPortConnectionInfo} <br>
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public final class SimpleNetworkConnectionStorageFactory 
extends AbstractNetworkConnectionStorageFactory<Configuration, NetworkConnectionStorage> 
{    
    /** Creates and returns UDP configuration settings. */
    private UDPConnectionInfo getUDPConnectionInfo(Configuration networkConfig) 
            throws UnknownHostException {
        String hostStr = networkConfig.getString("host");
        InetAddress ipAddress = InetAddress.getByName(hostStr);
        int port = networkConfig.getInt("port");
        return new BaseUDPConnectionInfo(ipAddress, port);
    }
    
    /** Creates and returns COM-port configuration settings. */
    private BaseCOMPortConnectionInfo getCOMConnectionInfo(Configuration networkConfig) 
            throws SimplyException {
        String port = networkConfig.getString("port", "");
        if ( port.equals("") ) {
            throw new SimplyException("COM-port not specified");
        }
        return new BaseCOMPortConnectionInfo(port);
    }

    /** Creates and returns SPI-port configuration settings. */
    private BaseSPIPortConnectionInfo getSPIConnectionInfo(Configuration networkConfig) 
            throws SimplyException {
        String port = networkConfig.getString("port", "");
        if ( port.equals("") ) {
            throw new SimplyException("SPI-port not specified");
        }
        return new BaseSPIPortConnectionInfo(port);
    }
    
    /** 
     * Creates and returns connection info based on type of network connection. 
     */ 
    private AbstractNetworkConnectionInfo getConnectionInfo(String connTypeStr, 
            Configuration networkConfig) throws SimplyException {
        if ( connTypeStr.equals("UDP") ) {
            try {
                return (AbstractNetworkConnectionInfo)getUDPConnectionInfo(networkConfig);
            } catch ( UnknownHostException e ) {
                throw new SimplyException(e);
            }
        }
        
        if ( connTypeStr.equals("COM") || connTypeStr.equals("UART") ) {
            return (AbstractNetworkConnectionInfo)getCOMConnectionInfo(networkConfig);
        }
        
        if ( connTypeStr.equals("SPI") ) {
            return (AbstractNetworkConnectionInfo)getSPIConnectionInfo(networkConfig);
        }
        
        // unknown connection type
        throw new SimplyException("Unknown connection type: " + connTypeStr);
    } 
    
    @Override
    public NetworkConnectionStorage getNetworkConnectionStorage(Configuration configuration)
            throws Exception {
        String connectionTypesFileName = configuration.getString("networkConnectionTypes.configFile");
        
        // creating map of connection types
        Map<String, Configuration> connTypeConfigs = 
                XMLConfigurationMappingReader.getConfigMapping(
                connectionTypesFileName, "connectionType", "name"
        );
        
        String networksSettingsFileName = configuration.getString("networkSettings.configFile");
        
        // creating map of networks configurations
        Map<String, Configuration> networkConfigs = 
                XMLConfigurationMappingReader.getConfigMapping(
                networksSettingsFileName, "network", "id"
        );
        
        Map<String, AbstractNetworkConnectionInfo> idToConnInfoMap = new HashMap<>();
        
        for (Map.Entry<String, Configuration> networkConfig : networkConfigs.entrySet()) {
            String connTypeStr = networkConfig.getValue().getString("type");
            AbstractNetworkConnectionInfo connInfo = getConnectionInfo(connTypeStr, networkConfig.getValue()); 
            idToConnInfoMap.put(networkConfig.getKey(), connInfo);
        }
        
        Map<AbstractNetworkConnectionInfo, String> connInfoToIdMap = new HashMap<>();
        
        // creating transposition
        for ( Map.Entry<String, AbstractNetworkConnectionInfo> entry : idToConnInfoMap.entrySet() ) {
            connInfoToIdMap.put(entry.getValue(), entry.getKey());
        }
        
        return new BaseNetworkConnectionStorage(idToConnInfoMap, connInfoToIdMap);
    }
}
