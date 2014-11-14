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

package com.microrisc.simply.iqrf.dpa.v210.examples.user_peripherals.myled.def;

import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapperFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User peripheral to Device Interfaces mapper.
 * 
 * @author Michal Konopa
 */
public class UserPerToDevIfaceMapperFactory 
implements PeripheralToDevIfaceMapperFactory { 
    
    /**
     * Holds mapping between my peripherals and Device Interfaces.
     */
    private class UserPerToDevIfaceMapper implements PeripheralToDevIfaceMapper {
        private final Map<String, Class> peripheralToIface; 
        private final Map<Class, String> ifaceToPeripheral;
    
    
        private void createMappings() {
            peripheralToIface.put("32", MyLED.class);

            // creating transposition
            for ( Map.Entry<String, Class> entry : peripheralToIface.entrySet() ) {
                ifaceToPeripheral.put(entry.getValue(), entry.getKey());
            }
        }
    
    
        public UserPerToDevIfaceMapper() {
            peripheralToIface = new HashMap<String, Class>();
            ifaceToPeripheral = new HashMap<Class, String>();
            createMappings();
        }
    
        @Override
        public Set<Class> getMappedDeviceInterfaces() {
            return ifaceToPeripheral.keySet();
        }

        @Override   
        public Class getDeviceInterface(String perId) {
            return peripheralToIface.get(perId);
        }

        @Override
        public String getPeripheralId(Class devInterface) {
            return ifaceToPeripheral.get(devInterface);
        }
    }
    
    @Override
    public PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper() throws Exception {
        return new UserPerToDevIfaceMapper();
    }
}
