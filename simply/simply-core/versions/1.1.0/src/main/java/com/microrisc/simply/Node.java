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

import java.util.Map;

/**
 * Encapsulates access to underlaying network node. Each node has devices, which are 
 * accessed via DeviceObject objects - each {@code DeviceObject} instance provides access
 * to concrete device on the node.
 * 
 * @author Michal Konopa
 */
public interface Node {
    /**
     * Returns node identifier.
     * @return node identifier.
     */
    String getId();
    
    /**
     * Returns identifier of network, which this node belongs to.
     * @return identifier of network, which this node belongs to.
     */
    String getNetworkId();
    
    /**
     * Returns DO, which implements specified device interface.
     * @param <T> type of device interface
     * @param deviceInterface device interface, which is implemented by returned 
     *                        device object
     * @return Device Object, which implements specified Device Interface.
     */
    <T> T getDeviceObject(Class<T> deviceInterface);
    
    /**
     * Returns mapping of device interfaces into its implementing device objects.
     * @return mapping of device interfaces into its implementing device objects.
     */
    Map<Class, DeviceObject> getDeviceObjectsMap();
       
}
