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
package com.microrisc.simply.iqrf.dpa.v220.protocol;

import com.microrisc.simply.iqrf.dpa.v220.devices.MyCustom;
import com.microrisc.simply.iqrf.dpa.v220.typeconvertors.DPA_AdditionalInfoConvertor;
import com.microrisc.simply.iqrf.typeconvertors.ArrayUns8Convertor;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * Factory for protocol mapping of my periperals.
 * <p>
 * @author Michal Konopa
 * @author Martin Strouhal
 */
public class CustomUserProtocolMappingFactory implements ProtocolMappingFactory {

    /**
     * Reference to protocol mapping.
     */
    private ProtocolMapping protocolMapping = null;

    // REQUEST MAPPING
    // returns currently empty list of mappings
    static private List<ConstValueToPacketMapping> createRequestConstMappings() {
        List<ConstValueToPacketMapping> mappings = new LinkedList<>();
        return mappings;
    }

    // returns empty list of mappings - more networks capability is not currently used
    static private List<ValueToPacketMapping> createRequestNetworkMappings() {
        List<ValueToPacketMapping> mappings = new LinkedList<>();
        return mappings;
    }

    static private List<ValueToPacketMapping> createRequestNodeMappings() {
        List<ValueToPacketMapping> mappings = new LinkedList<>();
        ValueToPacketMapping nodeMapping = new ValueToPacketMapping(0,
                StringToByteConvertor.getInstance()
        );
        mappings.add(nodeMapping);
        return mappings;
    }

    // MyCustom interface
    static private MethodToPacketMapping createSendMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(2, Uns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(3, Uns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestMyCustomMapping() {
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
    static private Map<Class, InterfaceToPacketMapping> createRequestIfaceMappings() {
        Map<Class, InterfaceToPacketMapping> mappings = new HashMap<>();

        // creating interface mappings
        mappings.put(MyCustom.class, createRequestMyCustomMapping());
        return mappings;
    }

    static private CallRequestToPacketMapping createCallRequestToPacketMapping() {
        List<ConstValueToPacketMapping> constMappings = createRequestConstMappings();
        List<ValueToPacketMapping> networkMappings = createRequestNetworkMappings();
        List<ValueToPacketMapping> nodeMappings = createRequestNodeMappings();
        Map<Class, InterfaceToPacketMapping> ifaceMappings = createRequestIfaceMappings();

        return new SimpleCallRequestToPacketMapping(constMappings,
                networkMappings, nodeMappings, ifaceMappings
        );
    }

    // RESPONSES MAPPING
    static private PacketToValueMapping createResponseNetworkMapping() {
        return new PacketToValueMapping(0, 0, StringToByteConvertor.getInstance());
    }

    static private PacketToValueMapping createResponseNodeMapping() {
        return new PacketToValueMapping(0, 1, StringToByteConvertor.getInstance());
    }

    // MyCustom
    static private PacketToMethodMapping createResponseSend() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        List<Short> possibleResponseId = new LinkedList<>();
        for (int i = DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_START;
                i <= DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_END; i++) {
            possibleResponseId.add((short) i);
        }

        packetValues.add(new PacketPositionValues(DPA_ProtocolProperties.PCMD_START, possibleResponseId));

        PacketToValueMapping resultMapping = new PacketToValueMapping(8, ArrayUns8Convertor.getInstance());
        return new PacketToMethodMapping("0", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseMyCustomMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        List<Short> possiblePNumIds = new LinkedList<>();
        for (short i = DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_START;
                i <= DPA_ProtocolProperties.PNUM_Properties.USER_PERIPHERAL_END; i++) {
            possiblePNumIds.add(i);
        }

        packetValues.add(new PacketPositionValues(DPA_ProtocolProperties.PNUM_START, possiblePNumIds));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("0", createResponseSend());

        return new PacketToInterfaceMapping(MyCustom.class, packetValues, methodMappings);
    }

    // creating response mapping for Device Interfaces
    static private Map<Class, PacketToInterfaceMapping> createResponseIfaceMappings() {
        Map<Class, PacketToInterfaceMapping> mappings = new HashMap<>();

        // creating interface mappings
        mappings.put(MyCustom.class, createResponseMyCustomMapping());

        return mappings;
    }

    static private PacketToValueMapping createAdditionalDataMapping() {
        return new PacketToValueMapping(4, DPA_AdditionalInfoConvertor.getInstance());
    }

    static private PacketToCallResponseMapping createPacketToCallResponseMapping() {
        PacketToValueMapping networkMapping = createResponseNetworkMapping();
        PacketToValueMapping nodeMapping = createResponseNodeMapping();
        Map<Class, PacketToInterfaceMapping> ifaceMappings = createResponseIfaceMappings();
        PacketToValueMapping additionalDataMapping = createAdditionalDataMapping();

        return new SimplePacketToCallResponseMapping(
                networkMapping, nodeMapping, ifaceMappings, additionalDataMapping
        );
    }

    private final Configuration config;

    /**
     * Create a new instance of {@link CustomUserProtocolMappingFactory} with peripherals
     * used in specified config.
     * <p>
     * @param config must contain used perpiherals which will be mapped
     */
    public CustomUserProtocolMappingFactory(Configuration config) {
        this.config = config;
    }

    //TODO použít config
    
    @Override
    public ProtocolMapping createProtocolMapping() throws Exception {
        if (protocolMapping != null) {
            return protocolMapping;
        }
        if (config == null) {
            throw new Exception("It must be called method for assign Configuration first!");
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
