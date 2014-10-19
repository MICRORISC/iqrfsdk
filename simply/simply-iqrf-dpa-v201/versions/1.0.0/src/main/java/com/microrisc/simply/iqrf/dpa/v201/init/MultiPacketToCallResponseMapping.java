

package com.microrisc.simply.iqrf.dpa.v201.init;

import com.microrisc.simply.protocol.mapping.PacketToCallResponseMapping;
import com.microrisc.simply.types.ValueConversionException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Call response mapping which puts together other call response mappings.
 * Each of the 'source' call response mapping must support disjunctive set of Device
 * Interfaces from each other mapping.
 * 
 * @author Michal Konopa
 */
final class MultiPacketToCallResponseMapping implements PacketToCallResponseMapping {
    private PacketToCallResponseMapping[] mappings;
    private Map<Class, PacketToCallResponseMapping> classMapping;
    
            
    // sorts specified call request mappings according to supported classes
    private void sortMappingsAccordingToSuppClasses( PacketToCallResponseMapping[] mappings ) {
        classMapping = new HashMap<Class, PacketToCallResponseMapping>();
        
        for ( PacketToCallResponseMapping mapping : mappings ) {
            Set<Class> supportedDevIfaces = mapping.getSupportedDeviceInterfaces();
            for ( Class supportedDevIface : supportedDevIfaces ) {
                if ( classMapping.containsKey(supportedDevIface) ) {
                    throw new IllegalArgumentException(
                            "Packet to response mappings haven't discjunctive"
                            + " supported sets of Device Interfaces: " + supportedDevIface
                    );
                }
                classMapping.put(supportedDevIface, mapping);
            }
        }
    }
    
    private PacketToCallResponseMapping[] checkPacketToResponseMappings( 
            PacketToCallResponseMapping[] mappings 
    ) {
        if ( mappings == null ) {
            throw new IllegalArgumentException("Packet to response mappings cannot be null");
        }
        
        if ( mappings.length == 0 ) {
            throw new IllegalArgumentException("Packet to response mappings cannot be empty");
        }
        return mappings;
    }
    
    private void copyMappings(PacketToCallResponseMapping[] mappings) {
        List<PacketToCallResponseMapping> mappingList = new LinkedList<PacketToCallResponseMapping>();
        for ( PacketToCallResponseMapping mapping : mappings ) {
            if ( mapping != null ) {
                mappingList.add(mapping);
            }
        }
        this.mappings = mappingList.toArray(new PacketToCallResponseMapping[] {} );
    }
    
    
    /**
     * Creates new multi response mapping constitued from specified mappings 
     * @param mappings source response mappings
     */
    public MultiPacketToCallResponseMapping( PacketToCallResponseMapping[] mappings ) {
        mappings = checkPacketToResponseMappings(mappings);
        copyMappings(mappings);
        sortMappingsAccordingToSuppClasses(this.mappings);
    }
    
    @Override
    public Set<Class> getSupportedDeviceInterfaces() {
        return classMapping.keySet();
    }
    
    @Override
    public String getNetworkId(short[] packet) throws ValueConversionException {
        return mappings[0].getNetworkId(packet);
    }

    @Override
    public String getNodeId(short[] packet) throws ValueConversionException {
        return mappings[0].getNodeId(packet);
    }

    @Override
    public Class getDeviceInterface(short[] packet) throws ValueConversionException {
        Class foundClass = null;
        for ( PacketToCallResponseMapping packetMapping : mappings ) {
            Class suppClass = packetMapping.getDeviceInterface(packet);
            if ( suppClass != null ) {
                if ( foundClass != null ) {
                    throw new ValueConversionException(
                            "Class resolution ambiguous: " + foundClass + " vs. " + suppClass
                    );
                } else {
                    foundClass = suppClass;
                }
            }
        }
        return foundClass;
    }

    @Override
    public String getMethodId(Class devInterface, short[] packet) throws ValueConversionException {
        PacketToCallResponseMapping packetMapping = classMapping.get(devInterface);
        if ( packetMapping == null ) {
            throw new ValueConversionException("Device Interface not supported: " + devInterface);
        }
        return packetMapping.getMethodId(devInterface, packet);
    }

    @Override
    public Object getMethodResult(Class devInterface, String methodId, short[] protoMsg) 
            throws ValueConversionException {
        PacketToCallResponseMapping packetMapping = classMapping.get(devInterface);
        if ( packetMapping == null ) {
            throw new ValueConversionException("Device Interface not supported: " + devInterface);
        }
        return packetMapping.getMethodResult(devInterface, methodId, protoMsg);
    }

    @Override
    public Object getAdditionalData(short[] protoMsg) throws ValueConversionException {
        return mappings[0].getAdditionalData(protoMsg);
    }
    
}
