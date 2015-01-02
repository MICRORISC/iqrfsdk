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

package com.microrisc.simply.connector;

import com.microrisc.simply.ProtocolLayerService;

/**
 * Base class for connectors factories.
 * 
 * @author Michal Konopa
 * @param <T> type of protocol layer service
 * @param <U> type of configuration
 * @param <V> type of connector to create
 */
public abstract class AbstractConnectorFactory
<T extends ProtocolLayerService, U extends Object, V extends Connector> {
    /**
     * Returns connector implementation.
     * @param protocolLayer protocol layer to use
     * @param configuration configuration of connector
     * @return network layer implementation
     * @throws Exception if an error has occured
     */
    public abstract V getConnector(
            T protocolLayer, 
            U configuration
    ) throws Exception;
}
