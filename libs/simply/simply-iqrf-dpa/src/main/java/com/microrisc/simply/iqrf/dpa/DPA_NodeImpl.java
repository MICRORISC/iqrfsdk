/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa;

import com.microrisc.simply.BaseNode;
import com.microrisc.simply.DeviceObject;
import com.microrisc.simply.services.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of DPA node.
 * 
 * @author Michal Konopa
 */
public final class DPA_NodeImpl 
extends BaseNode implements DPA_Node
{
    /** Map of services. */
    private final Map<Class, Service> servicesMap;
    
    
    /**
     * Creates new DPA Node without services.
     *
     * @param networkId Identifier of network, which this node belongs to
     * @param id identifier of this node
     * @param devicesMap map of device objects
     */
    public DPA_NodeImpl(String networkId, String id, Map<Class, DeviceObject> devicesMap) 
    {
        super(networkId, id, devicesMap);
        this.servicesMap = new HashMap<>();
    }
    
    /**
     * Creates new DPA Node with specified services.
     *
     * @param networkId Identifier of network, which this node belongs to
     * @param id identifier of this node
     * @param devicesMap map of device objects
     * @param servicesMap services map to provide for user code
     */
    public DPA_NodeImpl(
            String networkId, String id, Map<Class, DeviceObject> devicesMap, 
            Map<Class, Service> servicesMap) 
    {
        super(networkId, id, devicesMap);
        this.servicesMap = new HashMap<>(servicesMap);
    }

    @Override
    public <T> T getService(Class<T> service) {
        if ( servicesMap.containsKey(service)) {
            return (T)servicesMap.get(service);
        }
        return null;
    }

    @Override
    public Map<Class, Service> getServicesMap() {
        return new HashMap<>(servicesMap);
    }
}
