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
package com.microrisc.simply.iqrf.dpa.v22x.init;

import com.microrisc.simply.DeviceObject;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.services.node.load_code.LoadCodeService;
import com.microrisc.simply.iqrf.dpa.v22x.services.node.load_code.SimpleLoadCodeService;
import java.util.HashMap;
import java.util.Map;

/**
 * Node Services factory.
 * 
 * @author Michal Konopa
 */
public final class NodeServiceFactory {
    // base class for encapsulating of creation of service objects
    private static abstract class ServiceCreator<T> {
        abstract T create(Map<Class, DeviceObject> devices);
    }
    
    
    // load code service creator
    private static class LoadCodeServiceCreator extends ServiceCreator<LoadCodeService> {
        @Override
        LoadCodeService create(Map<Class, DeviceObject> devices) {
            Map<Class, DeviceObject> servDevices = new HashMap<>();

            DeviceObject eeeprom = devices.get(EEEPROM.class);
            if ( eeeprom == null ) {
                return null;
            }
            servDevices.put(EEEPROM.class, eeeprom);

            DeviceObject os = devices.get(OS.class);
            if ( os == null ) {
                return null;
            }
            servDevices.put(OS.class, os);
            
            return new SimpleLoadCodeService(servDevices);
        }
    }
    
    
    private static Map<Class, ServiceCreator> creators = null;
    
    private static void initCreators() {
        creators = new HashMap<>();
        creators.put(LoadCodeService.class, new LoadCodeServiceCreator());
    }
    
    static {
        initCreators();
    }
    
    private static void checkDevices(Map<Class, DeviceObject> devices) {
        if ( devices == null ) {
            throw new IllegalArgumentException("Devices cannot be null.");
        }
    }
    
    /**
     * Creates and returns object of service as specified by {@code service} 
     * argument. If the service object couldn't be created, {@code null} is returned.
     * @param <T> type of service
     * @param service service to create
     * @param devices devices to use in the service
     * @return object of the service <br>
     *         {@code null} if the service object has not been created
     * @throws IllegalArgumentException if {@code devices} is {@code null}
     */
    public static <T> T createService(Class<T> service, Map<Class, DeviceObject> devices) 
    {
        checkDevices(devices);
        
        ServiceCreator<T> creator = creators.get(service);
        if ( creator == null ) {
            return null;
        }
        
        return creator.create(devices);
    }
}
