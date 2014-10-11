
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
