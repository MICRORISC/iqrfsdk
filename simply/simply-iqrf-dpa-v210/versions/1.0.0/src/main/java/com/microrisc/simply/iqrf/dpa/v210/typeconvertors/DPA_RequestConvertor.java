

package com.microrisc.simply.iqrf.dpa.v210.typeconvertors;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v210.di_services.method_id_transformers.StandardMethodIdTransformers;
import com.microrisc.simply.iqrf.dpa.protocol.ProtocolObjects;
import com.microrisc.simply.iqrf.dpa.v210.types.DPA_Request;
import com.microrisc.simply.protocol.RequestPacketCreator;
import com.microrisc.simply.protocol.mapping.PacketFragment;
import com.microrisc.simply.protocol.mapping.ProtocolMappingException;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting DPA request to protocol packets.
 * 
 * @author Michal Konopa
 */
public final class DPA_RequestConvertor extends AbstractConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_RequestConvertor.class);
    
    private DPA_RequestConvertor() {}
    
    /** Singleton - for Standard method ID transformer only. */
    private static final DPA_RequestConvertor instance = new DPA_RequestConvertor();
    
    
    /**
     * @return {@code DPA_RequestConvertor} instance
     */
    static public DPA_RequestConvertor getInstance() {
        return instance;
    }
    
    private String getStringMethodId(DPA_Request dpaRequest) { 
        MethodIdTransformer methodIdTransformer = dpaRequest.getMethodTransformer();
        if ( methodIdTransformer == null ) {
            methodIdTransformer = StandardMethodIdTransformers.getInstance().
                    getTransformer(dpaRequest.getDeviceInterface());
            if ( methodIdTransformer == null ) {
                throw new IllegalArgumentException(
                        "Method ID transformer not found for" + dpaRequest.getDeviceInterface()
                );
            }
        }
        return methodIdTransformer.transform(dpaRequest.getMethodId());
    }
    
    private List<PacketFragment> subtractStartingPositions(
            List<PacketFragment> argFragments, int shift
    ) {
        List<PacketFragment> subtractedFragments = new LinkedList<>();
        for ( PacketFragment argFragment : argFragments ) {
            subtractedFragments.add( new PacketFragment( 
                    argFragment.getStartingPosition()-shift, argFragment.getData()
                )
            );
        }
        return subtractedFragments;
    }
    
    private short getPNUM(DPA_Request dpaRequest) {
        String pNumStr = ProtocolObjects.getPeripheralToDevIfaceMapper().
                getPeripheralId(dpaRequest.getDeviceInterface());
        if ( pNumStr == null ) {
            throw new IllegalArgumentException(
                "Peripheral ID not found for" + dpaRequest.getDeviceInterface()
            );
        }
        return Short.decode(pNumStr);
    }
    
    private short getPCMD(DPA_Request dpaRequest) {
        return Short.decode(getStringMethodId(dpaRequest));
    }
    
    private short[] getSerializedRequest(DPA_Request dpaRequest) 
            throws ProtocolMappingException, ValueConversionException 
    {
        String strMethodId = getStringMethodId(dpaRequest);
        Object[] argsWithHwProfile = null;
        if ( dpaRequest.getArgs() == null ) {
            argsWithHwProfile = new Object[] { dpaRequest.getHwProfile() };
        } else {
            argsWithHwProfile = new Object[ dpaRequest.getArgs().length + 1 ];
            argsWithHwProfile[0] = dpaRequest.getHwProfile();
            System.arraycopy( dpaRequest.getArgs(), 0, argsWithHwProfile, 1, dpaRequest.getArgs().length );
        }
   
        List<PacketFragment> argFragments = ProtocolObjects.getProtocolMapping().
                getCallRequestToPacketMapping().getSerializedMethodArgs( 
                        dpaRequest.getDeviceInterface(), strMethodId, argsWithHwProfile
        );
        
        // ATTENTION: all starting position of all argument packet fragments must
        // be subtracted by 1 because batched request has no NADR field
        List<PacketFragment> subtractedArgFragments = subtractStartingPositions(argFragments, 1);
        
        List<PacketFragment> allFragments = new LinkedList<>();
        allFragments.add( new PacketFragment(0, new short[] { (short) (0) } ));
        allFragments.add( new PacketFragment(1, new short[] { getPNUM(dpaRequest) } ));
        allFragments.add( new PacketFragment(2, new short[] { getPCMD(dpaRequest) } ));
        allFragments.addAll(subtractedArgFragments);
   
        short[] serRequest = RequestPacketCreator.createRequestPacket(allFragments);
        
        // write the right value of length of the packet
        serRequest[0] = (short)serRequest.length;
        return serRequest;
    }
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if ( !(value instanceof DPA_Request) ) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        DPA_Request dpaRequest = (DPA_Request) value;
        
        short[] serRequest = null;
        try {
            serRequest = getSerializedRequest(dpaRequest);
        } catch ( ProtocolMappingException ex ) {
            throw new ValueConversionException("Value to convert has not proper type.", ex);
        }
         
        logger.debug("toProtoValue - end: {}", serRequest);
        return serRequest; 
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
