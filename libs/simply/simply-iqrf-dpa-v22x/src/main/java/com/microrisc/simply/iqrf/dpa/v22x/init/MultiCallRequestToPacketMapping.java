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

package com.microrisc.simply.iqrf.dpa.v22x.init;

import com.microrisc.simply.protocol.mapping.CallRequestToPacketMapping;
import com.microrisc.simply.protocol.mapping.PacketFragment;
import com.microrisc.simply.protocol.mapping.ProtocolMappingException;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Call request mapping which puts together other call request mappings.
 * Each of the 'source' call request mapping must support disjunctive set of Device
 * Interfaces from each other mapping.
 * 
 * @author Michal Konopa
 */
final class MultiCallRequestToPacketMapping implements CallRequestToPacketMapping {
    private CallRequestToPacketMapping[] mappings;
    private Map<Class, CallRequestToPacketMapping> classMapping;
    
            
    // sorts specified call request mappings according to supported classes
    private void sortMappingsAccordingToSuppClasses( CallRequestToPacketMapping[] mappings ) {
        classMapping = new HashMap<Class, CallRequestToPacketMapping>();
        
        for ( CallRequestToPacketMapping mapping : mappings ) {
            Set<Class> supportedDevIfaces = mapping.getSupportedDeviceInterfaces();
            for ( Class supportedDevIface : supportedDevIfaces ) {
                if ( classMapping.containsKey(supportedDevIface) ) {
                    throw new IllegalArgumentException(
                            "Call request mappings haven't discjunctive"
                            + " supported sets of Device Interfaces: " + supportedDevIface
                    );
                }
                classMapping.put(supportedDevIface, mapping);
            }
        }
    }
    
    private CallRequestToPacketMapping[] checkCallRequestMappings( 
            CallRequestToPacketMapping[] mappings 
    ) {
        if ( mappings == null ) {
            throw new IllegalArgumentException("Call request mappings cannot be null");
        }
        
        if ( mappings.length == 0 ) {
            throw new IllegalArgumentException("Call request mappings cannot be empty");
        }
        return mappings;
    }
    
    private void copyMappings(CallRequestToPacketMapping[] mappings) {
        List<CallRequestToPacketMapping> mappingList = new LinkedList<CallRequestToPacketMapping>();
        for ( CallRequestToPacketMapping mapping : mappings ) {
            if ( mapping != null ) {
                mappingList.add(mapping);
            }
        }
        this.mappings = mappingList.toArray(new CallRequestToPacketMapping[] {} );
    }
    
    
    /**
     * Creates new multi call request mapping constitued from specified mappings 
     * @param mappings source call request mappings
     */
    public MultiCallRequestToPacketMapping( CallRequestToPacketMapping[] mappings ) {
        mappings = checkCallRequestMappings(mappings);
        copyMappings(mappings);
        sortMappingsAccordingToSuppClasses(this.mappings);
    }
    
    @Override
    public Set<Class> getSupportedDeviceInterfaces() {
        return classMapping.keySet();
    }
    
    @Override
    public List<PacketFragment> getSerializedNetworkData(String networkId) 
            throws ValueConversionException {
        return mappings[0].getSerializedNetworkData(networkId);
    }

    @Override
    public List<PacketFragment> getSerializedNodeData(String nodeId) 
            throws ValueConversionException {
        return mappings[0].getSerializedNodeData(nodeId);
    }

    @Override
    public List<PacketFragment> getSerializedInterfaceData(Class devInterface) 
            throws ProtocolMappingException {
        CallRequestToPacketMapping requestMapping = classMapping.get(devInterface);
        if ( requestMapping == null ) {
            throw new ProtocolMappingException("Device Interface not supported: " + devInterface);
        }
        return requestMapping.getSerializedInterfaceData(devInterface);
    }

    @Override
    public List<PacketFragment> getSerializedMethodArgs(
            Class devInterface, String methodId, Object[] args
    ) throws ProtocolMappingException, ValueConversionException {
        CallRequestToPacketMapping requestMapping = classMapping.get(devInterface);
        if ( requestMapping == null ) {
            throw new ProtocolMappingException("Device Interface not supported: " + devInterface);
        }
        return requestMapping.getSerializedMethodArgs(devInterface, methodId, args);
    }

    @Override
    public List<PacketFragment> getSerializedMethodData(
            Class devInterface, String methodId
    ) throws ProtocolMappingException {
        CallRequestToPacketMapping requestMapping = classMapping.get(devInterface);
        if ( requestMapping == null ) {
            throw new ProtocolMappingException("Device Interface not supported: " + devInterface);
        }
        return requestMapping.getSerializedMethodData(devInterface, methodId);
    }

    @Override
    public List<PacketFragment> getSerializedProtocolData() {
        return mappings[0].getSerializedProtocolData();
    }

    
}
