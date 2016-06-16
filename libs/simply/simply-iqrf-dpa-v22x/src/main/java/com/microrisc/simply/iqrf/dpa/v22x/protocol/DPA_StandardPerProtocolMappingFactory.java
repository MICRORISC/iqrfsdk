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
package com.microrisc.simply.iqrf.dpa.v22x.protocol;

import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.FRC;
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
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.AddressingInfoConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.ArrayIO_CommandConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.ArrayIO_DirectionSettingsConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.ArrayPeripheralInfoConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.BatchCommandConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.BaudRateConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.BondedDeviceConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.BondedNodesConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DPA_AdditionalInfoConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DPA_ParameterConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DiscoveredNodesConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DiscoveryParamsConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DiscoveryResultConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.FRC_CommandConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.FRC_ConfigurationConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.FRC_DataConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.FRC_SelectCommandConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.HWP_ConfigurationByteConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.HWP_ConfigurationConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.LED_StateConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.LoadingCodePropertiesConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.LoadingResultConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.NodeStatusInfoConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.OsInfoConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.PWM_ParametersConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.PeripheralEnumerationConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.PeripheralInfoConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.RemotelyBondedModuleIdConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.RoutingHopsConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.SleepInfoConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.ThermometerValueConvertor;
import com.microrisc.simply.iqrf.typeconvertors.ArrayUns8Convertor;
import com.microrisc.simply.iqrf.typeconvertors.IntToUns8Convertor;
import com.microrisc.simply.iqrf.typeconvertors.PrimArrayUns8Convertor;
import com.microrisc.simply.iqrf.typeconvertors.Uns16Convertor;
import com.microrisc.simply.iqrf.typeconvertors.VoidTypeConvertor;
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

/**
 * Factory for protocol mapping of standard DPA periperals.
 *
 * @author Michal Konopa
 */
public final class DPA_StandardPerProtocolMappingFactory implements ProtocolMappingFactory {

    /** Reference to protocol mapping. */
    private ProtocolMapping protocolMapping = null;

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

