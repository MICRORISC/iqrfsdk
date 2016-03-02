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

package com.microrisc.simply.iqrf.dpa.v22x.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralEnumeration;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@code peripheral_enumeration} 
 * type values to {@code PeripheralEnumeration} objects. 
 * 
 * @author Michal Konopa
 */
public final class PeripheralEnumerationConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = 
            LoggerFactory.getLogger(PeripheralEnumerationConvertor.class);
    
    private PeripheralEnumerationConvertor() {}
    
    /** Singleton. */
    private static final PeripheralEnumerationConvertor instance = new PeripheralEnumerationConvertor();
    
    
    /**
     * @return {@code PeripheralEnumerationConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public PeripheralEnumerationConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 12;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int DPA_PROTO_MINOR_VERSION_POS = 0;
    static private final int DPA_PROTO_MAJOR_VERSION_POS = 
            DPA_PROTO_MINOR_VERSION_POS + 1;
    
    static private final int USER_PERS_NUM_POS = 2;
    
    static private final int DEF_PERS_POS = 3;
    static private final int DEF_PERS_LENGTH = 4;
    
    static private final int HWPROFID_POS = 7;
    static private final int HWPROFID_LENGTH = 2;
    
    static private final int HWPROFVERSION_POS = 9;
    static private final int HWPROFVERSION_LENGTH = 2;
    
    static private final int FLAGS_POS = 11;
    
    
    // returns array of numbers of supported peripheral
    private int[] getDefaultPeripherals(short[] defPers) {
        List<Integer> perNumbersList = new LinkedList<>();
        
        int byteRank = 0;
        for (short defPersByte : defPers) {
            int bitComp = 1;
            for (int i = 0; i < 8; i++) {
                if ((defPersByte & bitComp) == bitComp) {
                    perNumbersList.add(byteRank*8 + i);
                }
                bitComp *= 2; 
            }
            byteRank++;
        }
        
        // toArray method works with reference types
        int[] perNumberArr = new int[perNumbersList.size()];
        int arrIndex = 0;
        for (int perNumber : perNumbersList) {
            perNumberArr[arrIndex++] = perNumber;
        }
        
        return perNumberArr;
    }
    
    // returns HW Profile ID
    private int getHwProfID(short[] protoValue) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int byteId = 0; byteId < HWPROFID_LENGTH; byteId++) {
            byteBuffer.put((byte)protoValue[HWPROFID_POS + byteId]);
        }
        return byteBuffer.getInt(0);
    }
    
    
    // returns HW Profile Version
    private int getHwProfVersion(short[] protoValue) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int byteId = 0; byteId < HWPROFVERSION_LENGTH; byteId++) {
            byteBuffer.put((byte)protoValue[HWPROFVERSION_POS + byteId]);
        }
        return byteBuffer.getInt(0);
    }
    
    // returns BCD code for specified value
    private short getBCD_Code(short value) {
        return Short.parseShort(Integer.toHexString(value));
    }
    
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        short userDefPeripheralsNum = protoValue[USER_PERS_NUM_POS];
        
        short[] defPers = new short[DEF_PERS_LENGTH];
        System.arraycopy(protoValue, DEF_PERS_POS, defPers, 0, DEF_PERS_LENGTH);
        int[] defaultPeripherals = getDefaultPeripherals(defPers);
        int hwProfType = getHwProfID(protoValue);
        int hwProfVersion = getHwProfVersion(protoValue);
        int flags = protoValue[FLAGS_POS];
        
        // dpa protocol version info
        // prevent one bit which describes demo HWP
        short protoMinorNumber = getBCD_Code((short)(protoValue[DPA_PROTO_MINOR_VERSION_POS] & 0x7F));  
        short protoMajorNumber = getBCD_Code(protoValue[DPA_PROTO_MAJOR_VERSION_POS]);
        
        PeripheralEnumeration.DPA_ProtocolVersion dpaProtoVersion = 
                new PeripheralEnumeration.DPA_ProtocolVersion(protoMinorNumber, protoMajorNumber);
        
        PeripheralEnumeration perEnum = new PeripheralEnumeration(
                dpaProtoVersion, userDefPeripheralsNum, defaultPeripherals, 
                hwProfType, hwProfVersion, flags
        );
        
        logger.debug("toObject - end: {}", perEnum);
        return perEnum;
    }
}
