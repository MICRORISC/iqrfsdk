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

package com.microrisc.simply.protocol.mapping;

import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Describes mapping of call request to packet of protocol layer.
 * 
 * @author Michal Konopa
 */
public final class SimpleCallRequestToPacketMapping 
implements CallRequestToPacketMapping {
    /** Constant values mappings. */
    private List<ConstValueToPacketMapping> constantMappings = null;
    
    /** Network mappings. */
    private List<ValueToPacketMapping> networkMappings = null;
    
    /** Node mappings. */
    private List<ValueToPacketMapping> nodeMappings = null;
    
    /** Device Interface mappings. */
    private Map<Class, InterfaceToPacketMapping> ifaceMappings = null;
    
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SimpleCallRequestToPacketMapping.class);
    
    
    
    /**
     * Constructor.
     * @param constantMappings
     * @param networkMappings
     * @param nodeMapping
     * @param ifaceMappings 
     */
    public SimpleCallRequestToPacketMapping(
            List<ConstValueToPacketMapping> constantMappings,
            List<ValueToPacketMapping> networkMappings,
            List<ValueToPacketMapping> nodeMapping, 
            Map<Class, InterfaceToPacketMapping> ifaceMappings
    ) {
       this.constantMappings = constantMappings;
       this.networkMappings = networkMappings;
       this.nodeMappings = nodeMapping;
       this.ifaceMappings = ifaceMappings;
    }
    
    @Override
    public Set<Class> getSupportedDeviceInterfaces() {
        return ifaceMappings.keySet();
    }
    
    /**
     * @return serialized protocol data
     */
    @Override
    public List<PacketFragment> getSerializedProtocolData() {
        logger.debug("getSerializedProtocolData - start: ");
        
        List<PacketFragment> serData = new LinkedList<>();
        for (ConstValueToPacketMapping protoMapping : constantMappings) {
            serData.add(
                    new PacketFragment(protoMapping.getStartingPosition(), 
                        protoMapping.getConvertedValue())
                    );
        }
        
        logger.debug("getSerializedProtocolData - end: {}", serData);
        return serData;
    }
    
    /**
     * Serializes specified network ID and returns it.
     * @param networkId network ID
     * @return serialized network data
     * @throws ValueConversionException if an error has occurred during serialization
     */
    @Override
    public List<PacketFragment> getSerializedNetworkData(String networkId) 
            throws ValueConversionException {
        logger.debug("getSerializedNetworkData - start: nodeId={}", networkId);
        
        List<PacketFragment> serNetworkData = new LinkedList<>();
        for (ValueToPacketMapping networkMapping : networkMappings) {
            short[] serData = Serializer.serialize(networkMapping, networkId);
            serNetworkData.add(
                    new PacketFragment(networkMapping.getStartingPosition(), serData)
                );
        }
        
        logger.debug("getSerializedNetworkData - end: {}", serNetworkData);
        return serNetworkData;
    }
    
    /**
     * Serializes specified Node ID and returns it.
     * @param nodeId node ID
     * @return serialized node data
     * @throws ValueConversionException if an error has occurred during serialization
     */
    @Override
    public List<PacketFragment> getSerializedNodeData(String nodeId) 
            throws ValueConversionException {
        logger.debug("getSerializedNodeData - start: nodeId={}", nodeId);
        
        List<PacketFragment> serNodeData = new LinkedList<>();
        for (ValueToPacketMapping nodeMapping : nodeMappings) {
            short[] serData = Serializer.serialize(nodeMapping, nodeId);
            serNodeData.add(
                    new PacketFragment(nodeMapping.getStartingPosition(), serData)
                );
        }
        
        logger.debug("getSerializedNodeData - end: {}", serNodeData);
        return serNodeData;
    }
    
    @Override
    public List<PacketFragment> getSerializedInterfaceData(Class devInterface) 
            throws ProtocolMappingException {
        logger.debug("getSerializedInterfaceData - start: devInterface={}", devInterface);
        
        InterfaceToPacketMapping ifaceMapping = ifaceMappings.get(devInterface);
        if ( ifaceMapping == null ) {
            throw new ProtocolMappingException(
                "Interface mapping not found. Device Interface: " + devInterface.getName()
            );
        }
        
        List<PacketFragment> serIfaceData = new LinkedList<>();
        List<ConstValueToPacketMapping> constMappings = ifaceMapping.getConstantMappings();
        for (ConstValueToPacketMapping constMapping : constMappings) {
            serIfaceData.add(
                    new PacketFragment(constMapping.getStartingPosition(), 
                        constMapping.getConvertedValue())
                    );
        }
        
        logger.debug("getSerializedInterfaceData - end: {}", serIfaceData);
        return serIfaceData;
    }
    
    @Override
    public List<PacketFragment> getSerializedMethodData(Class devInterface, String methodId) 
            throws ProtocolMappingException {
        logger.debug("getSerializedMethodData - start: devInterface={}, "
                + "methodId={}", devInterface, methodId);
        
        InterfaceToPacketMapping ifaceMapping = ifaceMappings.get(devInterface);
        if ( ifaceMapping == null ) {
            throw new ProtocolMappingException("Interface mapping not found. "
                + " Device Interface: " + devInterface.getName());
        }
        
        MethodToPacketMapping methodMapping = ifaceMapping.getMethodMapping(methodId);
        if ( methodMapping == null ) {
            throw new ProtocolMappingException("Method mapping not found. "
                + " Method ID: " + methodId);
        }
        
        List<PacketFragment> serMethodData = new LinkedList<>();
        List<ConstValueToPacketMapping> constMappings = methodMapping.getConstantMappings();
        for (ConstValueToPacketMapping constMapping : constMappings) {
            serMethodData.add(
                    new PacketFragment(constMapping.getStartingPosition(), 
                        constMapping.getConvertedValue())
                    );
        }
        
        logger.debug("getSerializedMethodData - end: {}", serMethodData);
        return serMethodData;
    }
    
    @Override
    public List<PacketFragment> getSerializedMethodArgs(
            Class devInterface, String methodId, Object[] args
    ) throws ProtocolMappingException, ValueConversionException {
        Object[] logArgs = new Object[3];
        logArgs[0] = devInterface;
        logArgs[1] = methodId;
        logArgs[2] = args;
        logger.debug(
            "getSerializedMethodArgs - start: devInterface={}, " 
             + "methodId={}, args={}", logArgs
        );
        
        InterfaceToPacketMapping ifaceMapping = ifaceMappings.get(devInterface);
        if ( ifaceMapping == null ) {
            throw new ProtocolMappingException("Interface mapping not found. "
                + " Device Interface: " + devInterface.getName());
        }
        
        MethodToPacketMapping methodMapping = ifaceMapping.getMethodMapping(methodId);
        if ( methodMapping == null ) {
            throw new ProtocolMappingException("Method mapping not found. "
                + " Method ID: " + methodId);
        }
        
        Iterator<ValueToPacketMapping> argIter = methodMapping.getArgMappings().iterator();
        List<PacketFragment> serArgs = new LinkedList<>();
        
        int argId = 0;
        while ( argIter.hasNext() ) {
            ValueToPacketMapping argMapping = argIter.next();
            short[] serArg = Serializer.serialize(argMapping, args[argId]);
            serArgs.add(
                new PacketFragment(argMapping.getStartingPosition(), serArg)
            );
            argId++;
        }
        
        logger.debug("getSerializedMethodArgs - end: {}", serArgs);
        return serArgs;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "constant mappings=" + constantMappings + 
                ", node mappings=" + nodeMappings + 
                ", interface mappings=" + ifaceMappings +
                " }");
    }
}
