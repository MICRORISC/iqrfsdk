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

package com.microrisc.simply.iqrf.dpa.v22x.protocol;

import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Custom;
import com.microrisc.simply.iqrf.dpa.v22x.init.NetworksFunctionalityToSimplyMapping;
import com.microrisc.simply.iqrf.dpa.v22x.init.NetworksFunctionalityToSimplyMappingParser;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DPA_AdditionalInfoConvertor;
import com.microrisc.simply.iqrf.typeconvertors.ArrayUns8Convertor;
import com.microrisc.simply.iqrf.typeconvertors.PrimArrayUns8Convertor;
import com.microrisc.simply.iqrf.typeconvertors.Uns16Convertor;
import com.microrisc.simply.iqrf.typeconvertors.Uns8Convertor;
import com.microrisc.simply.protocol.mapping.CallRequestToPacketMapping;
import com.microrisc.simply.protocol.mapping.ConstValueToPacketMapping;
import com.microrisc.simply.protocol.mapping.InterfaceToPacketMapping;
import com.microrisc.simply.protocol.mapping.MethodToPacketMapping;
import com.microrisc.simply.protocol.mapping.PacketPositionValues;
import com.microrisc.simply.protocol.mapping.PacketToCallResponseMapping;
import com.microrisc.simply.protocol.mapping.PacketToInterfaceMapping;
import com.microrisc.simply.protocol.mapping.PacketToMethodMapping;
import com.microrisc.simply.protocol.mapping.PacketToValueMapping;
import com.microrisc.simply.protocol.mapping.ProtocolMapping;
import com.microrisc.simply.protocol.mapping.ProtocolMappingFactory;
import com.microrisc.simply.protocol.mapping.SimpleCallRequestToPacketMapping;
import com.microrisc.simply.protocol.mapping.SimplePacketToCallResponseMapping;
import com.microrisc.simply.protocol.mapping.SimpleProtocolMapping;
import com.microrisc.simply.protocol.mapping.ValueToPacketMapping;
import com.microrisc.simply.typeconvertors.StringToByteConvertor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for protocol mapping of my periperals.
 * <p>
 * @author Michal Konopa
 * @author Martin Strouhal
 */
