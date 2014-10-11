
package com.microrisc.simply.protocol.mapping;

import com.microrisc.simply.types.ValueConversionException;
import java.util.Set;

/**
 * Interface for access a functionality of mapping of protocol layer packet to 
 * call response data.
 * 
 * @author Michal Konopa
 */
public interface PacketToCallResponseMapping {
    /**
     * Returns set of supported Device Interfaces.
     * @return set of supported Device Interfaces.
     */
    Set<Class> getSupportedDeviceInterfaces();
    
    /**
     * Returns network ID from specified protocol packet.
     * @param packet source protocol packet
     * @return network ID <br>
     *         {@code null}, if network Id was not determined
     * @throws ValueConversionException if some conversion error encountered
     */
    String getNetworkId(short[] packet) throws ValueConversionException;
    
    /**
     * Returns node ID from specified protocol packet.
     * @param packet source protocol packet
     * @return node ID <br>
     *         {@code null}, if Node Id was not determined
     * @throws ValueConversionException if some conversion error encountered
     */
    String getNodeId(short[] packet) throws ValueConversionException;
    
    /**
     * Returns Device Interface from specified protocol packet.
     * @param packet source protocol packet
     * @return Device Interface <br>
     *         {@code null}, if no Device Interface was found
     * @throws ValueConversionException if some conversion error encountered
     */
    Class getDeviceInterface(short[] packet) throws ValueConversionException;

    /**
     * Returns method ID from specified Device Interface and protocol packet.
     * @param devInterface Device Interface, which the method belongs to
     * @param packet source protocol packet
     * @return method ID <br>
     *         {@code null}, if no method was found
     * @throws ValueConversionException if some conversion error encountered
     */
    String getMethodId(Class devInterface, short[] packet) throws ValueConversionException;

    /**
     * Returns deserialized result of specified method.
     * @param devInterface Device Interface, which the method belongs to
     * @param methodId ID of the method
     * @param protoMsg source protocol packet
     * @return deserialized result
     * @throws ValueConversionException if some conversion error encountered
     */
    Object getMethodResult(Class devInterface, String methodId, short[] protoMsg) 
            throws ValueConversionException;
    
    /**
     * Returns deserialized additional data.
     * @param protoMsg source protocol packet
     * @return deserialized additional data.
     * @throws ValueConversionException if some conversion error encountered
     */
    public Object getAdditionalData(short[] protoMsg) throws ValueConversionException;
}
