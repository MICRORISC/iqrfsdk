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

import com.microrisc.simply.connector.Connector;
import com.microrisc.simply.protocol.ProtocolLayer;
import com.microrisc.simply.network.NetworkLayer;

/**
 * Access to connection stack of underlaying networks.
 * 
 * @author Michal Konopa
 */
public interface ConnectionStack {

    /**
     * Returns connector.
     * @return connector
     */
    Connector getConnector();

    /**
     * Returns network layer.
     * @return network layer
     */
    NetworkLayer getNetworkLayer();

    /**
     * Returns protocol layer.
     * @return protocol layer
     */
    ProtocolLayer getProtocolLayer();
    
    /**
     * Starts connection stack.
     * @throws SimplyException if an error has occured during starting process
     */
    void start() throws SimplyException;
    
    /**
     * Destroy connection stack and frees up used resources.
     */
    void destroy();
}