    // Peripheral info getter
    static private MethodToPacketMapping createPerEnumerationMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(2, new short[]{0xFF, 0x3F}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createPerInfoMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x3F}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createMorePerInfoMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(2, new short[]{0xFF}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(3, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestInfoGetterMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createPerEnumerationMapping());
        methodMappings.put("2", createPerInfoMapping());
        methodMappings.put("3", createMorePerInfoMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // Coordinator interface
    static private MethodToPacketMapping createGetAddrInfoMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createGetDiscoveredNodesMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x01}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createGetBondedNodesMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x02}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createClearAllBondsMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x03}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createBondNodeMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x04}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createRemoveBondedNodeMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x05}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createRebondNodeMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x06}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createRunDiscoveryMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x07}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, DiscoveryParamsConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createSetDPAParamsMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x08}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, DPA_ParameterConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createSetHopsMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x09}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, RoutingHopsConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createDiscoveryDataMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x0A}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createBackupMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x0B}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createRestoreMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x0C}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createAuthorizeBondMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x0D}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createEnableRemoteBondingCMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x11}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(8, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createReadRemotelyBondedModuleIdCMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x0F}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createClearRemotelyBondedModuleIdCMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x10}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestCoordinatorMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{0}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createGetAddrInfoMapping());
        methodMappings.put("2", createGetDiscoveredNodesMapping());
        methodMappings.put("3", createGetBondedNodesMapping());
        methodMappings.put("4", createClearAllBondsMapping());
        methodMappings.put("5", createBondNodeMapping());
        methodMappings.put("6", createRemoveBondedNodeMapping());
        methodMappings.put("7", createRebondNodeMapping());
        methodMappings.put("8", createRunDiscoveryMapping());
        methodMappings.put("9", createSetDPAParamsMapping());
        methodMappings.put("10", createSetHopsMapping());
        methodMappings.put("11", createDiscoveryDataMapping());
        methodMappings.put("12", createBackupMapping());
        methodMappings.put("13", createRestoreMapping());
        methodMappings.put("14", createAuthorizeBondMapping());
        methodMappings.put("16", createEnableRemoteBondingCMapping());
        methodMappings.put("17", createReadRemotelyBondedModuleIdCMapping());
        methodMappings.put("18", createClearRemotelyBondedModuleIdCMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // Node interface 
    static private MethodToPacketMapping createReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createRemoveBondMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x01}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createEnableRemoteBondingMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x04}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(8, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createReadRemotelyBondedModuleIdMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x02}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createClearRemotelyBondedModuleIdMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x03}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add( new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping removeBondAddress() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add( new ConstValueToPacketMapping(3, new short[] { 0x05 } ));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add( new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createNodeBackupMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add( new ConstValueToPacketMapping(3, new short[] { 0x06 } ));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add( new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add( new ValueToPacketMapping(6, IntToUns8Convertor.getInstance() ));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createNodeRestoreMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add( new ConstValueToPacketMapping(3, new short[] { 0x07 } ));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add( new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add( new ValueToPacketMapping(6, ArrayUns8Convertor.getInstance() ));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestNodeMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{1}));

        Map<String, MethodToPacketMapping> methodMappings
                = new HashMap<>();

        methodMappings.put("1", createReadMapping());
        methodMappings.put("2", createRemoveBondMapping());
        methodMappings.put("3", createEnableRemoteBondingMapping());
        methodMappings.put("4", createReadRemotelyBondedModuleIdMapping());
        methodMappings.put("5", createClearRemotelyBondedModuleIdMapping());
        methodMappings.put("6", removeBondAddress());
        methodMappings.put("7", createNodeBackupMapping());
        methodMappings.put("8", createNodeRestoreMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // OS interface
    static private MethodToPacketMapping createOsReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createResetMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x01}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createReadHWPConfigMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x02}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createRunRFPGMMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x03}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createSleepMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x04}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, SleepInfoConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createBatchMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x05}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, BatchCommandConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createSetUSEC_UserMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x06}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createSetMIDMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x07}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createRestartMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x08}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }
    
    static private MethodToPacketMapping createWriteHWPConfigurationMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x0F}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, HWP_ConfigurationConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }
    
   static private MethodToPacketMapping createWriteHWPConfigurationByteMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x09}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));        
        argMapping.add(new ValueToPacketMapping(6, HWP_ConfigurationByteConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }
   
   static private MethodToPacketMapping createLoadCode() {
      List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
      constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x0A}));

      List<ValueToPacketMapping> argMapping = new LinkedList<>();
      argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
      argMapping.add(new ValueToPacketMapping(6, LoadingCodePropertiesConvertor.getInstance()));

      return new MethodToPacketMapping(constMapping, argMapping);
   }

   static private InterfaceToPacketMapping createRequestOsMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{2}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createOsReadMapping());
        methodMappings.put("2", createResetMapping());
        methodMappings.put("3", createReadHWPConfigMapping());
        methodMappings.put("4", createRunRFPGMMapping());
        methodMappings.put("5", createSleepMapping());
        methodMappings.put("6", createBatchMapping());
        methodMappings.put("7", createSetUSEC_UserMapping());
        methodMappings.put("8", createSetMIDMapping());
        methodMappings.put("9", createRestartMapping());
        methodMappings.put("10", createWriteHWPConfigurationMapping());
        methodMappings.put("11", createWriteHWPConfigurationByteMapping());
        methodMappings.put("12", createLoadCode());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }
    
    // EEPROM interface
    static private MethodToPacketMapping createEEPROMReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createEEPROMWriteMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x01}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        // written user data
        argMapping.add(new ValueToPacketMapping(7, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestEEPROMMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{3}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createEEPROMReadMapping());
        methodMappings.put("2", createEEPROMWriteMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // EEEPROM interface
    static private MethodToPacketMapping createEEEPROMReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createEEEPROMWriteMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x01}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));

        // written user data
        argMapping.add(new ValueToPacketMapping(7, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }
    
    static private MethodToPacketMapping createEEEPROMExtendedReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x02}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(8, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createEEEPROMExtendedWriteMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x03}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, Uns16Convertor.getInstance()));

        // written user data
        argMapping.add(new ValueToPacketMapping(8, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestEEEPROMMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{4}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createEEEPROMReadMapping());
        methodMappings.put("2", createEEEPROMWriteMapping());
        methodMappings.put("3", createEEEPROMExtendedReadMapping());
        methodMappings.put("4", createEEEPROMExtendedWriteMapping());        

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // RAM interface
    static private MethodToPacketMapping createRAMReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, IntToUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createRAMWriteMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x01}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        // written user data
        argMapping.add(new ValueToPacketMapping(7, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestRAMMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{5}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createRAMReadMapping());
        methodMappings.put("2", createRAMWriteMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // LED interface
    static private MethodToPacketMapping createSetLEDStateMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(3, LED_StateConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createGetLEDStateMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x02}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createPulseMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x03}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestLEDRMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{6}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createSetLEDStateMapping());
        methodMappings.put("2", createGetLEDStateMapping());
        methodMappings.put("3", createPulseMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    static private InterfaceToPacketMapping createRequestLEDGMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{7}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createSetLEDStateMapping());
        methodMappings.put("2", createGetLEDStateMapping());
        methodMappings.put("3", createPulseMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // SPI interface
    static private MethodToPacketMapping createSPIWriteAndReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestSPIMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{0x08}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createSPIWriteAndReadMapping());
        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // IO interface
    static private MethodToPacketMapping createIODirectionMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, ArrayIO_DirectionSettingsConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createIOSetMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x01}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, ArrayIO_CommandConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createIOGetMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x02}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestIOMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{0x09}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createIODirectionMapping());
        methodMappings.put("2", createIOSetMapping());
        methodMappings.put("3", createIOGetMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // Thermometer interface
    static private MethodToPacketMapping createThermometerReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestThermometerMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{0x0A}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createThermometerReadMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // PWM
    static private MethodToPacketMapping createPWMSetMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, PWM_ParametersConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestPWMMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{0x0B}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createPWMSetMapping());
        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // UART
    static private MethodToPacketMapping createUARTOpenMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, BaudRateConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createUARTCloseMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x01}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createUARTWriteAndReadMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x02}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, IntToUns8Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(7, ArrayUns8Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private InterfaceToPacketMapping createRequestUARTMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{0x0C}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createUARTOpenMapping());
        methodMappings.put("2", createUARTCloseMapping());
        methodMappings.put("3", createUARTWriteAndReadMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    // FRC
    static private MethodToPacketMapping createFRCSendMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x00}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, FRC_CommandConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createExtraResultMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add( new ConstValueToPacketMapping(3, new short[] { 0x01 } ));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add( new ValueToPacketMapping(4, Uns16Convertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createSendSelectiveMapping() {
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x02}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, FRC_SelectCommandConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }

    static private MethodToPacketMapping createSetFRCParamsMapping(){
        List<ConstValueToPacketMapping> constMapping = new LinkedList<>();
        constMapping.add(new ConstValueToPacketMapping(3, new short[]{0x03}));

        List<ValueToPacketMapping> argMapping = new LinkedList<>();
        argMapping.add(new ValueToPacketMapping(4, Uns16Convertor.getInstance()));
        argMapping.add(new ValueToPacketMapping(6, FRC_ConfigurationConvertor.getInstance()));

        return new MethodToPacketMapping(constMapping, argMapping);
    }
    
    static private InterfaceToPacketMapping createRequestFRCMapping() {
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();
        constMappings.add(new ConstValueToPacketMapping(2, new short[]{0x0D}));

        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createFRCSendMapping());
        methodMappings.put("2", createExtraResultMapping());
        methodMappings.put("3", createSendSelectiveMapping());
        methodMappings.put("4", createSetFRCParamsMapping());

        return new InterfaceToPacketMapping(constMappings, methodMappings);
    }

    /**
     * @return map of mapping of Device Interfaces into protocol packets.
     */
    static private Map<Class, InterfaceToPacketMapping> createRequestIfaceMappings() {
        Map<Class, InterfaceToPacketMapping> mappings = new HashMap<>();

        // creating interface mappings
        mappings.put(PeripheralInfoGetter.class, createRequestInfoGetterMapping());
        mappings.put(Coordinator.class, createRequestCoordinatorMapping());
        mappings.put(Node.class, createRequestNodeMapping());
        mappings.put(OS.class, createRequestOsMapping());
        mappings.put(EEPROM.class, createRequestEEPROMMapping());
        mappings.put(EEEPROM.class, createRequestEEEPROMMapping());
        mappings.put(RAM.class, createRequestRAMMapping());
        mappings.put(LEDR.class, createRequestLEDRMapping());
        mappings.put(LEDG.class, createRequestLEDGMapping());
        mappings.put(SPI.class, createRequestSPIMapping());
        mappings.put(IO.class, createRequestIOMapping());
        mappings.put(PWM.class, createRequestPWMMapping());
        mappings.put(Thermometer.class, createRequestThermometerMapping());
        mappings.put(UART.class, createRequestUARTMapping());
        mappings.put(FRC.class, createRequestFRCMapping());

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

    // PACKET TO RESPONSE MAPPING
    static private PacketToValueMapping createResponseNetworkMapping() {
        return new PacketToValueMapping(0, 0, StringToByteConvertor.getInstance());
    }

    static private PacketToValueMapping createResponseNodeMapping() {
        return new PacketToValueMapping(0, 1, StringToByteConvertor.getInstance());
    }

    static private PacketToMethodMapping createResponsePerEnumeration() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 0xFF));
        packetValues.add(new PacketPositionValues(3, (short) 0xBF));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PeripheralEnumerationConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseGetPerInfo() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0xBF));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PeripheralInfoConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseGetMorePerInfo() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 0xFF));

        // the remainder of the packet will be taken for conversion
        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, ArrayPeripheralInfoConvertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }

    // Peripheral info getter - there isn't equivalent peripheral in DPA.
    static private PacketToInterfaceMapping createResponseInfoGetterMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponsePerEnumeration());
        methodMappings.put("2", createResponseGetPerInfo());
        methodMappings.put("3", createResponseGetMorePerInfo());

        return new PacketToInterfaceMapping(
                PeripheralInfoGetter.class, packetValues, methodMappings
        );
    }

    // Coordinator
    static private PacketToMethodMapping createResponseGetAddrInfo() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, AddressingInfoConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseGetDiscoveredDevices() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, DiscoveredNodesConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseGetBondedDevices() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x82));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, BondedNodesConvertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseClearAllBonds() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x83));

        // how about response, where aren't any result data? VoidType 
        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("4", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseBondDevice() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x84));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, BondedDeviceConvertor.getInstance()
        );
        return new PacketToMethodMapping("5", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseRemoveBondedDevice() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x85));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, IntToUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("6", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseRebondDevice() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x86));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, IntToUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("7", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseRunDiscovery() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x87));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, DiscoveryResultConvertor.getInstance()
        );
        return new PacketToMethodMapping("8", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseSetDPAParams() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x88));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, DPA_ParameterConvertor.getInstance()
        );
        return new PacketToMethodMapping("9", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseSetHops() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x89));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, RoutingHopsConvertor.getInstance()
        );
        return new PacketToMethodMapping("10", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseDiscoveryData() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x8A));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("11", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseBackup() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x8B));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("12", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseRestore() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x8C));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("13", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseAuthorizeBond() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x8D));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, BondedDeviceConvertor.getInstance()
        );
        return new PacketToMethodMapping("14", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseBridge() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x8E));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("15", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createEnableRemoteBondingC() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x91));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("16", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createReadRemotelyBondedModuleIdC() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x8F));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, RemotelyBondedModuleIdConvertor.getInstance()
        );
        return new PacketToMethodMapping("17", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createClearRemotelyBondedModuleIdC() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x90));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("18", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseCoordinatorMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 0));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseGetAddrInfo());
        methodMappings.put("2", createResponseGetDiscoveredDevices());
        methodMappings.put("3", createResponseGetBondedDevices());
        methodMappings.put("4", createResponseClearAllBonds());
        methodMappings.put("5", createResponseBondDevice());
        methodMappings.put("6", createResponseRemoveBondedDevice());
        methodMappings.put("7", createResponseRebondDevice());
        methodMappings.put("8", createResponseRunDiscovery());
        methodMappings.put("9", createResponseSetDPAParams());
        methodMappings.put("10", createResponseSetHops());
        methodMappings.put("11", createResponseDiscoveryData());
        methodMappings.put("12", createResponseBackup());
        methodMappings.put("13", createResponseRestore());
        methodMappings.put("14", createResponseAuthorizeBond());
        methodMappings.put("15", createResponseBridge());
        methodMappings.put("16", createEnableRemoteBondingC());
        methodMappings.put("17", createReadRemotelyBondedModuleIdC());
        methodMappings.put("18", createClearRemotelyBondedModuleIdC());

        return new PacketToInterfaceMapping(Coordinator.class, packetValues, methodMappings);
    }

    // Node interface
    static private PacketToMethodMapping createResponseNodeRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, NodeStatusInfoConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseRemoveBond() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createEnableRemoteBonding() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x84));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createReadRemotelyBondedModuleId() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x82));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, RemotelyBondedModuleIdConvertor.getInstance()
        );
        return new PacketToMethodMapping("4", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createClearRemotelyBondedModuleId() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x83));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("5", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createRemoveBondAddress() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x85));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("6", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createNodeResponseBackup() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x86));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("7", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createNodeResponseRestore() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x87));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("8", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseIQMeshNodeMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 1));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseNodeRead());
        methodMappings.put("2", createResponseRemoveBond());
        methodMappings.put("3", createEnableRemoteBonding());
        methodMappings.put("4", createReadRemotelyBondedModuleId());
        methodMappings.put("5", createClearRemotelyBondedModuleId());
        methodMappings.put("6", createRemoveBondAddress());
        methodMappings.put("7", createNodeResponseBackup());
        methodMappings.put("8", createNodeResponseRestore());

        return new PacketToInterfaceMapping(Node.class, packetValues, methodMappings);
    }

    // OS interface
    static private PacketToMethodMapping createResponseRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, OsInfoConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseReset() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseReadHWPConfig() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x82));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, HWP_ConfigurationConvertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseRunRFPGM() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x83));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("4", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseSleep() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x84));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("5", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseBatch() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x85));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("6", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseSetUSEC() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x86));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("7", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseSetMID() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x87));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("8", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseRestart() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x88));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("9", packetValues, resultMapping);
    }
    
    static private PacketToMethodMapping createResponseWriteHWPConfig() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x8F));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("10", packetValues, resultMapping);
    }
    
    static private PacketToMethodMapping createResponseWriteHWPConfigByte() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x89));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("11", packetValues, resultMapping);
    }
    
    static private PacketToMethodMapping createResponseLoadCode() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x8A));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 1, LoadingResultConvertor.getInstance()
        );
        return new PacketToMethodMapping("12", packetValues, resultMapping);
    }
    
    static private PacketToInterfaceMapping createResponseOsMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 2));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseRead());
        methodMappings.put("2", createResponseReset());
        methodMappings.put("3", createResponseReadHWPConfig());
        methodMappings.put("4", createResponseRunRFPGM());
        methodMappings.put("5", createResponseSleep());
        methodMappings.put("6", createResponseBatch());
        methodMappings.put("7", createResponseSetUSEC());
        methodMappings.put("8", createResponseSetMID());
        methodMappings.put("9", createResponseRestart());
        methodMappings.put("10", createResponseWriteHWPConfig());
        methodMappings.put("11", createResponseWriteHWPConfigByte());
        methodMappings.put("12", createResponseLoadCode());

        return new PacketToInterfaceMapping(OS.class, packetValues, methodMappings);
    }

    // EEPROM interface
    static private PacketToMethodMapping createResponseEEPROMRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseEEPROMWrite() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseEEPROMMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 3));

        Map<String, PacketToMethodMapping> methodMappings
                = new HashMap<>();

        methodMappings.put("1", createResponseEEPROMRead());
        methodMappings.put("2", createResponseEEPROMWrite());

        return new PacketToInterfaceMapping(EEPROM.class, packetValues, methodMappings);
    }

    // EEEPROM interface
    static private PacketToMethodMapping createResponseEEEPROMRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseEEEPROMWrite() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }
    
    static private PacketToMethodMapping createResponseEEEPROMExtendedRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x82));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseEEEPROMExtendedWrite() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x83));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("4", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseEEEPROMMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 4));

        Map<String, PacketToMethodMapping> methodMappings
                = new HashMap<>();

        methodMappings.put("1", createResponseEEEPROMRead());
        methodMappings.put("2", createResponseEEEPROMWrite());
        methodMappings.put("3", createResponseEEEPROMExtendedRead());
        methodMappings.put("4", createResponseEEEPROMExtendedWrite());

        return new PacketToInterfaceMapping(EEEPROM.class, packetValues, methodMappings);
    }

    // RAM interface
    static private PacketToMethodMapping createResponseRAMRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseRAMWrite() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseRAMMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 5));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseRAMRead());
        methodMappings.put("2", createResponseRAMWrite());

        return new PacketToInterfaceMapping(RAM.class, packetValues, methodMappings);
    }

    // LEDR interface
    static private PacketToMethodMapping createResponseLEDSetState() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        // 2 alternatives possible: 0x80 and 0x81
        packetValues.add(new PacketPositionValues(3, new short[]{0x80, 0x81}));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseLEDGetState() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x82));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, LED_StateConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponsePulse() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x83));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseLEDRMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 6));

        Map<String, PacketToMethodMapping> methodMappings
                = new HashMap<>();

        methodMappings.put("1", createResponseLEDSetState());
        methodMappings.put("2", createResponseLEDGetState());
        methodMappings.put("3", createResponsePulse());

        return new PacketToInterfaceMapping(LEDR.class, packetValues, methodMappings);
    }

    // LEDG interface
    static private PacketToInterfaceMapping createResponseLEDGMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 7));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseLEDSetState());
        methodMappings.put("2", createResponseLEDGetState());
        methodMappings.put("3", createResponsePulse());

        return new PacketToInterfaceMapping(LEDG.class, packetValues, methodMappings);
    }

    // SPI interface
    static private PacketToMethodMapping createResponseSPIWriteAndRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseSPIMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 0x08));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseSPIWriteAndRead());
        return new PacketToInterfaceMapping(SPI.class, packetValues, methodMappings);
    }

    // IO interface
    static private PacketToMethodMapping createResponseDirection() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance());

        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseIOSet() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseIOGet() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x82));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }
    
    static private PacketToMethodMapping createEnableAsynchronyRequestSetDirection(){
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x00));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("4", packetValues, resultMapping);        
    }
    
    static private PacketToMethodMapping createEnableAsynchronyRequestSetOutputState(){
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x01));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("5", packetValues, resultMapping);        
    }
    
    static private PacketToMethodMapping createEnableAsynchronyRequestGet(){
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x01));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("6", packetValues, resultMapping);        
    } 
    
    static private PacketToInterfaceMapping createResponseIOMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 9));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseDirection());
        methodMappings.put("2", createResponseIOSet());
        methodMappings.put("3", createResponseIOGet());
        /* ASYNCHRONY REQUEST ENABLERS */
        methodMappings.put("4", createEnableAsynchronyRequestSetDirection());
        methodMappings.put("5", createEnableAsynchronyRequestSetOutputState());
        methodMappings.put("6", createEnableAsynchronyRequestGet());

        return new PacketToInterfaceMapping(IO.class, packetValues, methodMappings);
    }

    // Thermometer
    static private PacketToMethodMapping createResponseThermometerRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, ThermometerValueConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseThermometerMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 10));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseThermometerRead());
        return new PacketToInterfaceMapping(Thermometer.class, packetValues, methodMappings);
    }

    // PWM
    static private PacketToMethodMapping createResponsePWMSet() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponsePWMMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 0x0B));

        Map<String, PacketToMethodMapping> methodMappings
                = new HashMap<>();

        methodMappings.put("1", createResponsePWMSet());
        return new PacketToInterfaceMapping(PWM.class, packetValues, methodMappings);
    }

    // UART
    static private PacketToMethodMapping createResponseUARTOpen() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseUARTClose() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 0, VoidTypeConvertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseUARTWriteAndRead() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x82));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }

    static private PacketToInterfaceMapping createResponseUARTMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 0x0C));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseUARTOpen());
        methodMappings.put("2", createResponseUARTClose());
        methodMappings.put("3", createResponseUARTWriteAndRead());
        return new PacketToInterfaceMapping(UART.class, packetValues, methodMappings);
    }

    // FRC
    static private PacketToMethodMapping createResponseFRCSend() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x80));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, FRC_DataConvertor.getInstance()
        );
        return new PacketToMethodMapping("1", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseExtraResult() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x81));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, PrimArrayUns8Convertor.getInstance()
        );
        return new PacketToMethodMapping("2", packetValues, resultMapping);
    }

    static private PacketToMethodMapping createResponseFRCSendSelective() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x82));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, FRC_DataConvertor.getInstance()
        );
        return new PacketToMethodMapping("3", packetValues, resultMapping);
    }
    
    static private PacketToMethodMapping createResponseSetFRCParams(){
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(3, (short) 0x83));

        PacketToValueMapping resultMapping = new PacketToValueMapping(
                8, 1, FRC_ConfigurationConvertor.getInstance()
        );
        return new PacketToMethodMapping("4", packetValues, resultMapping);
    }    
        
    static private PacketToInterfaceMapping createResponseFRCMapping() {
        List<PacketPositionValues> packetValues = new LinkedList<>();
        packetValues.add(new PacketPositionValues(2, (short) 0x0D));

        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();

        methodMappings.put("1", createResponseFRCSend());
        methodMappings.put("2", createResponseExtraResult());
        methodMappings.put("3", createResponseFRCSendSelective());
        methodMappings.put("4", createResponseSetFRCParams());
        
        return new PacketToInterfaceMapping(FRC.class, packetValues, methodMappings);
    }

    // creating response mapping for Device Interfaces
    static private Map<Class, PacketToInterfaceMapping> createResponseIfaceMappings() {
        Map<Class, PacketToInterfaceMapping> mappings = new HashMap<>();

        // creating interface mappings
        mappings.put(PeripheralInfoGetter.class, createResponseInfoGetterMapping());
        mappings.put(Coordinator.class, createResponseCoordinatorMapping());
        mappings.put(Node.class, createResponseIQMeshNodeMapping());
        mappings.put(OS.class, createResponseOsMapping());
        mappings.put(EEPROM.class, createResponseEEPROMMapping());
        mappings.put(EEEPROM.class, createResponseEEEPROMMapping());
        mappings.put(RAM.class, createResponseRAMMapping());
        mappings.put(LEDR.class, createResponseLEDRMapping());
        mappings.put(LEDG.class, createResponseLEDGMapping());
        mappings.put(SPI.class, createResponseSPIMapping());
        mappings.put(IO.class, createResponseIOMapping());
        mappings.put(Thermometer.class, createResponseThermometerMapping());
        mappings.put(PWM.class, createResponsePWMMapping());
        mappings.put(UART.class, createResponseUARTMapping());
        mappings.put(FRC.class, createResponseFRCMapping());

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

    /**
     * Creates new DPA protocol mapping factory.
     */
    public DPA_StandardPerProtocolMappingFactory() {
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
