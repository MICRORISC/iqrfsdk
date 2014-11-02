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

package com.microrisc.simply;

import com.microrisc.simply.ConnectionStack;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.protocol.ProtocolLayer;
import com.microrisc.simply.network.NetworkLayer;
import com.microrisc.simply.connector.Connector;

/**
 * Simple implementation of connection stack. It has 3 main parts:
 * - connector
 * - protocol layer
 * - network layer
 * 
 * @author Michal Konopa
 */
public final class SimpleConnectionStack implements ConnectionStack {
    /** Network Layer. */
    private final NetworkLayer networkLayer;
    
    /** Protocol Layer. */
    private final ProtocolLayer protocolLayer;
    
    /** Connector. */
    private final Connector connector;
    
    
    /**
     * Creates new connection stack.
     * @param networkLayer network layer
     * @param protoLayer protocol layer
     * @param connector connector
     */
    public SimpleConnectionStack(NetworkLayer networkLayer, ProtocolLayer protoLayer, 
            Connector connector
    ) {
        this.networkLayer = networkLayer;
        this.protocolLayer = protoLayer;
        this.connector = connector;
    }

    /**
     * @return the Network Layer
     */
    @Override
    public NetworkLayer getNetworkLayer() {
        return networkLayer;
    }

    /**
     * @return the Protocol Layer
     */
    @Override
    public ProtocolLayer getProtocolLayer() {
        return protocolLayer;
    }

    /**
     * @return the connector
     */
    @Override
    public Connector getConnector() {
        return connector;
    }
    
    @Override
    public void start() throws SimplyException {
        networkLayer.start();
        protocolLayer.start();
        connector.start();
    }
    
    @Override
    public void destroy() {
        connector.destroy();
        protocolLayer.destroy();
        networkLayer.destroy();
    }
}
