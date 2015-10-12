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
 * FRC_VRN command. 
 * 
 * @author Michal Konopa
 */
public final class FRC_VRN extends AbstractFRC_Command  {
    private static final int id = 0xC0;
    
    
    /** Provides acces to parsed FRC data comming from IQRF. */
    public static interface Result extends FRC_CollectedBytes {
        /** 
         * @return VRN value
         */ 
        short getVRN();
    }
    
    /** Parsed FRC data comming from IQRF. */
    private static class ResultImpl implements Result {
        private final short byteValue;
        
        public ResultImpl(short byteValue) {
            this.byteValue = byteValue;
        }

        @Override
        public short getVRN() {
            return byteValue;
        }

        @Override
        public short getByte() {
            return byteValue;
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
     * Creates new object of {@code FRC_VRN} with specified user data.
     * @param userData user data
     * @throws IllegalArgumentException if {@code userData} is invalid. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[]) AbstractFRC_Command}
     * constructor.
     */
    public FRC_VRN(short[] userData) {
        super(userData);
    }
    
    /**
     * Creates new object of {@code FRC_VRN} with default user data.
     * See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     */
    public FRC_VRN() {
    }

        /**
     * Creates new object of {@code FRC_VRN} with specified user data.
     *
     * @param userData user data
     * @param selectedNodes node on which will be command processed
     * @throws IllegalArgumentException if {@code userData} or
     * {@code selectedNodes} is invalid. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[], Node[]) AbstractFRC_Command}
     * constructor.
     */
    public FRC_VRN(short[] userData, Node[] selectedNodes) {
        super(userData, selectedNodes);
    }

    /**
     * Creates new object of {@code FRC_VRN} with default user data. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     *
     * @param selectedNodes node on which will be command processed
     */
    public FRC_VRN(Node[] selectedNodes) {
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
