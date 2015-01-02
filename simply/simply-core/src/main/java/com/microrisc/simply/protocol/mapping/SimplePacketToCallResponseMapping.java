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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores mappings from protocol packets to Device Objects. 
 * 
 * @author Michal Konopa
 */
public final class SimplePacketToCallResponseMapping 
implements PacketToCallResponseMapping {
    
    /** Network mapping. */
    private final PacketToValueMapping networkMapping;
    
    /** Node mapping. */
    private final PacketToValueMapping nodeMapping;
    
    /** Device Interface mappings. */
    private final Map<Class, PacketToInterfaceMapping> interfaceMappings;
    
    /** Additional data mapping. */
    private final PacketToValueMapping additionalDataMapping;
    
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SimplePacketToCallResponseMapping.class);
    
    // Indicates, wheather specified packet fullfils specified packet values.
    private boolean isCompatible(short[] packet, List<PacketPositionValues> packetPosValues) 
    {
        logger.debug("isCompatible - start: packet={}, packetValues={}", 
                packet, packetPosValues);
        
        for (PacketPositionValues packetPosValue : packetPosValues) {
            if (packetPosValue.getPosition() >= packet.length) {
                logger.debug("isCompatible - end: {}", false);
                return false;
            }
            
            if (!packetPosValue.canBeAtPosition(packet[packetPosValue.getPosition()])) {
                logger.debug("isCompatible - end: {}", false);
                return false;
            }
        }
        
        logger.debug("isCompatible - end: {}", true);
        return true;
    }
    
    /**
     * Indicates, wheather the packet is compatible with specified result mapping.
     * @param packet
     * @param resultMapping
     * @return {@code true} if packet is compatible
     *         {@code false} otherwise
     */
    private boolean isResultCompatible(short[] packet, PacketToValueMapping resultMapping) {
        logger.debug("isResultCompatible - start: packet={}, resultMapping={}", 
                packet, resultMapping);
        
        if (resultMapping.isUpToEnd()) {
            logger.debug("isResultCompatible - end: {}", true);
            return true;
        }
        
        int lastResultPos = resultMapping.getStartingPosition() - 1;
        if (resultMapping.getLength() > 0) {
            lastResultPos += resultMapping.getLength();
        }
        
        if (lastResultPos == packet.length - 1) {
            logger.debug("isResultCompatible - end: {}", true);
            return true;
        }
        
        logger.debug("isResultCompatible - end: {}", false);
        return false;
    }
    
    /**
     * Constructor.
     * @param networkMapping network mapping
     * @param nodeMapping node mapping
     * @param interfaceMappings interface mappings
     * @param additionalDataMapping additional data mapping
     */
    public SimplePacketToCallResponseMapping(
            PacketToValueMapping networkMapping, 
            PacketToValueMapping nodeMapping, 
            Map<Class, PacketToInterfaceMapping> interfaceMappings,
            PacketToValueMapping additionalDataMapping
    ) {
        this.networkMapping = networkMapping;
        this.nodeMapping = nodeMapping;
        this.interfaceMappings = interfaceMappings;
        this.additionalDataMapping = additionalDataMapping;
    }
    
    @Override
    public Set<Class> getSupportedDeviceInterfaces() {
        return interfaceMappings.keySet();
    }
    
    @Override
    public String getNetworkId(short[] packet) throws ValueConversionException {
        logger.debug("getNetworkId - start: packet={}", packet);
        
        String networkId = (String)Deserializer.deserialize(networkMapping, packet);
        
        logger.debug("getNetworkId - end: {}", networkId);
        return networkId;
    }
    
    @Override
    public String getNodeId(short[] packet) throws ValueConversionException {
        logger.debug("getNodeId - start: packet={}", packet);
        
        String nodeId = (String)Deserializer.deserialize(nodeMapping, packet);
        
        logger.debug("getNodeId - end: {}", nodeId);
        return nodeId;
    }
    
    
    @Override
    public Class getDeviceInterface(short[] packet) {
        logger.debug("getDeviceInterface - start: packet={}", packet);
        
        int bestEquality = 0;
        Class devIface = null;
        
        for (PacketToInterfaceMapping ifaceMapping : interfaceMappings.values()) {
            List<PacketPositionValues> packetValues = ifaceMapping.getPacketValues();
            if (isCompatible(packet, packetValues)) {
                if (packetValues.size() > bestEquality) {
                    bestEquality = packetValues.size();
                    devIface = ifaceMapping.getDeviceInterface();
                }
            }
        }
        
        logger.debug("getDeviceInterface - end: {}", devIface);
        return devIface;
    }
    
    @Override
    public String getMethodId(Class devInterface, short[] packet) {
        logger.debug("getMethodId - start: devInterface={}, packet={}", 
                devInterface, packet
        );
        
        PacketToInterfaceMapping ifaceMapping = interfaceMappings.get(devInterface);
        if (ifaceMapping == null) {
            logger.warn("Interface mapping not found");
            logger.debug("getMethodId - end: null");
            return null;
        }
        
        int bestEquality = 0;
        String methodId = null;
        
        Collection<PacketToMethodMapping> methodMappings = ifaceMapping.getMethodMappings();
        for (PacketToMethodMapping methodMapping : methodMappings) {
            List<PacketPositionValues> patterns = methodMapping.getPacketValues();
            if (isCompatible(packet, patterns)) {
                if (isResultCompatible(packet, methodMapping.getResultMapping())) {
                    if (patterns.size() > bestEquality) {
                        bestEquality = patterns.size();
                        methodId = methodMapping.getMethodId();
                    }
                }  
            }
        }
        
        logger.debug("getMethodId - end: {}", methodId);
        return methodId;
    }
    
    @Override
    public Object getMethodResult(
            Class devInterface, String methodId, short[] protoMsg
    ) throws ValueConversionException {
        Object logArgs[] = new Object[3];
        logArgs[0] = devInterface;
        logArgs[1] = methodId;
        logArgs[2] = protoMsg;
        logger.debug("getMethodResult - start: devInterface={}, methodId={},"
                + "protoMsg={}", logArgs
        );
        
        PacketToInterfaceMapping ifaceMapping = interfaceMappings.get(devInterface);
        if (ifaceMapping == null) {
            logger.warn("Interface mapping not found");
            logger.debug("getMethodResult - end: null");
            return null;
        }
        
        PacketToMethodMapping methodMapping = ifaceMapping.getMethodMapping(methodId);
        if (methodMapping == null) {
            logger.warn("Method mapping not found");
            logger.debug("getMethodResult - end: null");
            return null;
        }
        
        PacketToValueMapping resultMapping = methodMapping.getResultMapping();
        Object result = Deserializer.deserialize(resultMapping, protoMsg);
        
        logger.debug("getMethodResult - end: {}", result);
        return result;
    }
    
    @Override
    public Object getAdditionalData(short[] packet) throws ValueConversionException 
    {
        logger.debug("getAdditionalData - start: packet={}", packet);
        
        Object additionalData = Deserializer.deserialize(additionalDataMapping, packet);
        
        logger.debug("getAdditionalData - end: {}", additionalData);
        return additionalData;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "node mapping=" + nodeMapping + 
                ", interface mappings=" + interfaceMappings +
                ", additional data mapping=" + additionalDataMapping +
                " }");
    }

}
