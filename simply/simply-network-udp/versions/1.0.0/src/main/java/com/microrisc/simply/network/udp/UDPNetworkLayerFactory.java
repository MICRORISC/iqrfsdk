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

package com.microrisc.simply.network.udp;

import com.microrisc.simply.SimplyException;
import com.microrisc.simply.network.NetworkLayer;
import com.microrisc.simply.network.AbstractNetworkLayerFactory;
import com.microrisc.simply.network.NetworkConnectionStorage;
import org.apache.commons.configuration.Configuration;

/**
 * Factory for creation of network layers, which are bound to UDP GW-ETH.
 * <p>
 * Two types of UDP network layer is supported: <br>
 * 1. bound to single network: supports only one specified network <br>
 * 2. multinetwork: is able to work with arbitrary UDP network <br>
 * 
 * If user want to create type 1 of network, it must correctly specify <b>remote address</b> 
 * and <b>remote port</b> through configuration settings: <br>
 * <b>networkLayer.type.udp.remoteaddress</b> and <b>networkLayer.type.udp.remoteport</b>.
 * If <b>neither</b> of the two settings are specified, type 2 of network layer is created.
 * 
 * <p>
 * Particular network layer version to create is specified by configuration key:
 * <b>networkLayer.type.udp.version</b>. If no such key is present in configuration
 * properties, version of "01" is assumed.
 * 
 * @author Michal Konopa
 */
public class UDPNetworkLayerFactory 
extends AbstractNetworkLayerFactory<Configuration, NetworkLayer> {
    
    /**
     * Types of network layers.
     */
    private enum NetworkLayerType {
        CLIENT_SINGLE,
        CLIENT_MULTI
    }
    
    // network layer parameters
    private class NetworkLayerParams {
        NetworkConnectionStorage connectionStorage;
        String localAddress;
        int localPort;
        String remoteAddress;
        int remotePort;
        int maxRecvPacketSize;
        int receptionTimeout;
        
        NetworkLayerParams(NetworkConnectionStorage connectionStorage, 
                String localAddress, int localPort, String remoteAddress,
                int remotePort, int maxRecvPacketSize, int receptionTimeout) {
            this.connectionStorage = connectionStorage;
            this.localAddress = localAddress;
            this.localPort = localPort;
            this.remoteAddress = remoteAddress;
            this.remotePort = remotePort;
            this.maxRecvPacketSize = maxRecvPacketSize;
            this.receptionTimeout = receptionTimeout;
        }
    }
    
    /**
     * Creates network layer parameters encapsulation object.
     * @param connectionStorage
     * @param configuration
     * @return network layer parameters encapsulation object
     */
    private NetworkLayerParams createNetworkLayerParams(
            NetworkConnectionStorage connectionStorage, Configuration configuration
    ) {
        String localAddress = configuration.getString("networkLayer.type.udp.localaddress", "");
        int localPort = configuration.getInt("networkLayer.type.udp.localport", -1);
        String remoteAddress = configuration.getString("networkLayer.type.udp.remoteaddress", "");
        int remotePort = configuration.getInt("networkLayer.type.udp.remoteport", -1);

        int maxRecvPacketSize = configuration.getInt("networkLayer.type.udp.maxRecvPacketSize", 
                UDPNetworkLayerMultinet.MAX_RECEIVED_PACKET_SIZE
        );
        int receptionTimeout = configuration.getInt("networkLayer.type.udp.receptionTimeout", 
                UDPNetworkLayer.RECEPTION_TIMEOUT_DEFAULT
        );
        
        return new NetworkLayerParams(connectionStorage, localAddress, localPort, 
                remoteAddress, remotePort, maxRecvPacketSize, receptionTimeout
        );
    }
    
    /**
     * @return type of UDP network layer 
     */
    private NetworkLayerType getNetworkLayerType(Configuration configuration) 
            throws Exception {
        String networkLayerTypeStr = configuration.getString("networkLayer.type", "");
        if (networkLayerTypeStr.equals("")) {
            throw new Exception("Network layer type not specified");
        }
        
        // only for "udp" layer type
        if ( !networkLayerTypeStr.equals("udp")) {
            throw new SimplyException("Network layer must be of 'udp' type.");
        }
        
        String remoteAddress = configuration.getString("networkLayer.type.udp.remoteaddress", "");
        int remotePort = configuration.getInt("networkLayer.type.udp.remoteport", -1);
        
        if (remoteAddress.equals("")) {
            if (remotePort == -1) {
                return NetworkLayerType.CLIENT_MULTI;
            }
        } else {
            if (remotePort != -1) {
                return NetworkLayerType.CLIENT_SINGLE;
            }
        }
        
        throw new Exception("Must be specified both remote address and remote port, or"
                + "neither of the both");
    }
    
    private UDPNetworkLayer createClientSingleNetworkLayer(
            NetworkConnectionStorage connectionStorage, Configuration configuration
    ) throws Exception {
        NetworkLayerParams networkParams = createNetworkLayerParams(connectionStorage, configuration); 
        // if not set, "01" is default
        String version = configuration.getString("networkLayer.type.udp.version", "01");
        if ( !version.equals("01") ) {
            throw new Exception("Unsupported network layer version: " + version);
        }
        
        return new UDPNetworkLayer(
                        networkParams.connectionStorage,
                        networkParams.localAddress,
                        networkParams.localPort,
                        networkParams.remoteAddress,
                        networkParams.remotePort,
                        networkParams.maxRecvPacketSize, 
                        networkParams.receptionTimeout
        );
    }
    
    private UDPNetworkLayerMultinet createClientMultiNetworkLayer(
            NetworkConnectionStorage connectionStorage, Configuration configuration
    ) throws Exception {
        NetworkLayerParams networkParams = createNetworkLayerParams(connectionStorage, configuration); 
        // if not set, "01" is default
        String version = configuration.getString("networkLayer.type.udp.version", "01");
        if ( !version.equals("01") ) {
            throw new Exception("Unsupported network layer version: " + version);
        }
        
        return new UDPNetworkLayerMultinet(
                        networkParams.connectionStorage,
                        networkParams.localAddress,
                        networkParams.localPort,
                        networkParams.maxRecvPacketSize, 
                        networkParams.receptionTimeout
        );
    }
    
    @Override
    public NetworkLayer getNetworkLayer(NetworkConnectionStorage connectionStorage, 
            Configuration configuration) throws Exception {
        NetworkLayerType networkLayerType = getNetworkLayerType(configuration);
        switch (networkLayerType) {
            case CLIENT_SINGLE:
                return createClientSingleNetworkLayer(connectionStorage, configuration);
            case CLIENT_MULTI:
                return createClientMultiNetworkLayer(connectionStorage, configuration);
        }
        
        throw new Exception("Unsupported network layer type: " + networkLayerType);
    }
    
}

