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

import com.microrisc.simply.iqrf.dpa.v22x.types.OsInfo;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between {@code OsInfo} objects and
 * protocol packets.
 * 
 * @author Michal Konopa
 */
public final class OsInfoConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OsInfoConvertor.class);
    
    private OsInfoConvertor() {}
    
    /** Singleton. */
    private static final OsInfoConvertor instance = new OsInfoConvertor();
    
    
    /**
     * @return {@code OsInfoConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public OsInfoConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 11;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    // postitions of fields
    static private final int MODULE_ID_POS = 0;
    static private final int MODULE_ID_LENGTH = 4;
    static private final int OS_VERSION_POS = 4;
    static private final int MCUTR_TYPE_POS = 5;
    static private final int OS_BUILD_POS = 6;
    static private final int OS_BUILD_LENGTH = 2;
    static private final int RSSI_POS = 8;
    static private final int SUPPLY_VOLTAGE_POS = 9;
    static private final int FLAGS_POS = 10;
    static private final int RESERVED_POS = 11;
    
    private OsInfo.MCU_Type getMCU_Type(short packetValue) 
            throws ValueConversionException 
    {
        for (OsInfo.MCU_Type mcuType : OsInfo.MCU_Type.values()) {
            if ((packetValue & 0x07) == mcuType.getValue()) {
                return mcuType;
            }
        }
        throw new ValueConversionException("Uknown value of MCU type: " + packetValue);
    }
    
    private OsInfo.TR_Type getTR_Type(short packetValue)
            throws ValueConversionException 
    {
        for (OsInfo.TR_Type trType : OsInfo.TR_Type.values()) {
            if (((packetValue & 0xF0) >> 4) == trType.getValue()) {
                return trType;
            }
        }
        throw new ValueConversionException("Uknown value of TR type: " + ((packetValue & 0xF0) >> 4));
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
        
        short[] moduleId = new short[MODULE_ID_LENGTH];
        System.arraycopy(protoValue, MODULE_ID_POS, moduleId, 0, MODULE_ID_LENGTH);
        
        short osVersion = protoValue[OS_VERSION_POS];
        OsInfo.TR_Type trType = getTR_Type(protoValue[MCUTR_TYPE_POS]);
        OsInfo.MCU_Type mcuType = getMCU_Type(protoValue[MCUTR_TYPE_POS]);
        
        short[] osBuild = new short[OS_BUILD_LENGTH];
        System.arraycopy(protoValue, OS_BUILD_POS, osBuild, 0, OS_BUILD_LENGTH);
        
        short rssi = protoValue[RSSI_POS];
        short supplyVoltage = protoValue[SUPPLY_VOLTAGE_POS];
        
        int flags = protoValue[FLAGS_POS];
        
        int reserved = protoValue[RESERVED_POS];
        
        OsInfo osInfo = new OsInfo(moduleId, osVersion, mcuType, trType, osBuild, rssi, 
                supplyVoltage, flags, reserved
        );
        
        logger.debug("toObject - end: {}", osInfo);
        return osInfo;
    }
}
