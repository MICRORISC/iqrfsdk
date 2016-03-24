/*
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.protocol.mapping;

import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;
import com.microrisc.simply.iqrf.typeconvertors.Uns16Convertor;
import com.microrisc.simply.protocol.mapping.*;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.StringToByteConvertor;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides parsing of xml file into {@link FileMappingObjects} when user using 
 * mapping from file.
 * 
 * @author Martin Strouhal
 */
public final class FileMappingParser {       
    
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(FileMappingParser.class);

    private final FileMappingUtils utils = FileMappingUtils.getInstance();
    
    private NodeList listInterfaces;
    private Map<Integer, Class> peripheralToIface;                
    
    //
    // REQUEST MAPPING
    // general - all mapings are organized here
    private CallRequestToPacketMapping createCallRequestToPacketMapping() throws Exception {
        List<ConstValueToPacketMapping> constMappings = createRequestConstMappings();
        List<ValueToPacketMapping> networkMappings = createRequestNetworkMappings();
        List<ValueToPacketMapping> nodeMappings = createRequestNodeMappings();
        Map<Class, InterfaceToPacketMapping> ifaceMappings = createAllRequestInterfaceMappings();

        return new SimpleCallRequestToPacketMapping(constMappings, 
                networkMappings, nodeMappings, ifaceMappings);
    }

    // default - returns currently empty list of mappings
    private List<ConstValueToPacketMapping> createRequestConstMappings() {
        List<ConstValueToPacketMapping> mappings = new LinkedList<>();
        return mappings;
    }

    // default - returns empty list of mappings - more networks capability is not currently used
    private List<ValueToPacketMapping> createRequestNetworkMappings() {
        List<ValueToPacketMapping> mappings = new LinkedList<>();
        return mappings;
    }

    // default
    private List<ValueToPacketMapping> createRequestNodeMappings() {
        List<ValueToPacketMapping> mappings = new LinkedList<>();
        ValueToPacketMapping nodeMapping = new ValueToPacketMapping(0,
                StringToByteConvertor.getInstance()
        );
        mappings.add(nodeMapping);
        return mappings;
    }

