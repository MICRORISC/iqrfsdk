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

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Stores mapping of protocol packet into Device Interface. 
 * 
 * @author Michal Konopa
 */
public final class PacketToInterfaceMapping {
    /** Device Interface, which is descibed by this mapping. */
    private Class devInterface = null;
    
    /** Packet values. */
    private List<PacketPositionValues> packetValues = null;
    
    /** Device Interface's methods mappings. */
    private Map<String, PacketToMethodMapping> methodMappings = null;
    
    
    /**
     * Constructor.
     * @param devInterface Device Interface
     * @param packetValues values of bytes of packet
     * @param methodMappings methods mappings
     */
    public PacketToInterfaceMapping(Class devInterface, List<PacketPositionValues> packetValues, 
            Map<String, PacketToMethodMapping> methodMappings) {
        this.devInterface = devInterface;
        this.packetValues = packetValues;
        this.methodMappings = methodMappings;
    } 
    
    /**
     * @return Device Interface, which is descibed by this mapping 
     */
    Class getDeviceInterface() {
        return devInterface;
    }
    
    /**
     * Returns method mapping corresponding to specified method ID.
     * @param methodId ID of method, whose mapping to return
     * @return method mapping corresponding to specified method ID
     * @return {@code null}, if no mapping was found
     */
    PacketToMethodMapping getMethodMapping(String methodId) {
        return methodMappings.get(methodId);
    }
    
    /**
     * Returns this interface packet values.
     * @return packet values of this interface
     */
    List<PacketPositionValues> getPacketValues() {
        return packetValues;
    }

    Collection<PacketToMethodMapping> getMethodMappings() {
        return methodMappings.values();
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "device interface=" + devInterface + 
                ", packet values=" + packetValues +
                ", method mappings=" + methodMappings +
                " }");
    }
}
