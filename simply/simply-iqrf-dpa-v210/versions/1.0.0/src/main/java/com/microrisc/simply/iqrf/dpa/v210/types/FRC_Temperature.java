
package com.microrisc.simply.iqrf.dpa.v210.types;

import java.util.HashMap;
import java.util.Map;

/**
 * FRC_Temperature command. 
 * 
 * @author Michal Konopa
 */
public final class FRC_Temperature extends AbstractFRC_Command {
    private static final int id = 0x80;
    
    /** Provides acces to parsed FRC data comming from IQRF. */
    public static interface Result extends FRC_CollectedBytes {
        /** 
         * Returns temperature on device.
         * Resulting byte equals to the temperature value read by 
         * getTemperature() IQRF OS method. If resulting temperature is 0 
         * degree Celsius, that would normally equal to value 0, then a fixed 
         * value 0x7F is returned instead. This value substitution makes it 
         * possible to distinguish between devices reporting 0 degree Celsius and 
         * devices not reporting at all. Device would normally never return a 
         * temperature corresponding to the value 0x7F, because +127 degree Celsius 
         * is out of working temperature range.
         * @return temperature
         */ 
        byte getTemperature();
    }
    
    /** Parsed FRC data comming from IQRF. */
    private static class ResultImpl implements Result {
        private final short byteValue;
        
        public ResultImpl(short byteValue) {
            this.byteValue = byteValue;
        }
        
        @Override
        public short getByte() {
            return byteValue;
        }
        
        @Override
        public byte getTemperature() {
            return (byte)byteValue;
        }
    }
    
    
    private static short[] checkFrcData(short[] frcData) {
        if ( frcData == null ) {
            throw new IllegalArgumentException("FRC data to parse cannot be null");
        }
        
        if ( frcData.length != 64 ) {
            throw new IllegalArgumentException(
                    "Invalid length of FRC data. Expected: 64, got: " + frcData.length 
            );
        }
        return frcData;
    }
    
    
    /**
     * Creates new object of {@code FRC_Temperature} with specified user data.
     * @param userData user data
     * @throws IllegalArgumentException if {@code userData} is invalid. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[]) AbstractFRC_Command}
     * constructor.
     */
    public FRC_Temperature(short[] userData) {
        super(userData);
    }
    
    /**
     * Creates new object of {@code FRC_Temperature} with default user data.
     * See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     */
    public FRC_Temperature() {
    }
    
    @Override
    public int getId() {
        return id;
    }

    @Override
    public short[] getUserData() {
        return userData;
    }
    
    /**
     * Parses specified FRC data comming from IQRF.
     * @param frcData FRC data to parse
     * @return map of results for each node. Identifiers of nodes are used as a
     *         keys of the returned map.
     * @throws IllegalArgumentException if specified FRC data are not in correct format
     * @throws Exception if parsing failed
     */
    public static Map<String, Result> parse(short[] frcData) throws Exception {
        checkFrcData(frcData);
        Map<String, ResultImpl> resultImplMap = null;
        try {
            resultImplMap = FRC_ResultParser.parseAsCollectedBytes(frcData, ResultImpl.class);
        } catch ( Exception ex ) {
            throw new Exception("Parsing failed: " + ex);
        }
        Map<String, Result> resultMap = new HashMap<>();
        for ( Map.Entry<String, ResultImpl> resImplEntry : resultImplMap.entrySet() ) {
            resultMap.put(resImplEntry.getKey(), resImplEntry.getValue());
        }
        return resultMap;
    }
}
