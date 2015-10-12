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
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DPA_RequestConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.HashMap;
import java.util.Map;

/**
 * FRC_AcknowledgedBroadcastBits command. 
 * 
 * @author Michal Konopa
 */
public final class FRC_AcknowledgedBroadcastBits extends AbstractFRC_Command {
    private static final int id = 0x02;
    
    
    /** Provides acces to parsed FRC data comming from IQRF. */
    public static interface Result extends FRC_CollectedBits {
        public static enum DeviceProcResult {
            /** Node device did not respond to FRC at all. */
            NOT_RESPOND, 
            
            /** HWPID did not match HWPID of the device. */
            HWPID_NOT_MATCH,
            
            /** HWPID matches HWPID of the device. */
            HWP_MATCH
        }
        
        /**
         * @return result of processing of FRC command on node device.
         */
        DeviceProcResult getDeviceProcResult();
    }
    
    /** Parsed FRC data comming from IQRF. */
    private static class ResultImpl implements Result {
        private final byte bit0;
        private final byte bit1;
        private final Result.DeviceProcResult devProcResult;
        
        public ResultImpl(byte bit0, byte bit1) {
            this.bit0 = bit0;
            this.bit1 = bit1;
            if ( (bit0 == 0) && (bit1 == 0) ) {
                devProcResult = Result.DeviceProcResult.NOT_RESPOND;
            } else if ( (bit0 == 0) && (bit1 == 1) ) {
                devProcResult = Result.DeviceProcResult.HWPID_NOT_MATCH;
            } else {
                devProcResult = Result.DeviceProcResult.HWP_MATCH;
            }
        }
        
        @Override
        public byte getBit0() {
            return bit0;
        }

        @Override
        public byte getBit1() {
            return bit1;
        }
        
        @Override
        public DeviceProcResult getDeviceProcResult() {
            return devProcResult;
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
    
    private static DPA_Request checkDpaRequest(DPA_Request dpaRequest) {
        if ( dpaRequest == null ) {
            throw new IllegalArgumentException("DPA request cannot be null");
        }
        return dpaRequest;
    }
    
    
    /**
     * Creates new object of {@code FRC_AcknowledgedBroadcastBits} with specified user data.
     * @param dpaRequest DPA request to take as a user data
     * @throws IllegalArgumentException if an error has occured during conversion 
     *         of specified DPA request into the series of bytes of user data
     */
    public FRC_AcknowledgedBroadcastBits(DPA_Request dpaRequest) {
        super();
        try {
            this.userData = DPA_RequestConvertor.getInstance().toProtoValue(checkDpaRequest(dpaRequest));
        } catch ( ValueConversionException e ) {
            throw new IllegalArgumentException("Conversion of DPA request failed: " + e);
        }
    }
    
    /**
     * Creates new object of {@code FRC_AcknowledgedBroadcastBits} with specified user data.
     * @param userData user data
     * @throws IllegalArgumentException if {@code userData} is invalid. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[]) AbstractFRC_Command}
     * constructor.
     */
    public FRC_AcknowledgedBroadcastBits(short[] userData) {
        super(userData);
    }
    
    /**
     * Creates new object of {@code FRC_AcknowledgedBroadcastBits} with default user data.
     * See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     */
    public FRC_AcknowledgedBroadcastBits() {
    }

        /**
     * Creates new object of {@code FRC_AcknowledgedBroadcastBits} with specified user data.
     *
     * @param userData user data
     * @param selectedNodes node on which will be command processed
     * @throws IllegalArgumentException if {@code userData} or
     * {@code selectedNodes} is invalid. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[], Node[]) AbstractFRC_Command}
     * constructor.
     */
    public FRC_AcknowledgedBroadcastBits(short[] userData, Node[] selectedNodes) {
        super(userData, selectedNodes);
    }

    /**
     * Creates new object of {@code FRC_AcknowledgedBroadcastBits} with default user data. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     *
     * @param selectedNodes node on which will be command processed
     */
    public FRC_AcknowledgedBroadcastBits(Node[] selectedNodes) {
        super(selectedNodes);        
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
            resultImplMap = FRC_ResultParser.parseAsCollectedBits(frcData, ResultImpl.class);
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
