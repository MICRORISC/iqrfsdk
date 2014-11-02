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

/**
 * Base class of device object factories.
 * 
 * @author Michal Konopa
 * @param <T> type of connector service
 * @param <U> type of configuration settings
 * @param <V> type of Device Object to create
 */
public abstract class AbstractDeviceObjectFactory
<T extends ConnectorService, U extends Object, V extends DeviceObject> {
    /**
     * Returns device object.
     * @param networkId ID of network, which returned device object belongs to.
     * @param nodeId ID of node, which returned device object belongs to.
     * @param implClass Device interface implementation class
     * @param connector connector to use
     * @param configuration configuration settings
     * @return device object
     * @throws Exception if an error has occured
     */
    public abstract V getDeviceObject(
            String networkId, 
            String nodeId, 
            T connector, 
            Class implClass, 
            U configuration
    ) throws Exception;
}
