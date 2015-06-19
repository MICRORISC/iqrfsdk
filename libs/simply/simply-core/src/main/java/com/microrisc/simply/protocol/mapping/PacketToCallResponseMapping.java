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

import com.microrisc.simply.typeconvertors.ValueConversionException;
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