public class CustomUserProtocolMappingFactory implements ProtocolMappingFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Reference to protocol mapping.
     */
    private ProtocolMapping protocolMapping = null;

    // REQUEST MAPPING
    // returns currently empty list of mappings
    private List<ConstValueToPacketMapping> createRequestConstMappings() {
        List<ConstValueToPacketMapping> mappings = new LinkedList<>();
        return mappings;
    }

    // returns empty list of mappings - more networks capability is not currently used
    private List<ValueToPacketMapping> createRequestNetworkMappings() {
        List<ValueToPacketMapping> mappings = new LinkedList<>();
        return mappings;
    }

    private List<ValueToPacketMapping> createRequestNodeMappings() {
        List<ValueToPacketMapping> mappings = new LinkedList<>();
        ValueToPacketMapping nodeMapping = new ValueToPacketMapping(0,
                StringToByteConvertor.getInstance()
        );
        mappings.add(nodeMapping);
        return mappings;
    }

    // MyCustom interface
    private MethodToPacketMapping createSendMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(2, Uns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(3, Uns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    private InterfaceToPacketMapping createRequestMyCustomMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("0", createSendMapping());
        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    /**
     * Creates map of mapping of Device Interfaces into protocol packets.
     * <p>
     * @return
     */
    private Map<Class, InterfaceToPacketMapping> createRequestIfaceMappings() {
        Map<Class, InterfaceToPacketMapping> mappings = new HashMap<>();

        // creating interface mappings
        mappings.put(Custom.class, createRequestMyCustomMapping());
        return mappings;
    }

    private CallRequestToPacketMapping createCallRequestToPacketMapping() {
        List<ConstValueToPacketMapping> constMappings = createRequestConstMappings();
        List<ValueToPacketMapping> networkMappings = createRequestNetworkMappings();
        List<ValueToPacketMapping> nodeMappings = createRequestNodeMappings();
        Map<Class, InterfaceToPacketMapping> ifaceMappings = createRequestIfaceMappings();

        return new SimpleCallRequestToPacketMapping(constMappings,
                networkMappings, nodeMappings, ifaceMappings
        );
    }

    // RESPONSES MAPPING
    private PacketToValueMapping createResponseNetworkMapping() {
        return new PacketToValueMapping(0, 0, StringToByteConvertor.getInstance());
    }

    private PacketToValueMapping createResponseNodeMapping() {
        return new PacketToValueMapping(0, 1, StringToByteConvertor.getInstance());
    }

    // MyCustom
    private PacketToMethodMapping createResponseSend() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        List<Short> possibleResponseId = new LinkedList<>();

        // Extend the range for async packets - can be either dpa requests or responses 
        for (Integer i = 0; i < 255; i++) {
            possibleResponseId.add(i.shortValue());
        }
        packetValues.add(new PacketPositionValues(DPA_ProtocolProperties.PCMD_START, possibleResponseId));

        PacketToValueMapping resultMapping = new PacketToValueMapping(8, PrimArrayUns8Convertor.getInstance());
        return new PacketToMethodMapping("0", packetValues, resultMapping);
    }

    private PacketToInterfaceMapping createResponseMyCustomMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();

        List<Short> possiblePNumIds = new LinkedList<>();
        possiblePNumIds.addAll(usedPeripherals);
        packetValues.add(new PacketPositionValues(DPA_ProtocolProperties.PNUM_START, possiblePNumIds));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("0", createResponseSend());

        return new PacketToInterfaceMapping(Custom.class, packetValues, methodMappings);
    }

    // creating response mapping for Device Interfaces
    private Map<Class, PacketToInterfaceMapping> createResponseIfaceMappings() {
        Map<Class, PacketToInterfaceMapping> mappings = new HashMap<>();

        // creating interface mappings
        mappings.put(Custom.class, createResponseMyCustomMapping());

        return mappings;
    }

    private PacketToValueMapping createAdditionalDataMapping() {
        return new PacketToValueMapping(4, DPA_AdditionalInfoConvertor.getInstance());
    }

    private PacketToCallResponseMapping createPacketToCallResponseMapping() {
        PacketToValueMapping networkMapping = createResponseNetworkMapping();
        PacketToValueMapping nodeMapping = createResponseNodeMapping();
        Map<Class, PacketToInterfaceMapping> ifaceMappings = createResponseIfaceMappings();
        PacketToValueMapping additionalDataMapping = createAdditionalDataMapping();

        return new SimplePacketToCallResponseMapping(
                networkMapping, nodeMapping, ifaceMappings, additionalDataMapping
        );
    }

    private final Set<Short> usedPeripherals = new HashSet<>();

    /**
     * Create a new instance of {@link CustomUserProtocolMappingFactory} with
     * peripherals used in specified config.
     * <p>
     * @param config must contain used perpiherals which will be mapped
     */
    public CustomUserProtocolMappingFactory(Configuration config) {
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
            usedPeripherals.add((short) 0x20);
            return;
        }

        // determining only peripherals IDs
        Map<String, Map<String, Set<Integer>>> mapping = networkFuncMapping.getMapping();
        List<Integer> usedPerOnAllNodes = new LinkedList<>();
        for (Map.Entry<String, Map<String, Set<Integer>>> networkEntry : mapping.entrySet()) {
            Map<String, Set<Integer>> network = networkEntry.getValue();
            for (Map.Entry<String, Set<Integer>> node : network.entrySet()) {
                usedPerOnAllNodes.addAll(node.getValue());
            }
        }

        // mapping determined peripherals
        for (Integer peripheral : usedPerOnAllNodes) {
            if (peripheral >= DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_START
                    && peripheral <= DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_END) {
                usedPeripherals.add(peripheral.shortValue());
            }
        }
    }

    @Override
    public ProtocolMapping createProtocolMapping() throws Exception {
        if (protocolMapping != null) {
            return protocolMapping;
        }

        CallRequestToPacketMapping callRequestToPacketMapping
                = createCallRequestToPacketMapping();

        PacketToCallResponseMapping packetToCallResponseMapping
                = createPacketToCallResponseMapping();

        protocolMapping = new SimpleProtocolMapping(callRequestToPacketMapping,
                packetToCallResponseMapping
        );
        return protocolMapping;
    }
}
