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

package com.microrisc.simply.network.serial.v2;

import com.microrisc.simply.SimplyException;
import com.microrisc.simply.network.AbstractNetworkConnectionInfo;
import com.microrisc.simply.network.NetworkLayer;
import com.microrisc.simply.network.AbstractNetworkLayerFactory;
import com.microrisc.simply.network.NetworkConnectionStorage;
import com.microrisc.simply.network.comport.COMPortConnectionInfo;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * Serial factory for creation of network layers, which are bound to serial port.
 * 
 * Configuration key: <b>networkLayer.type.serial.port</b>. If no such configuration
 * key is found, "auto"( which means autoconfiguration ) is used as default.
 * 
 * <p>
 * <b>Autoconfiguration</b> is the process, during which a COM-port to use is deduced
 * from a Network Connection Storage's information by this way: <br>
 * - if there aren't any Connection Info records in the Network Connection Storage,
 *   autoconfiguration ends up with an error <br>
 * - if the Network Connection Storage contains some Connection Info records, which
 *    relates to other type of connections then COM-port is, autoconfiguration 
 *    ends up with an error <br>
 * - if the Network Connection Storage contains more Connection Info records, which
 *   relate to different COM-ports, autoconfiguration ends up with an error <br>
 * - if any of the preceding cases comes, then the COM-port is obtained from some
 *   of the Connection Info records in the Network Connection Storage
 * 
 * 
 * Configuration key: <b>networkLayer.type.serial.baudrate</b>. If no such configuration
 * key is found, 19200 is used as default.
 * 
 * 
 * @author Rostislav Spinar
 */
public final class SerialNetworkLayerFactory 
extends AbstractNetworkLayerFactory<Configuration, NetworkLayer> {
    
    // network layer parameters
    private class NetworkLayerParams {
        NetworkConnectionStorage connectionStorage;
        String portName;
        int serialBaudrate;
        
        NetworkLayerParams(
                NetworkConnectionStorage connectionStorage, String portName, 
                int serialBaudrate
        ) { 
            this.connectionStorage = connectionStorage;
            this.portName = portName;
            this.serialBaudrate = serialBaudrate;
        }
    }
    
    /** Denotes to use autoconfiguration to obtain COM-port name. */
    private static final String AUTOCONF = "auto";
    
    // checks Connection Storage if it is suitable for autoconfiguration and if
    // so, returns serial-port number
    private String getAutoconfiguredPortName( NetworkConnectionStorage connStorage ) {
        Map<String, AbstractNetworkConnectionInfo> allConnections = 
                connStorage.getAllNetworkConnectionInfo();
        String lastPortName = null;
        for ( AbstractNetworkConnectionInfo connInfo : allConnections.values() ) {
            if ( !(connInfo instanceof COMPortConnectionInfo) ) {
                throw new IllegalArgumentException("Invalid type of connection information");
            }
            
            COMPortConnectionInfo comportConnInfo = (COMPortConnectionInfo) connInfo;
            if ( lastPortName == null ) {
                lastPortName = comportConnInfo.getCOMPortName();
                continue;
            }
            
            if ( !comportConnInfo.getCOMPortName().equals(lastPortName) ) {
                throw new IllegalArgumentException(
                        "COM-ports mismatch: " + lastPortName + " vs. " + comportConnInfo.getCOMPortName() 
                );
            }
        }
        
        if ( lastPortName == null ) {
            throw new IllegalArgumentException("No COM-port specification found");
        }
        return lastPortName; 
    }
    
    /**
     * Creates network layer parameters encapsulation object.
     * @param connectionStorage
     * @param configProps
     * @return network layer parameters encapsulation object
     */
    private NetworkLayerParams createNetworkLayerParams(
            NetworkConnectionStorage connectionStorage, Configuration configProps
    ) {
        String portName = configProps.getString("networkLayer.type.serial.port", AUTOCONF);
        int serialBaudrate = configProps.getInt("networkLayer.type.serial.baudrate", 19200);
        return new NetworkLayerParams(connectionStorage, portName, serialBaudrate);
    }
    
    /**
     * Creates serial network layer - according to specified network layer
     * parameters and version.
     * @param networkParams network layer parameters
     * @return serial network layer
     */
    private SerialNetworkLayerJssc createSerialNetworkLayer(NetworkLayerParams networkParams) 
            throws Exception {
        String portName = networkParams.portName;
        if ( portName.equals(AUTOCONF) ) {
            portName = getAutoconfiguredPortName( networkParams.connectionStorage );
        }
        
        return new SerialNetworkLayerJssc(
                        networkParams.connectionStorage,
                        portName,
                        networkParams.serialBaudrate
        );
    }
    
    @Override
    public NetworkLayer getNetworkLayer(
            NetworkConnectionStorage connectionStorage, Configuration configProps
    ) throws Exception {
        String networkLayerType = configProps.getString("networkLayer.type");
        
        // only for "serial" layer type
        if ( !networkLayerType.equals("serial")) {
            throw new SimplyException("Network layer must be of 'serial' type.");
        }
        
        NetworkLayerParams networkParams = createNetworkLayerParams(connectionStorage, configProps);
        return createSerialNetworkLayer(networkParams);
    }
}
