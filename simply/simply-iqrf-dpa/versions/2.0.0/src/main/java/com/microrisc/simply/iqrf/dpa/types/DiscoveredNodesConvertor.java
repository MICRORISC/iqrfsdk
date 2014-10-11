
package com.microrisc.simply.iqrf.dpa.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion from IQRF DPA bit array of discovered nodes 
 * to {@code Integer[]} values  
 * 
 * @author Michal Konopa
 */
public final class DiscoveredNodesConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DiscoveredNodesConvertor.class);
    
    private DiscoveredNodesConvertor() {}
    
    /** Singleton. */
    private static final DiscoveredNodesConvertor instance = new DiscoveredNodesConvertor();
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 32;
    
    
    /**
     * @return DiscoveredNodesConvertor instance 
     */
    static public DiscoveredNodesConvertor getInstance() {
        return instance;
    }
    
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        // maximal bonded node number
        final int MAX_BONDED_NODE_NUMBER = 0xEF; 
        final int MAX_BYTES_USED = MAX_BONDED_NODE_NUMBER / 8;
        
        List<Integer> discoveredNodesList = new LinkedList<Integer>();
        
        for (int byteId = 0; byteId < 32; byteId++) {
            if (byteId >= MAX_BYTES_USED) {
                break;
            }
            
            if (protoValue[byteId] == 0) {
                continue;
            }
            
            int bitComp = 1;
            for (int bitId = 0; bitId < 8; bitId++) {
                if ((protoValue[byteId] & bitComp) == bitComp) {
                    discoveredNodesList.add(byteId * 8 + bitId);
                }
                bitComp *= 2;
            }
        }
        DiscoveredNodes discoveredNodes = new DiscoveredNodes(discoveredNodesList);
        
        logger.debug("toObject - end: {}", discoveredNodes);
        return discoveredNodes;
    }
}