    // mapping of all interfaces from file
    private Map<Class, InterfaceToPacketMapping> createAllRequestInterfaceMappings() throws Exception {
        Map<Class, InterfaceToPacketMapping> map = new HashMap<>();
        for (int i = 0; i < listInterfaces.getLength(); i++) {
            Map.Entry<Class, InterfaceToPacketMapping> entry
                    = createRequestInterfaceMappings(utils.convertNode(listInterfaces.item(i)));
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    // mapping of speific interface from file    
    private Map.Entry<Class, InterfaceToPacketMapping> createRequestInterfaceMappings(Element elementInterface) throws Exception {
        // Device interface class name
        Node nodeClass = utils.getNode(elementInterface, "class");
        Class clasz = Class.forName(nodeClass.getTextContent());

        // Device interface ID = id of periheral used in packet
        Node nodePeripheralId = utils.getNode(elementInterface, "peripheralId");
        short peripheralId = utils.parseNumber(nodePeripheralId.getTextContent());
        List<ConstValueToPacketMapping> constMappings = new LinkedList<>();        
        constMappings.add(new ConstValueToPacketMapping(
                DPA_ProtocolProperties.PNUM_START, new short[]{peripheralId}
        ));
        
        // adding device interface with number to list for UserPerToDevIfaceMapper
        peripheralToIface.put((int)peripheralId, clasz);

        // Device interface methods
        Map<String, MethodToPacketMapping> methodMappings = new HashMap<>();
        NodeList listMethods = elementInterface.getElementsByTagName("method");
        for (int i = 0; i < listMethods.getLength(); i++) {
            Map.Entry<String, MethodToPacketMapping> method
                    = createRequestMethodMappings(utils.convertNode(listMethods.item(i)));
            methodMappings.put(method.getKey(), method.getValue());
        }

        InterfaceToPacketMapping interfaceMappings = new InterfaceToPacketMapping(
                constMappings, methodMappings);
        return new AbstractMap.SimpleEntry<>(clasz, interfaceMappings);
    }

    // mapping of specific methods of DI
    private Map.Entry<String, MethodToPacketMapping> createRequestMethodMappings(Element elementMethod) throws Exception {
        Node nodeMethodId = utils.getNode(elementMethod, "id");
        short methodId = utils.parseNumber(nodeMethodId.getTextContent());
        List<ConstValueToPacketMapping> peripheralConst = new LinkedList<>();
        peripheralConst.add(new ConstValueToPacketMapping(
                DPA_ProtocolProperties.PCMD_START, new short[]{methodId}
        ));

        Element elementArguments = utils.convertNode(utils.getNode(elementMethod, "requestArguments"));
        List<FileMappingArgument> sortedListArguments = getSortedArguments(elementArguments);

        List<ValueToPacketMapping> valuesMapping = new LinkedList<>();
        //mapping of data convertors
        short lastUsedPosition = DPA_ProtocolProperties.PDATA_START;
        for (int i = 0; i < valuesMapping.size(); i++) {
            lastUsedPosition++; //for each next argument next position            
            FileMappingArgument argument = sortedListArguments.get(i);
            valuesMapping.add(new ValueToPacketMapping(lastUsedPosition, argument.getConvertor()));
            //increase plus value for positions occupied by previous longer argument 
            lastUsedPosition += argument.getLength();
        }
        valuesMapping.add(new ValueToPacketMapping(
                DPA_ProtocolProperties.HW_PROFILE_START, Uns16Convertor.getInstance()
        ));

        MethodToPacketMapping method = new MethodToPacketMapping(peripheralConst, valuesMapping);

        return new AbstractMap.SimpleEntry<>(Short.toString(methodId), method);
    }

    //
    // RESPONSES MAPPING
    // general
    private PacketToCallResponseMapping createPacketToCallResponseMapping() throws Exception {
        PacketToValueMapping networkMapping = createResponseNetworkMapping();
        PacketToValueMapping nodeMapping = createResponseNodeMapping();
        Map<Class, PacketToInterfaceMapping> ifaceMappings = createAllResponseInterfaceMappings();
        PacketToValueMapping additionalDataMapping = createAdditionalDataMapping();

        return new SimplePacketToCallResponseMapping(
                networkMapping, nodeMapping, ifaceMappings, additionalDataMapping
        );
    }

    // default
    private PacketToValueMapping createResponseNetworkMapping() {
        return new PacketToValueMapping(0, 0, StringToByteConvertor.getInstance());
    }

    // default
    private PacketToValueMapping createResponseNodeMapping() {
        return new PacketToValueMapping(0, 1, StringToByteConvertor.getInstance());
    }

    // mapping of all interfaces from file
    private Map<Class, PacketToInterfaceMapping> createAllResponseInterfaceMappings() throws Exception {
        Map<Class, PacketToInterfaceMapping> map = new HashMap<>();
        for (int i = 0; i < listInterfaces.getLength(); i++) {
            Map.Entry<Class, PacketToInterfaceMapping> entry
                    = createResponseInterfaceMappings(utils.convertNode(listInterfaces.item(i)));
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    // mapping of specific interface (it meants the form itself packet with specified methods) from file
    private Map.Entry<Class, PacketToInterfaceMapping> createResponseInterfaceMappings(Element elementInterface) throws Exception {
        // Device interface class name
        Node nodeClass = utils.getNode(elementInterface, "class");
        Class clasz = Class.forName(nodeClass.getTextContent());

        // Device interface ID = id of periheral used in packet
        List<PacketPositionValues> packetValues = new LinkedList<>();
        Node nodePeripheralId = utils.getNode(elementInterface, "peripheralId");
        short peripheralId = utils.parseNumber(nodePeripheralId.getTextContent());
        packetValues.add(new PacketPositionValues(
                DPA_ProtocolProperties.PNUM_START, peripheralId
        ));

        // Device interface methods                
        Map<String, PacketToMethodMapping> methodMappings = new HashMap<>();
        NodeList listMethods = elementInterface.getElementsByTagName("method");
        for (int i = 0; i < listMethods.getLength(); i++) {
            Map.Entry<String, PacketToMethodMapping> method = createResponseMethodMappings(
                    utils.convertNode(listMethods.item(i)), peripheralId);
            methodMappings.put(method.getKey(), method.getValue());
        }

        PacketToInterfaceMapping packetMapping = new PacketToInterfaceMapping(
                clasz, packetValues, methodMappings);
        return new AbstractMap.SimpleEntry<>(clasz, packetMapping);
    }

    /**
     * Parse mapping of one method.
     * <p>
     * @param elementMethod element from which will be parsed data for method
     * mapping
     * @param peripheralId of peripheral in which is method mapped
     * @return entry for mapped method
     * @throws Exception if some error has been occured
     */
    private Map.Entry<String, PacketToMethodMapping> createResponseMethodMappings(Element elementMethod, short peripheralId) throws Exception {
        String methodId = utils.getNode(elementMethod, "periheralCommandId").getTextContent();

        Node nodeMethodId = utils.getNode(elementMethod, "periheralCommandId");
        short responseId = utils.parseNumber(nodeMethodId.getTextContent());
        responseId += 0x80; // response identification
        List<PacketPositionValues> positionValues = new LinkedList<>();
        positionValues.add(new PacketPositionValues(
                DPA_ProtocolProperties.PCMD_START, new short[]{responseId}
        ));

        Node nodeResponseCon = utils.getNode(elementMethod, "responseConvertor");

        AbstractConvertor convertor = utils.getConvertor(nodeResponseCon.getTextContent());
        PacketToValueMapping packetValue = new PacketToValueMapping(
                DPA_ProtocolProperties.RESPONSE_DATA_START, convertor
        );

        PacketToMethodMapping method = new PacketToMethodMapping(
                methodId, positionValues, packetValue
        );

        return new AbstractMap.SimpleEntry<>(methodId, method);
    }

    private List<FileMappingArgument> getSortedArguments(Element elementArguments) throws Exception {
        NodeList nodeListArguments = elementArguments.getElementsByTagName("arguments");
        List<FileMappingArgument> sortedListArguments = new ArrayList<>();
        
        for (int i = 0; i < nodeListArguments.getLength(); i++) {
            Element elementArgument = utils.convertNode(nodeListArguments.item(i));

            String stringOrder = utils.getNode(elementArgument, "order").getTextContent();
            short order = utils.parseNumber(stringOrder);

            String stringConvertor = utils.getNode(elementArgument, "convertor").getTextContent();
            AbstractConvertor convertor = utils.getConvertor(stringConvertor);

            short length = utils.parseNumber(
                    utils.getNode(elementArgument, "length").getTextContent());

            FileMappingArgument mappingArgument = new FileMappingArgument(order, length, convertor);
            sortedListArguments.add(mappingArgument);
        }
        
        Collections.sort(sortedListArguments, new Comparator<FileMappingArgument>() {
            @Override
            public int compare(FileMappingArgument o1, FileMappingArgument o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        return sortedListArguments;
    }

    // default - maybe not used???
    private PacketToValueMapping createAdditionalDataMapping() {
        return new PacketToValueMapping(4, null);// DPA_AdditionalInfoConvertor.getInstance());
    }
    
            
    private FileMappingParser(){        
    }
        
    private static FileMappingParser parser;
    /**
     * Returns instance of {@link FileMappingParser}.
     * @return instance of parser
     */
    public static FileMappingParser getParser(){
        return ((parser == null) ? new FileMappingParser() : parser);
    }
    
    /**
     * Parse xml file into {@link FileMappingObjects}
     * @param interfaces node from xml file
     * @return parsed data
     * @throws Exception if some error has been occured
     */
    public FileMappingObjects parse(NodeList interfaces) throws Exception {
        listInterfaces = interfaces;
        peripheralToIface = new HashMap<>();
        
        // parsing (interfaces are added to map too) and saving protocol mapping
        ProtocolMapping protocolMapping = new SimpleProtocolMapping(
                createCallRequestToPacketMapping(),
                createPacketToCallResponseMapping()
        );
        
        return new FileMappingObjects(protocolMapping, peripheralToIface);
    }
}