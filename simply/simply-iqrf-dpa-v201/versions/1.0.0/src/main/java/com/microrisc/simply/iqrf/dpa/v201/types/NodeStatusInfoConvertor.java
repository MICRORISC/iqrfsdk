
package com.microrisc.simply.iqrf.dpa.v201.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code node_status_info} type values 
 * to {@code NodeStatusInfo} objects. 
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public final class NodeStatusInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(NodeStatusInfoConvertor.class);
    
    private NodeStatusInfoConvertor() {}
    
    /** Singleton. */
    private static final NodeStatusInfoConvertor instance = new NodeStatusInfoConvertor();
    
    /**
     * @return NodeStatusInfoConvertor instance 
     */
    static public NodeStatusInfoConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 12;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    // postitions of fields
    static private final int ADDRESS_POS = 0;
    static private final int VRN_POS = 1;
    static private final int ZONE_INDEX_POS = 2;
    static private final int NTW_DID_POS = 3;
    static private final int PARENT_VRN_POS = 4;
    static private final int USER_ADDRESS_POS = 5;
    static private final int NETWORK_ID_POS = 7;
    static private final int VRN_FIRSTINZONE_POS = 9;
    static private final int NETWORK_CONFIGURATION_POS = 10;
    
    static private final int FLAGS_POS = 11;
    
    
    

    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @param value
     * @return
     * @throws ValueConversionException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        int address = protoValue[ADDRESS_POS];
        int vrn = protoValue[VRN_POS];
        int zoneIndex = protoValue[ZONE_INDEX_POS];
        int ntwDIDPos = protoValue[NTW_DID_POS];
        int parentVrn = protoValue[PARENT_VRN_POS];
        
        int userAddress = protoValue[USER_ADDRESS_POS];
        userAddress |= (protoValue[USER_ADDRESS_POS+1] << 8);
        
        short[] moduleId = new short[2];
        System.arraycopy(protoValue, NETWORK_ID_POS, moduleId, 0, 2);

        int vrnFirstNodeinZone = protoValue[VRN_FIRSTINZONE_POS];
        int networkConfiguration = protoValue[NETWORK_CONFIGURATION_POS];
        
        int flags = protoValue[FLAGS_POS];
        
        NodeStatusInfo nodeStatusInfo = new NodeStatusInfo(
            address, vrn, zoneIndex, ntwDIDPos, parentVrn, userAddress, moduleId,
                vrnFirstNodeinZone, networkConfiguration, flags
        );
        
        logger.debug("toObject - end: {}", nodeStatusInfo);
        return nodeStatusInfo;
    }
}
