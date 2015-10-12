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

package com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v22x.devices.GeneralLED;
import com.microrisc.simply.iqrf.dpa.v22x.devices.GeneralMemory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.IO;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDR;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Node;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.devices.PWM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.PeripheralInfoGetter;
import com.microrisc.simply.iqrf.dpa.v22x.devices.RAM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.SPI;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Thermometer;
import com.microrisc.simply.iqrf.dpa.v22x.devices.UART;
import java.util.HashMap;
import java.util.Map;

/**
 * Access to Standard method ID transformers. 
 * 
 * @author Michal Konopa
 */
public final class StandardMethodIdTransformers {
    private final Map<Class, MethodIdTransformer> transformers;
    
    /**
     * Private constructor.
     */
    private StandardMethodIdTransformers() {
        transformers = new HashMap<>();
        addTransformers();
    }
    
    /** Singleton. */
    private static final StandardMethodIdTransformers instance = 
            new StandardMethodIdTransformers();
    
    
    /**
     * @return StandardMethodIdTransformers instance
     */
    static public StandardMethodIdTransformers getInstance() {
        return instance;
    }
    
    private void addTransformers() {
        transformers.put(Coordinator.class, CoordinatorStandardTransformer.getInstance());
        transformers.put(EEEPROM.class, EEEPROMStandardTransformer.getInstance());
        transformers.put(FRC.class, FRCStandardTransformer.getInstance());
        transformers.put(GeneralLED.class, GeneralLEDStandardTransformer.getInstance());
        transformers.put(LEDR.class, GeneralLEDStandardTransformer.getInstance());
        transformers.put(LEDG.class, GeneralLEDStandardTransformer.getInstance());
        transformers.put(GeneralMemory.class, GeneralMemoryStandardTransformer.getInstance());
        transformers.put(EEPROM.class, GeneralMemoryStandardTransformer.getInstance());
        transformers.put(RAM.class, GeneralMemoryStandardTransformer.getInstance());
        transformers.put(IO.class, IOStandardTransformer.getInstance());
        transformers.put(Node.class, NodeStandardTransformer.getInstance());
        transformers.put(OS.class, OSStandardTransformer.getInstance());
        transformers.put(PWM.class, PWMStandardTransformer.getInstance());
        transformers.put(PeripheralInfoGetter.class, PeripheralInfoGetterStandardTransformer.getInstance());
        transformers.put(SPI.class, SPIStandardTransformer.getInstance());
        transformers.put(Thermometer.class, ThermometerStandardTransformer.getInstance());
        transformers.put(UART.class, UARTStandardTransformer.getInstance());
    }
    
    
    
    /**
     * Returns method ID transformer for specified device interface.
     * @param devIface device interface, which to return the transformer for
     * @return method ID transformer for specified device interface.
     */
    public MethodIdTransformer getTransformer(Class devIface) {
        return transformers.get(devIface);
    }
    
 }
