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
package com.microrisc.simply.iqrf.dpa.v22x.types;

import com.microrisc.simply.Node;
import java.util.HashMap;
import java.util.Map;

/**
 * FRC_ResponseTime command.<br>
 * FRC_ResponseTime is used to find out FRC response time of the specified user
 * FRC command. View more in DPA documentation.
 *
 * @author Martin Strouhal
 */
public final class FRC_ResponseTime extends AbstractFRC_Command {

    private static final int id = 0x84;

    /**
     * Provides acces to parsed FRC data comming from IQRF.
     */
    public static interface Result extends FRC_CollectedBytes {

        /**
         * Returns response time from device as integer value. The returned time
         * value equals to the value of the corresponding
         * _FRC_RESPONSE_TIME_??_MS constant.
         *
         * @return response time
         */
        byte getResponseTimeInInt();

        /**
         * Returns response time as {@link FRC_Configuration.FRC_RESPONSE_TIME}.
         *
         * @return {@link FRC_Configuration.FRC_RESPONSE_TIME}
         */
        FRC_Configuration.FRC_RESPONSE_TIME getResponseTime();

        /**
         * Returns response time encapsulated in {@link FRC_Configuration}
         *
         * @return {@link FRC_Configuration
         */
        FRC_Configuration getResponseTimeAsConfiguration();
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
        public byte getResponseTimeInInt() {
            return (byte) byteValue;
        }

        @Override
        public FRC_Configuration.FRC_RESPONSE_TIME getResponseTime() {
            return FRC_Configuration.FRC_RESPONSE_TIME.getResponseTimeFor(getResponseTimeInInt());
        }

        @Override
        public FRC_Configuration getResponseTimeAsConfiguration() {
            return new FRC_Configuration(getResponseTime());
        }
    }

    private static short[] checkFrcData(short[] frcData) {
        if (frcData == null) {
            throw new IllegalArgumentException("FRC data to parse cannot be null");
        }

        if (frcData.length != 64) {
            throw new IllegalArgumentException(
                    "Invalid length of FRC data. Expected: 64, got: " + frcData.length
            );
        }
        return frcData;
    }

    /**
     * Creates new object of {@code FRC_ResponseTime} with specified ID of user
     * FRC command for which will be found response time.
     *
     * @param frcId of FRC user command for which will be found response time
     * @throws IllegalArgumentException if {@code frcId} is invalid.
     */
    public FRC_ResponseTime(int frcId) {
        super(new short[]{(short) frcId, (short) 0});
        checkFrcId(frcId);
    }

    /**
     * Creates new object of {@code FRC_ResponseTime} with selected nodes and
     * specified ID of user FRC command for which will be found response time.
     *
     * @param frcId of FRC user command for which will be found response time
     * @param selectedNodes node on which will be command processed
     * @throws IllegalArgumentException if {@code frcId} is invalid or
     * {@code selectedNodes} is invalid. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[], Node[]) AbstractFRC_Command}
     * constructor.
     */
    public FRC_ResponseTime(int frcId, Node[] selectedNodes) {
        short[] frcIdUserData = new short[]{checkFrcId(frcId), (short) 0};
        new FRC_ResponseTime(frcIdUserData, selectedNodes);
    }

    //create instance from super abstract class
    private FRC_ResponseTime(short[] frcIdUserData, Node[] selectedNodes) {
        super(frcIdUserData, selectedNodes);
    }

    private short checkFrcId(int frcId) {
        if (frcId < 0 || frcId > 0xFF) {
            throw new IllegalArgumentException("FRC ID must be in interval 0 - 255");
        }
        return (short) frcId;
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
     *
     * @param frcData FRC data to parse
     * @return map of results for each node. Identifiers of nodes are used as a
     * keys of the returned map.
     * @throws IllegalArgumentException if specified FRC data are not in correct
     * format
     * @throws Exception if parsing failed
     */
    public static Map<String, Result> parse(short[] frcData) throws Exception {
        checkFrcData(frcData);
        Map<String, ResultImpl> resultImplMap = null;
        try {
            resultImplMap = FRC_ResultParser.parseAsCollectedBytes(frcData, ResultImpl.class);
        } catch (Exception ex) {
            throw new Exception("Parsing failed: " + ex);
        }
        Map<String, Result> resultMap = new HashMap<>();
        for (Map.Entry<String, ResultImpl> resImplEntry : resultImplMap.entrySet()) {
            resultMap.put(resImplEntry.getKey(), resImplEntry.getValue());
        }
        return resultMap;
    }
}
