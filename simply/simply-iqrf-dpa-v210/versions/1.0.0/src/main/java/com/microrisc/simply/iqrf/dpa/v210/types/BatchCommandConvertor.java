

package com.microrisc.simply.iqrf.dpa.v210.types;

import com.microrisc.simply.types.AbstractConvertor;
import com.microrisc.simply.types.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting batch commands to protocol packets.
 * 
 * @author Michal Konopa
 */
public final class BatchCommandConvertor extends AbstractConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BatchCommandConvertor.class);
    
    private BatchCommandConvertor() {
    }
    
    /** Singleton. */
    private static final BatchCommandConvertor instance = new BatchCommandConvertor();
    
     /**
     * @return BatchCommandConvertor instance
     */
    static public BatchCommandConvertor getInstance() {
        return instance;
    }
    
    /**
     * Serializes specified serialized requests into one final sequence of bytes. 
     * @param serRequests source request to put into final sequence
     * @return 
     */
    private short[] serializeToArray(List<short[]> serRequests) {
        final byte BATCH_LAST_BYTE = 0;

        // last byte of all the sequence will be 0
        int finalArrayLen = 1;
        
        for ( short[] serRequest : serRequests ) {
            finalArrayLen += serRequest.length;
        }
        
        short[] finalArray = new short[finalArrayLen];
        int actPos = 0;
        for ( short[] serRequest : serRequests ) {
            System.arraycopy(serRequest, 0, finalArray, actPos, serRequest.length);
            actPos += serRequest.length;
        }
      
        finalArray[finalArrayLen-1] = BATCH_LAST_BYTE;
        return finalArray;
    }
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if ( !(value instanceof DPA_Request[]) ) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        DPA_Request[] dpaRequests = (DPA_Request[]) value;
        
        List<short[]> serRequests = new LinkedList<>();
        for ( DPA_Request dpaRequest : dpaRequests ) {
            short[] serRequest = null;
            try {
                serRequest = DPA_RequestConvertor.getInstance().toProtoValue(dpaRequest);
            } catch (ValueConversionException ex) {
                throw new ValueConversionException("Value to convert has not proper type.", ex);
            }
            serRequests.add(serRequest);
        }
        
        short[] protoValue = serializeToArray(serRequests);
        
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
