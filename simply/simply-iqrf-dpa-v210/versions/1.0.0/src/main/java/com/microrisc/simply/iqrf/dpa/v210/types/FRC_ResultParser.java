

package com.microrisc.simply.iqrf.dpa.v210.types;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements base parsing of FRC result data. 
 * 
 * @author Michal Konopa
 */
public final class FRC_ResultParser {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FRC_ResultParser.class);
    
    /** Length of incomming data. */
    private static final int DATA_LENGTH = 64;
    
    private static final int FIRST_BIT_START_INDEX = 0;
    private static final int FIRST_BIT_END_INDEX = 29;
    
    private static final int SECOND_BIT_START_INDEX = 32;
    private static final int SECOND_BIT_END_INDEX = 61;
    
    private static final int BYTE_START_INDEX = 1;
    private static final int BYTE_END_INDEX = 62;
    
    
    private static short[] checkFrcData(short[] frcData) {
        if ( frcData == null ) {
            throw new IllegalArgumentException("FRC data to parse cannot be null");
        }
        
        if ( frcData.length != DATA_LENGTH ) {
            throw new IllegalArgumentException(
                    "Invalid length of FRC data. Expected: " + DATA_LENGTH 
                            + ", got: " + frcData.length 
            );
        }
        return frcData;
    }
    
    /**
     * Parses specified FRC result data as collected bits and returns parsed result.
     * Result is a map of parsed objects of type {@code T} for each node. Identifiers 
     * of nodes are used as a keys of the returned map.
     * <p>
     * Important note: <br>
     * Supplied T type must have a public constructor with 2 parameters of 
     * {@code byte} type, else an exception is thrown. 
     * @param <T> type of parsed result information of each node
     * @param frcData FRC data to parse
     * @param type Class object of object to return as a parsed result for each node 
     * @return map of parsed data for each node
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static <T> Map<String, T> parseAsCollectedBits(short[] frcData, Class<T> type) 
        throws
            NoSuchMethodException, 
            InstantiationException, 
            IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException 
    {
        logger.debug("parseAsCollectedBits - start: frcData={}, type={}", 
                Arrays.toString(frcData), type
        );
        
        checkFrcData(frcData);
        java.lang.reflect.Constructor constructor = type.getConstructor(byte.class, byte.class);
        
        Map<String, T> resultMap = new HashMap<>();
        
        int nodeId = 0;
        for ( int byteId = FIRST_BIT_START_INDEX;  byteId <= FIRST_BIT_END_INDEX; byteId++ ) {
            int bitComp = 1;
            for ( int bitId = 0; bitId < 8; bitId++ ) {
                byte bit0 = (byte)(( (frcData[byteId] & bitComp) == bitComp )? 1 : 0) ;
                byte bit1 = (byte)(( (frcData[byteId + SECOND_BIT_START_INDEX] & bitComp) == bitComp )? 1 : 0 );
                resultMap.put(String.valueOf(nodeId), (T)constructor.newInstance(bit0, bit1));
                nodeId++;
                bitComp *= 2;
            }
        }
        
        logger.debug("parseAsCollectedBits - end: {}", resultMap.toString());
        return resultMap;
    }
    
    /**
     * Parses specified FRC result data as collected bytes and returns parsed result.
     * Result is a map of parsed objects of type {@code T} for each node. Identifiers 
     * of nodes are used as a keys of the returned map.
     * <p>
     * Important note: <br>
     * Supplied T type must have a public constructor with 1 parameter of 
     * {@code short} type, else an exception is thrown. 
     * @param <T> type of parsed result information of each node
     * @param frcData FRC data to parse
     * @param type Class object of object to return as a parsed result for each node 
     * @return map of parsed data for each node
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static <T> Map<String, T> parseAsCollectedBytes(short[] frcData, Class<T> type) 
         throws 
            NoSuchMethodException, 
            InstantiationException, 
            IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException 
    {
        logger.debug("parseAsCollectedBytes - start: frcData={}, type={}", 
            Arrays.toString(frcData), type
        );
        
        checkFrcData(frcData);
        java.lang.reflect.Constructor constructor = type.getConstructor(short.class);
        
        Map<String, T> resultMap = new HashMap<>();
        
        int nodeId = 1;
        for ( int byteId = BYTE_START_INDEX; byteId <= BYTE_END_INDEX; byteId++ ) {
            resultMap.put(String.valueOf(nodeId), (T)constructor.newInstance(frcData[byteId]));
            nodeId++;
        }
        
        logger.debug("parseAsCollectedBytes - end: {}", resultMap.toString());
        return resultMap;
    }
}
