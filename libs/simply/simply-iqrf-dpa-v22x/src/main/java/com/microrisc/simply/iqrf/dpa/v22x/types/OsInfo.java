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

import java.util.Arrays;

/**
 * Encapsulates information about used OS of IQMesh network.
 * 
 * @author Michal Konopa, Rostislav Spinar
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
    
    /** TR Type. */
    public static enum TR_Type {
        DCTR_52D      (0),
        DCTR_58D_RJ   (1),
        DCTR_72D      (2),
        DCTR_53D      (3),
        DCTR_54D      (8),
        DCTR_55D      (9),
        DCTR_56D      (10),
        DCTR_76D      (11);
        
        private final int value;
        
        private TR_Type(int typeValue) {
            this.value = typeValue;
        }
        
        public int getValue() {
            return value;
        }

        public TR_TypeSeries getSeries(){
            switch(this){
                case DCTR_52D:
                case DCTR_53D:
                case DCTR_54D:
                case DCTR_55D:
                case DCTR_56D:
                case DCTR_58D_RJ:
                    return TR_TypeSeries.TR52x;
                case DCTR_72D:
                case DCTR_76D:
                    return TR_TypeSeries.TR72x;
                default:
                    return TR_TypeSeries.UNKNOWN;
            }
        }
        
        /**
         * TR_TypeSeries
         */
        public static enum TR_TypeSeries{
            UNKNOWN(0), TR52x(1), TR72x(2), ;
            
            private final int value;
            
            private TR_TypeSeries(int typeValue){
                this.value = typeValue;
            }
            
            public int getValue(){
                return value;
            }
        }
    }
    
    /** Module ID */
    private final short[] moduleId;
    
    /** OS version */
    private final int osVersion;
    
    /** MCU type */
    private final MCU_Type mcuType;
    
    /** TR type */
    private final TR_Type trType;
    
    /** OS build */
    private final short[] osBuild;
    
    /** Rssi */
    private final int rssi;
    
    /** Supply voltage */
    private final int supplyVoltage;
    
    /** Flags. */
    private final int flags;
    
    /** Reserved byte for future purpose */
    private final int reserved;
    
    
    /**
     * Creates new {@code OsInfo} object.
     * @param moduleId module ID
     * @param osVersion OS version
     * @param mcuType MCU type
     * @param trType  TR type
     * @param osBuild OS build
     * @param rssi Rssi
     * @param supplyVoltage supply voltage
     * @param flags flags
     * @param reserver reserved byte
     */
    public OsInfo(short[] moduleId, int osVersion, MCU_Type mcuType, 
            TR_Type trType, short[] osBuild, int rssi, int supplyVoltage, 
            int flags, int reserved
    ) {
        this.moduleId = moduleId;
        this.osVersion = osVersion;
        this.mcuType = mcuType;
        this.trType = trType;
        this.osBuild = osBuild;
        this.rssi = rssi;
        this.supplyVoltage = supplyVoltage;
        this.flags = flags;
        this.reserved = reserved;
    }

    /**
     * Returns module ID. See moduleInfo() at IQRF OS Reference Guide.
     * @return module ID
     */
    public short[] getModuleId() {
        return moduleId;
    }

    /**
     * Returns OS version. See moduleInfo() at IQRF OS Reference Guide.
     * @return OS version
     */
    public int getOsVersion() {
        return osVersion;
    }

    /**
     * Returns MCU type. See moduleInfo() at IQRF OS Reference Guide.
     * @return MCU type
     */
    public MCU_Type getMcuType() {
        return mcuType;
    }
    
    /**
     * Returns TR type. See moduleInfo() at IQRF OS Reference Guide.
     * @return TR type
     */
    public TR_Type getTrType() {
        return trType;
    }

    /**
     * Returns OS build. See moduleInfo() at IQRF OS Reference Guide.
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
     * Returns supply voltage. See getSupplyVoltage() at IQRF Reference Guide.
     * @return supply voltage
     */
    public int getSupplyVoltage() {
        return supplyVoltage;
    }
    
    /**
     * Flags.0 is 1 if there is an insufficient OsBuild for the used DPA version
     * @return flags
     */
    public int getFlags() {
        return flags;
    }

   /**
     * Reserved byte by DPA for future purpose only.
     * @return undefined value, reservation for future purpose only
     */
   public int getReserved() {
      return reserved;
   }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Module ID: " + Arrays.toString(moduleId) + NEW_LINE);
        strBuilder.append(" OS version: " + osVersion + NEW_LINE);
        strBuilder.append(" TR type: " + trType + NEW_LINE);
        strBuilder.append(" MCU type: " + mcuType + NEW_LINE);
        strBuilder.append(" OS build: " + Arrays.toString(osBuild) + NEW_LINE);
        strBuilder.append(" RSSI: " + rssi + NEW_LINE);
        strBuilder.append(" Supply voltage: " + supplyVoltage + NEW_LINE);
        strBuilder.append(" Flags: " + flags + NEW_LINE);
        strBuilder.append(" Reserved: " + reserved + NEW_LINE);
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
    
    public String getPrettyFormatedTrType() {
        return trType.toString();
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
        strBuilder.append("TR type: " + getPrettyFormatedTrType() + NEW_LINE);
        strBuilder.append("MCU type: " + getPrettyFormatedMCUType() + NEW_LINE);
        strBuilder.append("RSSI: " + rssi + NEW_LINE);
        strBuilder.append("Supply voltage: " + supplyVoltage + NEW_LINE);
        strBuilder.append("Flags: " + flags + NEW_LINE);
        strBuilder.append("Reserved: " + reserved + NEW_LINE);
        
        return strBuilder.toString();
    }
}
