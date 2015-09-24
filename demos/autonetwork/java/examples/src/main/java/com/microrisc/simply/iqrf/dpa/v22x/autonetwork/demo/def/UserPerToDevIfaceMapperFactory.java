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

package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.demo.def;

import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapperFactory;
<<<<<<< HEAD:demos/autonetwork/java/examples/src/main/java/com/microrisc/simply/iqrf/dpa/v22x/autonetwork/demo/def/UserPerToDevIfaceMapperFactory.java
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.P2PPrebonder;
=======
import com.microrisc.simply.iqrf.dpa.v220.init.NetworksFunctionalityToSimplyMapping;
import com.microrisc.simply.iqrf.dpa.v220.init.NetworksFunctionalityToSimplyMappingParser;
>>>>>>> MyCustom:libs/simply/simply-iqrf-dpa-v220/src/main/java/com/microrisc/simply/iqrf/dpa/v220/protocol/CustomUserPerToDevIfaceMapperFactory.java
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
<<<<<<< HEAD:demos/autonetwork/java/examples/src/main/java/com/microrisc/simply/iqrf/dpa/v22x/autonetwork/demo/def/UserPerToDevIfaceMapperFactory.java
=======
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
>>>>>>> MyCustom:libs/simply/simply-iqrf-dpa-v220/src/main/java/com/microrisc/simply/iqrf/dpa/v220/protocol/CustomUserPerToDevIfaceMapperFactory.java

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
        private final Map<Integer, Class> peripheralToIface; 
        private final Map<Class, Integer> ifaceToPeripheral;
<<<<<<< HEAD:demos/autonetwork/java/examples/src/main/java/com/microrisc/simply/iqrf/dpa/v22x/autonetwork/demo/def/UserPerToDevIfaceMapperFactory.java
    
    
        private void createMappings() {
            peripheralToIface.put(32, P2PPrebonder.class);
=======

        private final Logger logger = LoggerFactory.getLogger(CustomUserPerToDevIfaceMapperFactory.class);
>>>>>>> MyCustom:libs/simply/simply-iqrf-dpa-v220/src/main/java/com/microrisc/simply/iqrf/dpa/v220/protocol/CustomUserPerToDevIfaceMapperFactory.java

        private void createMappings() {
            // creating transposition
            for ( Map.Entry<Integer, Class> entry : peripheralToIface.entrySet() ) {
                ifaceToPeripheral.put(entry.getValue(), entry.getKey());
            }
         
            // mapping determined peripherals
            for (Integer peripheral : usedPeripherals) {
                if (peripheral >= DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_START
                        && peripheral <= DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_END) {
                    peripheralToIface.put(peripheral, MyCustom.class);
                }
            }
        }
<<<<<<< HEAD:demos/autonetwork/java/examples/src/main/java/com/microrisc/simply/iqrf/dpa/v22x/autonetwork/demo/def/UserPerToDevIfaceMapperFactory.java
    
    
=======

        /**
         * Create a new instance of {@link UserPerToDevIfaceMapper} with
         * peripherals used in specified config.
         * <p>
         * @param config must contain used perpiherals which will be mapped
         */
>>>>>>> MyCustom:libs/simply/simply-iqrf-dpa-v220/src/main/java/com/microrisc/simply/iqrf/dpa/v220/protocol/CustomUserPerToDevIfaceMapperFactory.java
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
<<<<<<< HEAD:demos/autonetwork/java/examples/src/main/java/com/microrisc/simply/iqrf/dpa/v22x/autonetwork/demo/def/UserPerToDevIfaceMapperFactory.java
    
=======

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Set<Integer> usedPeripherals = new HashSet<>();

    public CustomUserPerToDevIfaceMapperFactory(Configuration config) {
        if (config == null) {
            new CustomUserPerToDevIfaceMapperFactory();
        }

        String sourceFileName = config.getString(
                "initialization.type.dpa.fixed.sourceFile",
                "PeripheralDistribution.xml"
        );

        // parsing peripherals
        NetworksFunctionalityToSimplyMapping networkFuncMapping = null;
        try {
            networkFuncMapping = NetworksFunctionalityToSimplyMappingParser.parse(sourceFileName);
        } catch (ConfigurationException ex) {
            logger.warn(ex.toString());
            logger.info("Custom peripheral will be mapped only for 0x20.");
            usedPeripherals.add(0x20);
            return;
        }

        // determining only peripherals IDs
        Map<String, Map<String, Set<Integer>>> mapping = networkFuncMapping.getMapping();
        List<Integer> usedPer = new LinkedList<>();
        for (Entry<String, Map<String, Set<Integer>>> networkEntry : mapping.entrySet()) {
            Map<String, Set<Integer>> network = networkEntry.getValue();
            for (Entry<String, Set<Integer>> node : network.entrySet()) {
                usedPer.addAll(node.getValue());
            }
        }

        // mapping determined peripherals
        for (Integer peripheral : usedPer) {
            if (peripheral >= DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_START
                    && peripheral <= DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_END) {
                usedPeripherals.add(peripheral);
            }
        }
    }

    public CustomUserPerToDevIfaceMapperFactory() {
        logger.warn("Configuration wasn't found.");
        logger.info("Custom peripheral will be mapped only for 0x20.");
        usedPeripherals.add(0x20);
    }

>>>>>>> MyCustom:libs/simply/simply-iqrf-dpa-v220/src/main/java/com/microrisc/simply/iqrf/dpa/v220/protocol/CustomUserPerToDevIfaceMapperFactory.java
    @Override
    public PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper() throws Exception {
        return new UserPerToDevIfaceMapper();
    }
}
