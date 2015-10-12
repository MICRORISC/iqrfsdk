package com.microrisc.simply.iqrf.dpa.v22x.types;

import com.microrisc.simply.Node;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DPA_RequestConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implementation of {@link AbstractFRC_Command} for the use with
 * CustomDPAHandler-UserPeripheral-18B20-Idle. Implementation collects one byte
 * from each node with actual temperature.
 *
 * @author Martin Strouhal
 */
public final class FRC_Temperature_18B20Idle extends AbstractFRC_Command {

    private final int id = 0xC0;
    public static final int FRC_DATA_LENGTH = 64;

    /**
     * Creates new object of {@code FRC_Temperature_18B20Idle} with default user
     * data. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.   
     */
    public FRC_Temperature_18B20Idle() {
        super();
    }

    /**
     * Creates new object of {@code FRC_Temperature_18B20Idle} with specified
     * user data.
     *
     * @param userData user data of FRC command
     * @throws IllegalArgumentException if userData are incorrect
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[]) (more)}
     */
    public FRC_Temperature_18B20Idle(short[] userData) {
        super(userData);
    }

    /**
     * Creates new object of {@code FRC_Temperature_18B20Idle} with specified
     * dpa request.
     *
     * @param dpaRequest DPA request to take as a user data
     * @throws IllegalArgumentException if an error has occured during
     * conversion of specified DPA request into the series of bytes of user data
     */
    public FRC_Temperature_18B20Idle(DPA_Request dpaRequest) {
        super();
        try {
            this.userData = DPA_RequestConvertor.getInstance().toProtoValue(
                    checkDpaRequest(dpaRequest));
        } catch (ValueConversionException e) {
            throw new IllegalArgumentException(
                    "Conversion of DPA request failed: " + e);
        }
    }

    /**
     * Creates new object of {@code FRC_Temperature_18B20Idle} with specified user
     * data.
     *
     * @param userData user data
     * @param selectedNodes node on which will be command processed
     * @throws IllegalArgumentException if {@code userData} or
     * {@code selectedNodes} is invalid. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[], Node[]) AbstractFRC_Command}
     * constructor.
     */
    public FRC_Temperature_18B20Idle(short[] userData, Node[] selectedNodes) {
        super(userData, selectedNodes);
    }

    /**
     * Creates new object of {@code FRC_Temperature_18B20Idle} with default user
     * data. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     *
     * @param selectedNodes node on which will be command processed
     */
    public FRC_Temperature_18B20Idle(Node[] selectedNodes) {
        super(selectedNodes);
    }

    private DPA_Request checkDpaRequest(DPA_Request dpaRequest) {
        if (dpaRequest == null) {
            throw new IllegalArgumentException("DPA request cannot be null");
        }
        return dpaRequest;
    }

    public static interface Result extends FRC_CollectedBytes {
        
        /**
         * Returns temperature. If temperature is unkonown, returns 
         * {@code Integer.MAX_VALUE}
         * @return temperature in int
         */
        public int getTemperature();
        
        /**
         * Returns temperature in format: [temperature]°C.<br>
         * If measere was failed, it's returned "Unknown temperature"
         * @return formatted temperature
         */
        public String getFormattedTemperature();
        
    }

    public static class ResultImpl implements Result {

        private final short byteValue;

        public ResultImpl(short byteValue) {
            this.byteValue = byteValue;
        }

        @Override
        public short getByte() {
            return byteValue;
        }
        
        @Override
        public int getTemperature(){
            return (byteValue == 127) ? Integer.MAX_VALUE : (int)byteValue;
        }

        @Override
        public String getFormattedTemperature() {
            return (byteValue == 127) ? "Unknown temperature" : (byteValue + "°C");
        }
    }

    /**
     * Parses specified FRC data comming from IQRF.
     *
     * @param frcData FRC data to parse
     * @return map of results for each node. Identifiers of nodes are used as a
     * keys of the returned map.
     * @throws IllegalArgumentException if specified FRC data are not in correct
     * format
     * @throws Exception if parsing failed
     */
    public static Map<String, FRC_Temperature_18B20Idle.Result> parse(
            short[] frcData) throws Exception {
        checkFrcData(frcData);

        Map<String, FRC_Temperature_18B20Idle.ResultImpl> resultImplMap = null;
        try {
            resultImplMap = FRC_ResultParser.parseAsCollectedBytes(frcData,
                    FRC_Temperature_18B20Idle.ResultImpl.class);
        } catch (Exception ex) {
            throw new Exception("Parsing failed: " + ex);
        }

        Map<String, FRC_Temperature_18B20Idle.Result> resultMap = new HashMap<>();
        for (Map.Entry<String, FRC_Temperature_18B20Idle.ResultImpl> resImplEntry : resultImplMap.entrySet()) {
            resultMap.put(resImplEntry.getKey(), resImplEntry.getValue());
        }
        return resultMap;
    }

    /**
     * Parses specified FRC data comming from IQRF into easiest map in this case
     * - Map<String, Short>.
     *
     * @param frcData FRC data to parse
     * @return map of results for each node. Identifiers of nodes are used as a
     * keys of the returned map.
     * @throws IllegalArgumentException if specified FRC data are not in correct
     * format
     * @throws Exception if parsing failed
     */
    public static Map<String, Short[]> parseIntoShort(
            short[] frcData) throws Exception {
        checkFrcData(frcData);

        Map<String, FRC_Temperature_18B20Idle.Result> resultImplMap = parse(frcData);
        Map<String, Short[]> resultEasyMap = new HashMap<>();

        for (Entry<String, FRC_Temperature_18B20Idle.Result> e : resultImplMap.entrySet()) {
            String key = e.getKey();
            FRC_Temperature_18B20Idle.Result value = e.getValue();

            Short shortValues[] = new Short[]{value.getByte()};
            resultEasyMap.put(key, shortValues);
        }

        return resultEasyMap;
    }

    private static short[] checkFrcData(short[] frcData) {
        if (frcData == null) {
            throw new IllegalArgumentException(
                    "FRC data to parse cannot be null");
        }

        if (frcData.length != FRC_DATA_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid length of FRC data. Expected: " + FRC_DATA_LENGTH
                    + ", got: " + frcData.length);
        }
        return frcData;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public short[] getUserData() {
        return userData;
    }
}
