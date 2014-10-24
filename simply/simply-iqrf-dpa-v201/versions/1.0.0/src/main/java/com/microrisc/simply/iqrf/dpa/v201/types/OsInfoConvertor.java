
package com.microrisc.simply.iqrf.dpa.v201.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
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
    static private final int MCU_TYPE_POS = 5;
    static private final int OS_BUILD_POS = 6;
    static private final int OS_BUILD_LENGTH = 2;
    static private final int RSSI_POS = 8;
    static private final int SUPPLY_VOLTAGE_POS = 9;
    static private final int FLAGS_POS = 10;
    
    
    
    private OsInfo.MCU_Type getMCU_Type(short packetValue) 
            throws ValueConversionException 
    {
        for (OsInfo.MCU_Type mcuType : OsInfo.MCU_Type.values()) {
            if (packetValue == mcuType.getValue()) {
                return mcuType;
            }
        }
        throw new ValueConversionException("Uknown value of MCU type: " + packetValue);
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
        OsInfo.MCU_Type mcuType = getMCU_Type(protoValue[MCU_TYPE_POS]);
        
        short[] osBuild = new short[OS_BUILD_LENGTH];
        System.arraycopy(protoValue, OS_BUILD_POS, osBuild, 0, OS_BUILD_LENGTH);
        
        short rssi = protoValue[RSSI_POS];
        short supplyVoltage = protoValue[SUPPLY_VOLTAGE_POS];
        
        int flags = protoValue[FLAGS_POS];
        
        OsInfo osInfo = new OsInfo(moduleId, osVersion, mcuType, osBuild, rssi, 
                supplyVoltage, flags
        );
        
        logger.debug("toObject - end: {}", osInfo);
        return osInfo;
    }
}
