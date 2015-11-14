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

import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;
import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.BaseCallResponse;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.SimpleMessageSource;
import com.microrisc.simply.SimpleMethodMessageSource;
import com.microrisc.simply.errors.NetworkInternalError;
import com.microrisc.simply.iqrf.dpa.DPA_ResponseCode;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastRequest;
import com.microrisc.simply.iqrf.dpa.v22x.devices.PeripheralInfoGetter;
import com.microrisc.simply.protocol.RequestPacketCreator;
import com.microrisc.simply.protocol.SimpleMessageConvertor;
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
 * Interface for conversion between Protocol Layer and Network Layer messages.
 * 
 * @author Michal Konopa
 */
public final class DPA_MessageConvertor extends SimpleMessageConvertor { 
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_MessageConvertor.class);
    
    /** Broadcast address. */
    private static final int BROADCAST_ADDRESS = 0xFF;
    
    /**
     * Indicates if specified packet is from Device Exploration process.
     * @param msg data of the packet
     * @return {@code true} if specified packet is from Device Exploration process <br>
     *         {@code false} otherwise
     */
    private boolean isDeviceExploration(short[] msg) {
        int pNum = DPA_ProtocolProperties.getPeripheralNumber(msg);
        int pCmd = DPA_ProtocolProperties.getCommand(msg);
        
        if ( pNum == BROADCAST_ADDRESS ) {
            return true;
        }
        return  ( pCmd == 0xBF );
    }
    
    
    public DPA_MessageConvertor(ProtocolMapping protocolMapping) {
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
        
        List<PacketFragment> serNodeData = null;
        
        if ( callRequest instanceof BroadcastRequest ) {
            serNodeData = reqToPacketMapping.getSerializedNodeData(String.valueOf(BROADCAST_ADDRESS));
        } else {
            serNodeData = reqToPacketMapping.getSerializedNodeData(callRequest.getNodeId());
        }
        
        List<PacketFragment> serIfaceData = reqToPacketMapping.getSerializedInterfaceData(
                callRequest.getDeviceInterface()
        );
        List<PacketFragment> serMethodData = reqToPacketMapping.getSerializedMethodData(
                callRequest.getDeviceInterface(), callRequest.getMethodId());
        List<PacketFragment> serMethodArgs = reqToPacketMapping.getSerializedMethodArgs(
                callRequest.getDeviceInterface(), callRequest.getMethodId(), callRequest.getArgs());
        
        List<PacketFragment> allFragments = new LinkedList<>();
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
            throws ValueConversionException {
        logger.debug("convertToDOFormat - start: networkData={}", networkData);
        
        PacketToCallResponseMapping devObjMapping = 
                protocolMapping.getPacketToCallResponseMapping();
        
        String networkId = networkData.getNetworkId();
        if ( networkId == null ) {
            throw new ValueConversionException("Network Id was not found");
        }
        
        // protocol data
        short[] protoMsg = networkData.getData();
        
        String nodeId = devObjMapping.getNodeId(protoMsg);
        if ( nodeId == null ) {
            throw new ValueConversionException(
                "Node Id was not found, source packet: " + Arrays.toString(protoMsg)
            );
        }
        
        Class devIface = null;
        if ( isDeviceExploration(protoMsg) ) {
            devIface = PeripheralInfoGetter.class;
        } else {
            devIface = devObjMapping.getDeviceInterface(protoMsg);
            if ( devIface == null ) {
                throw new ValueConversionException(
                    "Device Interface was not found source packet: " + Arrays.toString(protoMsg)
                );
            }
        }
        
        String methodId = devObjMapping.getMethodId(devIface, protoMsg);
        if ( methodId == null ) {
            throw new ValueConversionException(
                "Method was not found, source packet: " + Arrays.toString(protoMsg)
            );
        }
        
        Object additionalData = devObjMapping.getAdditionalData(protoMsg);
        
        BaseCallResponse.MethodMessageSource respSource = 
                new SimpleMethodMessageSource( new SimpleMessageSource(networkId, nodeId), 
                devIface, methodId
        );
        
        // determining response code
        DPA_ResponseCode responseCode = DPA_ProtocolProperties.getResponseCode(networkData.getData());
        
        // check response code for errors
        if ( responseCode != DPA_ResponseCode.NO_ERROR ) {
                return new BaseCallResponse(
                        additionalData, respSource, 
                        new NetworkInternalError("Response code = " + responseCode)
                );
        }
        
        Object methodResult = devObjMapping.getMethodResult(devIface, methodId, protoMsg);
        if ( methodResult == null ) {
            throw new ValueConversionException(
                "Method result was not found, source packet: " + Arrays.toString(protoMsg)
            );
        }
        
        BaseCallResponse response = new BaseCallResponse(methodResult, additionalData, respSource);
        
        logger.debug("convertToDOFormat - end: {}", response);
        return response;
    }
}
