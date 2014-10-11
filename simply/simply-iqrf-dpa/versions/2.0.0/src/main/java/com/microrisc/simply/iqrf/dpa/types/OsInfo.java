
package com.microrisc.simply.iqrf.dpa.types;

import java.util.Arrays;

/**
 * Encapsulates information about used OS of IQMesh network.
 * 
 * @author Michal Konopa
 */
public final class OsInfo {
    
    /** MCU Type. */
    public static enum MCU_Type {
        PIC16F886       (3),
        PIC16LF1938     (4);
        
        private final int value;
        
        private MCU_Type(int typeValue) {
            this.value = typeValue;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    
    /** Module ID */
    private final short[] moduleId;
    
    /** OS version */
    private final int osVersion;
    
    /** MCU type */
    private final MCU_Type mcuType;
    
    /** OS build */
    private final short[] osBuild;
    
    /** Rssi */
    private final int rssi;
    
    /** Supply voltage */
    private final int supplyVoltage;
    
    /** Flags. */
    private final int flags;
    
    
    /**
     * Creates new {@code OsInfo} object.
     * @param moduleId module ID
     * @param osVersion OS version
     * @param mcuType MCU type
     * @param osBuild OS build
     * @param rssi Rssi
     * @param supplyVoltage supply voltage
     * @param flags flags
     */
    public OsInfo(short[] moduleId, int osVersion, MCU_Type mcuType, 
            short[] osBuild, int rssi, int supplyVoltage, int flags
    ) {
        this.moduleId = moduleId;
        this.osVersion = osVersion;
        this.mcuType = mcuType;
        this.osBuild = osBuild;
        this.rssi = rssi;
        this.supplyVoltage = supplyVoltage;
        this.flags = flags;
    }

    /**
     * @return module ID
     */
    public short[] getModuleId() {
        return moduleId;
    }

    /**
     * @return OS version
     */
    public int getOsVersion() {
        return osVersion;
    }

    /**
     * @return MCU type
     */
    public MCU_Type getMcuType() {
        return mcuType;
    }

    /**
     * @return OS build
     */
    public short[] getOsBuild() {
        return osBuild;
    }

    /**
     * @return rssi
     */
    public int getRssi() {
        return rssi;
    }
    
    /**
     * @return supply voltage
     */
    public int getSupplyVoltage() {
        return supplyVoltage;
    }
    
    /**
     * @return flags
     */
    public int getFlags() {
        return flags;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Module ID: " + Arrays.toString(moduleId) + NEW_LINE);
        strBuilder.append(" OS version: " + osVersion + NEW_LINE);
        strBuilder.append(" MCU type: " + mcuType + NEW_LINE);
        strBuilder.append(" OS build: " + Arrays.toString(osBuild) + NEW_LINE);
        strBuilder.append(" RSSI: " + rssi + NEW_LINE);
        strBuilder.append(" Supply voltage: " + supplyVoltage + NEW_LINE);
        strBuilder.append(" Flags: " + flags + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String getPrettyFormatedModuleId() {
        StringBuilder strBuilder = new StringBuilder();
        for (int dataId = (moduleId.length-1);  dataId >= 0; dataId--) {
            strBuilder.append( String.format("%02X", moduleId[dataId]) );
            
        }
        return strBuilder.toString();
    }
    
    public String getPrettyFormatedOsVersion() {
        StringBuilder strBuilder = new StringBuilder();
        int upperNible = (osVersion & ((0xF) << 4)) >> 4;
        strBuilder.append( String.format("%x", upperNible) );
        strBuilder.append('.');
        int lowerNibble = osVersion & 0xF;
        strBuilder.append( String.format("%02x", lowerNibble) );
        
        if ( mcuType == MCU_Type.PIC16LF1938 ) {
            strBuilder.append('D');
        }
        
        strBuilder.append(" (");
        strBuilder.append(getPrettyFormatedOsBuild());
        strBuilder.append(')');
        return strBuilder.toString();
    }
    
    public String getPrettyFormatedOsBuild() {
        StringBuilder strBuilder = new StringBuilder();
        for (int dataId = (osBuild.length-1); dataId >= 0; dataId--) {
            strBuilder.append( String.format("%02x", osBuild[dataId]) );
        }
        return strBuilder.toString();
    } 
    
    public String getPrettyFormatedMCUType() {
        return mcuType.toString();
    }
    
    /**
     * Returns pretty formated string information. 
     * @return pretty formated string information.
     */
    public String toPrettyFormatedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("Module ID: " + getPrettyFormatedModuleId() + NEW_LINE);
        strBuilder.append("OS version: " + getPrettyFormatedOsVersion() + NEW_LINE);
        strBuilder.append("MCU type: " + getPrettyFormatedMCUType() + NEW_LINE);
        strBuilder.append("RSSI: " + rssi + NEW_LINE);
        strBuilder.append("Supply voltage: " + supplyVoltage + NEW_LINE);
        strBuilder.append("Flags: " + flags + NEW_LINE);
        
        return strBuilder.toString();
    }
}
