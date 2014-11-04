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

package com.microrisc.simply.protocol;

import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.BaseCallResponse;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.SimpleMessageSource;
import com.microrisc.simply.SimpleMethodMessageSource;
import com.microrisc.simply.protocol.mapping.CallRequestToPacketMapping;
import com.microrisc.simply.protocol.mapping.PacketFragment;
import com.microrisc.simply.protocol.mapping.PacketToCallResponseMapping;
import com.microrisc.simply.protocol.mapping.ProtocolMapping;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of {@code AbstractMessageConvertor} class.
 * 
 * @author Michal Konopa
 */
public class SimpleMessageConvertor extends AbstractMessageConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SimpleMessageConvertor.class);
    
    
    /**
     * Creates new simple message convertor, which use specified protocol mapping. 
     * @param protocolMapping protocol mapping to use
     */
    public SimpleMessageConvertor(ProtocolMapping protocolMapping) {
        super(protocolMapping);
    }
    
    @Override
    public short[] convertToProtoFormat(CallRequest callRequest) throws SimplyException {
        logger.debug("convertToProtoFormat - start: callRequest={}", callRequest);
        
        CallRequestToPacketMapping reqToPacketMapping = protocolMapping.
                getCallRequestToPacketMapping();  
        List<PacketFragment> serProtoData = reqToPacketMapping.getSerializedProtocolData();
        List<PacketFragment> serNetworkData = reqToPacketMapping.
                getSerializedNetworkData(callRequest.getNetworkId()
        );
        List<PacketFragment> serNodeData = reqToPacketMapping.getSerializedNodeData(
                callRequest.getNodeId()
        );
        List<PacketFragment> serIfaceData = reqToPacketMapping.getSerializedInterfaceData(
                callRequest.getDeviceInterface()
        );
        List<PacketFragment> serMethodData = reqToPacketMapping.getSerializedMethodData(
                callRequest.getDeviceInterface(), callRequest.getMethodId());
        List<PacketFragment> serMethodArgs = reqToPacketMapping.getSerializedMethodArgs(
                callRequest.getDeviceInterface(), callRequest.getMethodId(), callRequest.getArgs());
        
        List<PacketFragment> allFragments = new LinkedList<PacketFragment>();
        allFragments.addAll(serProtoData);
        allFragments.addAll(serNetworkData);
        allFragments.addAll(serNodeData);
        allFragments.addAll(serIfaceData);
        allFragments.addAll(serMethodData);
        allFragments.addAll(serMethodArgs);
        
        short[] requestPacket = RequestPacketCreator.createRequestPacket(allFragments);
        
        logger.debug("convertToProtoFormat - end: {}", requestPacket);
        return requestPacket;
    }

    @Override
    public AbstractMessage convertToDOFormat(NetworkData networkData) 
            throws SimplyException {
        logger.debug("convertToDOFormat - start: networkData={}", networkData);
        
        PacketToCallResponseMapping devObjMapping = 
                protocolMapping.getPacketToCallResponseMapping();
        
        // protocol message
        short[] protoMsg = networkData.getData();
        
        String networkId = devObjMapping.getNetworkId(protoMsg);
        if ( networkId == null ) {
            throw new ValueConversionException("Network Id was not found, source packet: " + Arrays.toString(protoMsg));
        }
        
        String nodeId = devObjMapping.getNodeId(protoMsg);
        if ( nodeId == null ) {
            throw new ValueConversionException("Node Id was not found, source packet: " + Arrays.toString(protoMsg));
        }
        
        Class devIface = devObjMapping.getDeviceInterface(protoMsg);
        if ( devIface == null ) {
            throw new ValueConversionException(
                "Device Interface was not found, source packet: " + Arrays.toString(protoMsg)
            );
        }
        
        String methodId = devObjMapping.getMethodId(devIface, protoMsg);
        if ( methodId == null ) {
            throw new ValueConversionException("Method was not found, source packet: " + Arrays.toString(protoMsg));
        }
        
        Object methodResult = devObjMapping.getMethodResult(devIface, methodId, protoMsg);
        if ( methodResult == null ) {
            throw new ValueConversionException(
                "Method result was not found, source packet: " + Arrays.toString(protoMsg)
            );
        }
        
        Object additionalData = devObjMapping.getAdditionalData(protoMsg);
        
        BaseCallResponse.MethodMessageSource respSource = 
                new SimpleMethodMessageSource(new SimpleMessageSource(networkId, nodeId), 
                devIface, methodId
        );
        BaseCallResponse response = new BaseCallResponse(
                methodResult, additionalData, respSource
        );
        
        logger.debug("convertToDOFormat - end: {}", response);
        return response;
    } 
}
