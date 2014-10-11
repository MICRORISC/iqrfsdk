
package com.microrisc.simply.protocol.mapping;

/**
 * Main interface to protocol mapping features. 
 * 
 * @author Michal Konopa
 */
public interface ProtocolMapping {
    /**
     * @return mapping of call request to packet of protocol layer
     */
    CallRequestToPacketMapping getCallRequestToPacketMapping();

    /**
     * @return mapping of protocol layer packet to call response
     */
    PacketToCallResponseMapping getPacketToCallResponseMapping();
    
}
