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
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.MemoryRequestConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.HashMap;
import java.util.Map;

/**
 * FRC_MemoryRead provides collecting bytes. Resulting byte is read from the
 * specified memory address after provided DPA Request is executed. This allows
 * getting one byte from any memory location (RAM, EEPROM and EEEPROM
 * peripherals, Flash, MCU register, etc.). <br>
 * As the returned byte cannot equal to 0 there is also Memory read plus 1 FRC
 * command available. <br>
 * Batch request is not allowed to be a DPA request being executed.
 *
 * @author Martin Strouhal
 */
public final class FRC_MemoryRead extends AbstractFRC_Command {

    private static final int id = 0x82;
   
    /**
     * Provides acces to parsed FRC data comming from IQRF.
     */
    public static interface Result extends FRC_CollectedBytes {
        
        public short getMemoryValue();
        
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
        public short getMemoryValue(){
            return byteValue;
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

    private static MemoryRequest checkMemoryRequest(MemoryRequest memoryRequest) {
        if (memoryRequest == null) {
            throw new IllegalArgumentException("Memory request cannot be null");
        }
        return memoryRequest;
    }

    /**
     * Creates new object of {@code FRC_MemoryRead} with specified user data.
     *
     * @param memoryRequest memory request to take as a user data
     * @throws IllegalArgumentException if an error has occured during
     * conversion of specified memory request into the series of bytes of user data
     */
    public FRC_MemoryRead(MemoryRequest memoryRequest) {
        try {
            this.userData = MemoryRequestConvertor.getInstance().toProtoValue(checkMemoryRequest(memoryRequest));
        } catch (ValueConversionException e) {
            throw new IllegalArgumentException("Conversion of memory request failed: " + e);
        }
    }

    /**
     * Creates new object of {@code FRC_MemoryRead}. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     *
     * @param memoryRequest memory request to take as a user data
     * @param selectedNodes node on which will be command processed
     * @throws IllegalArgumentException if an error has occured during
     * conversion of specified memory request into the series of bytes of user
     * data
     */
    public FRC_MemoryRead(MemoryRequest memoryRequest, Node[] selectedNodes) {
        super(selectedNodes);
        try {
            this.userData = MemoryRequestConvertor.getInstance().toProtoValue(checkMemoryRequest(memoryRequest));
        } catch (ValueConversionException e) {
            throw new IllegalArgumentException("Conversion of memory request failed: " + e);
        }                
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
