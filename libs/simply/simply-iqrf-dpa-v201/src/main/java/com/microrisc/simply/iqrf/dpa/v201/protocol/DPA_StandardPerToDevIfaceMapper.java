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

package com.microrisc.simply.iqrf.dpa.v201.protocol;

import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import com.microrisc.simply.iqrf.dpa.v201.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v201.devices.EEEPROM;
import com.microrisc.simply.iqrf.dpa.v201.devices.EEPROM;
import com.microrisc.simply.iqrf.dpa.v201.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v201.devices.IO;
import com.microrisc.simply.iqrf.dpa.v201.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v201.devices.LEDR;
import com.microrisc.simply.iqrf.dpa.v201.devices.Node;
import com.microrisc.simply.iqrf.dpa.v201.devices.OS;
import com.microrisc.simply.iqrf.dpa.v201.devices.PWM;
import com.microrisc.simply.iqrf.dpa.v201.devices.RAM;
import com.microrisc.simply.iqrf.dpa.v201.devices.SPI;
import com.microrisc.simply.iqrf.dpa.v201.devices.Thermometer;
import com.microrisc.simply.iqrf.dpa.v201.devices.UART;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Holds mapping between standard DPA peripherals and Device Interfaces.
 * 
 * @author Michal Konopa
 */
class DPA_StandardPerToDevIfaceMapper implements PeripheralToDevIfaceMapper {
    private final Map<String, Class> peripheralToIface; 
    private final Map<Class, String> ifaceToPeripheral;
    
    
    private void createMappings() {
        peripheralToIface.put("0", Coordinator.class);
        peripheralToIface.put("1", Node.class);
        peripheralToIface.put("2", OS.class);
        peripheralToIface.put("3", EEPROM.class);
        peripheralToIface.put("4", EEEPROM.class);
        peripheralToIface.put("5", RAM.class);
        peripheralToIface.put("6", LEDR.class);
        peripheralToIface.put("7", LEDG.class);
        peripheralToIface.put("8", SPI.class);
        peripheralToIface.put("9", IO.class);
        peripheralToIface.put("10", Thermometer.class);
        peripheralToIface.put("11", PWM.class);
        peripheralToIface.put("12", UART.class);
        peripheralToIface.put("13", FRC.class);
        
        // creating transposition
        for ( Map.Entry<String, Class> entry : peripheralToIface.entrySet() ) {
            ifaceToPeripheral.put(entry.getValue(), entry.getKey());
        }
    }
    
    /**
     * Creates new mappings between standard DPA peripherals and Device Interfaces.
     */
    public DPA_StandardPerToDevIfaceMapper() {
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
