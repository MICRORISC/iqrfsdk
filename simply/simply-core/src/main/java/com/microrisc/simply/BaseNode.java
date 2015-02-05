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

import java.util.HashMap;
import java.util.Map;

/**
 * Base class of nodes.
 * 
 * @author Michal Konopas
 */
public class BaseNode implements Node {
    /** Unique identifier of this node. */
    protected final String id;
    
    /** Identifier of network ID. */
    protected final String networkId;
    
    /** Map of devicesMap on this node. */
    protected final Map<Class, DeviceObject> devicesMap;
    
    /**
     * Protected constructor.
     * @param networkId Identifier of network, which this node belongs to
     * @param id identifier of this node
     * @param devicesMap map of device objects
     */
    public BaseNode(String networkId, String id, Map<Class, DeviceObject> devicesMap) {
        this.networkId = networkId;
        this.id = id;
        this.devicesMap = new HashMap<Class, DeviceObject>(devicesMap);
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getNetworkId() {
        return networkId;
    }
    
    @Override
    public <T> T getDeviceObject(Class<T> deviceInterface) {
        if (devicesMap.containsKey(deviceInterface)) {
            return (T)devicesMap.get(deviceInterface);
        }
        return null;
    }
    
    @Override
    public Map<Class, DeviceObject> getDeviceObjectsMap() {
        return new HashMap<>(devicesMap);
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof Node)) {
            return false;
        }
        
        Node nodeObj = (Node)obj;
        return (this.id.equals(nodeObj.getId()) 
                && this.networkId.equals(nodeObj.getNetworkId())
        );
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.networkId != null ? this.networkId.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" ID: " + id + NEW_LINE);
        strBuilder.append(" Network ID: " + networkId + NEW_LINE);
        strBuilder.append(" Devices map: " + devicesMap + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
