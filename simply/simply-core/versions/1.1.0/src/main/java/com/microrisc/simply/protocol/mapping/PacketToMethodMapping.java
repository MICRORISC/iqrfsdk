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

import java.util.List;

/**
 * Stores mapping of Device Interface's method.
 * @author Michal Konopa
 */
public final class PacketToMethodMapping {
    /** Method ID. */
    private String methodId = null;
    
    /** Packet value patterns. */
    private List<PacketPositionValues> packetValues = null;
    
    /** Method result mapping. */
    private PacketToValueMapping resultMapping = null;
    
    
    /**
     * Constructor.
     * @param methodId method ID
     * @param packetValues packet values
     * @param resultMapping mapping of method's result
     */
    public PacketToMethodMapping(String methodId, List<PacketPositionValues> packetValues, 
            PacketToValueMapping resultMapping
    ) {
        this.methodId = methodId;
        this.packetValues = packetValues;
        this.resultMapping = resultMapping;
    }
    
    /**
     * @return method ID 
     */
    String getMethodId() {
        return methodId;
    }
    
    /**
     * @return packet values of this mapping.
     */
    List<PacketPositionValues> getPacketValues() {
        return packetValues;
    }
    
    /**
     * @return method result mapping
     */
    PacketToValueMapping getResultMapping() {
        return resultMapping;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "method ID=" + methodId + 
                ", packet values=" + packetValues +
                ", result mappings=" + resultMapping +
                " }");
    }
}
