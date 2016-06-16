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

package com.microrisc.simply.services.node;

import com.microrisc.simply.DeviceObject;
import com.microrisc.simply.services.Service;
import com.microrisc.simply.services.ServiceParameters;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for Services on Nodes.
 * 
 * @author Michal Konopa
 */
public class BaseService implements Service {
    // Device Objects to use
    protected final Map<Class, DeviceObject> deviceObjects;
    
   /**
     * Returns DO, which implements specified device interface.
     * @param <T> type of device interface
     * @param deviceInterface device interface, which is implemented by returned 
     *                        device object
     * @return Device Object, which implements specified Device Interface.
     */
    protected <T> T getDeviceObject(Class<T> deviceInterface){       
       if ( deviceObjects.containsKey(deviceInterface) ) {
            return (T)deviceObjects.get(deviceInterface);
        }
        return null;
    }
    
    private Map<Class, DeviceObject> checkDeviceObjects(Map<Class, DeviceObject> deviceObjects) 
    {
        if ( deviceObjects == null ) {
            throw new IllegalArgumentException("Device Objects cannot be null.");
        }
        return deviceObjects;
    }
    
    
    /**
     * Creates new service, which uses specified Device Objects.
     * @param deviceObjects Device Objects to use
     * @throws IllegalArgumentException if {@code deviceObjects} is {@code null}
     */
    public BaseService(Map<Class, DeviceObject> deviceObjects) {
        this.deviceObjects = new HashMap<>(checkDeviceObjects(deviceObjects));
    }
    
    /**
     * Does nothing.
     * @param params 
     */
    @Override
    public void setServiceParameters(ServiceParameters params) {
    }
}
