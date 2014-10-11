
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
