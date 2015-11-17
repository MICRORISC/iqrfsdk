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

package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.user_map.myadc.def;

import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapperFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User peripheral to Device Interfaces mapper.
 * Note: Used only if it's set in Simply properties reference for this class.
 * In case where is used FileMapper for mapping from File isn't used this class.
 * 
 * @author Michal Konopa
 */
public class UserPerToDevIfaceMapperFactory 
implements PeripheralToDevIfaceMapperFactory { 
    
    /**
     * Holds mapping between my peripherals and Device Interfaces.
     */
    private class UserPerToDevIfaceMapper implements PeripheralToDevIfaceMapper {
        private final Map<Integer, Class> peripheralToIface; 
        private final Map<Class, Integer> ifaceToPeripheral;
    
    
        private void createMappings() {
            peripheralToIface.put(32, MyPotentiometer.class);
            peripheralToIface.put(33, MyPhotoResistor.class);
           
            // creating transposition
            for ( Map.Entry<Integer, Class> entry : peripheralToIface.entrySet() ) {
                ifaceToPeripheral.put(entry.getValue(), entry.getKey());
            }
        }
    
    
        public UserPerToDevIfaceMapper() {
            peripheralToIface = new HashMap<>();
            ifaceToPeripheral = new HashMap<>();
            createMappings();
        }
    
        @Override
        public Set<Class> getMappedDeviceInterfaces() {
            return ifaceToPeripheral.keySet();
        }

        @Override   
        public Class getDeviceInterface(int perId) {
            return peripheralToIface.get(perId);
        }

        @Override
        public Integer getPeripheralId(Class devInterface) {
            return ifaceToPeripheral.get(devInterface);
        }

        @Override
        public Set<Integer> getMappedPeripherals() {
            return peripheralToIface.keySet();
        }
    }
    
    @Override
    public PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper() throws Exception {
        return new UserPerToDevIfaceMapper();
    }
}
