
package com.microrisc.simply.protocol.mapping;

import com.microrisc.simply.types.ValueConversionException;
import java.util.List;
import java.util.Set;

/**
 * Interface for access a functionality of mapping of call request to protocol layer 
 * packet.
 * 
 * @author Michal Konopa
 */
public interface CallRequestToPacketMapping {
    /**
     * Returns set of supported Device Interfaces.
     * @return set of supported Device Interfaces.
     */
    Set<Class> getSupportedDeviceInterfaces();
    
    /**
     * Serializes specified network ID and returns it.
     * @param networkId network ID
     * @return serialized network data
     * @throws ValueConversionException if some error has occurred during serialization
     */
    List<PacketFragment> getSerializedNetworkData(String networkId) throws ValueConversionException;
    
    /**
     * Serializes specified Node ID and returns it.
     * @param nodeId node ID
     * @return serialized node data
     * @throws ValueConversionException if some error has occurred during serialization
     */
    List<PacketFragment> getSerializedNodeData(String nodeId) throws ValueConversionException;
    
    /**
     * Serializes mapping data of specified Device Interface and returns it.
     * @param devInterface Device Interface
     * @return serialized Device Interface data
     * @throws ProtocolMappingException if some error has occurred
     */
    List<PacketFragment> getSerializedInterfaceData(Class devInterface) 
            throws ProtocolMappingException;

    /**
     * Serializes specified method arguments and returns it.
     * @param devInterface Device Interface, which the method belongs to
     * @param methodId method ID
     * @param args method arguments
     * @return serialized method arguments
     * @throws ProtocolMappingException if some error has occurred during mapping
     * @throws ValueConversionException if some error has occurred during agrument
     *         conversion to packet
     */
    List<PacketFragment> getSerializedMethodArgs(
            Class devInterface, String methodId, Object[] args
    ) throws ProtocolMappingException, ValueConversionException;

    /**
     * Serializes mapping data of specified method and returns it.
     * @param devInterface Device Interface, which the method belongs to
     * @param methodId method ID
     * @return serialized method data
     * @throws ProtocolMappingException if some error has occurred
     */
    List<PacketFragment> getSerializedMethodData(Class devInterface, String methodId) 
            throws ProtocolMappingException;

    /**
     * @return serialized protocol data
     */
    List<PacketFragment> getSerializedProtocolData();
    
}
