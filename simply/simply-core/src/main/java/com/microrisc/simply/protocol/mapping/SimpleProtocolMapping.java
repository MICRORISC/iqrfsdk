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

/**
 * Encapsulates mapping of Device Interfaces methods data into protocol layer 
 * packets and vice versa.
 * 
 * @author Michal Konopa
 */
public final class SimpleProtocolMapping implements ProtocolMapping {
    /** Call request to packet mapping. */
    private CallRequestToPacketMapping callRequestToPacketMapping = null;
    
    /** Packet to call response mapping. */
    private PacketToCallResponseMapping packetToCallResponseMapping = null;
    
    
    /**
     * Constructor.
     * @param callRequestToPacketMapping call request to packet mapping
     * @param packetToCallResponseMapping packet to call response mapping
     */
    public SimpleProtocolMapping(
            CallRequestToPacketMapping callRequestToPacketMapping,
            PacketToCallResponseMapping packetToCallResponseMapping
    ) {
        this.callRequestToPacketMapping = callRequestToPacketMapping;
        this.packetToCallResponseMapping = packetToCallResponseMapping;
    }
    
    /**
     * @return mapping of protocol layer packet to call response
     */
    @Override
    public PacketToCallResponseMapping getPacketToCallResponseMapping() {
        return packetToCallResponseMapping;
    }
    
    /**
     * @return mapping of call request to packet of protocol layer 
     */
    @Override
    public CallRequestToPacketMapping getCallRequestToPacketMapping() {
        return callRequestToPacketMapping;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "call request to packet mapping=" + callRequestToPacketMapping + 
                ", packet to call response mapping=" + packetToCallResponseMapping + 
                " }");
    }
}
