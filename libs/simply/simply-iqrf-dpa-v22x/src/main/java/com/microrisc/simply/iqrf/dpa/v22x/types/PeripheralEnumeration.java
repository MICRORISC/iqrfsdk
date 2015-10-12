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

/**
 * Information about peripheral enumeration on some network device.
 * 
 * @author Michal Konopa
 */
public final class PeripheralEnumeration {
    /**
     * Implements DPA Protocol Version.
     */
    public static class DPA_ProtocolVersion {
        /** Minor version. */
        private short minorVersion = 0;
        
        /** Major version. */
        private short majorVersion = 0;
        
        /**
         * Creates new Protocol Version object.
         * @param minorVersion minor version
         * @param majorVersion major version
         */
        public DPA_ProtocolVersion(short minorVersion, short majorVersion) {
            this.minorVersion = minorVersion;
            this.majorVersion = majorVersion;
        }

        /**
         * @return the minor version
         */
        public short getMinorVersion() {
            return minorVersion;
        }

        /**
         * @return the major version
         */
        public short getMajorVersion() {
            return majorVersion;
        }
        
        @Override
        public String toString() {
            return ("{ " +
                    "minor version=" + minorVersion + 
                    ", major version=" + majorVersion +
                    " }");
        }
    }
    
    /** DPA protocol version. */
    private final DPA_ProtocolVersion dpaProtocolVersion;
    
    /** Number of user defined peripherals. */
    private final short userDefPeripheralsNum;
    
    /** Numbers of default peripherals present on the device. */
    private final int[] defaultPeripherals;
    
    /** HW Profile ID. 0x0000 if not present. */
    private final int hwProfleID;
    
    /** HW Profile version. */
    private final int hwProfileVersion;
    
    /** Flags. */
    private final int flags;
    
    
    private String getDefaultPeripheralsString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[");
        for (int perNumber : defaultPeripherals) {
            sb.append(perNumber);
            sb.append(", ");
        }
        sb.append("]");
        
        return sb.toString();
    }
    
    /**
     * Creates new object of {@code PeripheralEnumeration}.
     * @param dpaProtocolVersion DPA protocol version
     * @param userDefPeripheralsNum number of user defined peripherals
     * @param defaultPeripherals numbers of enabled default peripherals
     * @param hwProfleID HW profile ID
     * @param hwProfileVersion HW profile version
     * @param flags flags
     */
    public PeripheralEnumeration(DPA_ProtocolVersion dpaProtocolVersion, 
            short userDefPeripheralsNum, int[] defaultPeripherals, int hwProfleID, 
            int hwProfileVersion, int flags 
    ) {
        this.dpaProtocolVersion = dpaProtocolVersion;
        this.userDefPeripheralsNum = userDefPeripheralsNum;
        this.defaultPeripherals = defaultPeripherals;
        this.hwProfleID = hwProfleID;
        this.hwProfileVersion = hwProfileVersion;
        this.flags = flags;
    }
    
    /**
     * @return the protocol version
     */
    public DPA_ProtocolVersion getDPA_ProtocolVersion() {
        return dpaProtocolVersion;
    }
    
    /**
     * @return number of user defined peripherals
     */
    public short getUserDefPeripheralsNum() {
        return userDefPeripheralsNum;
    }

    /**
     * @return numbers of default peripherals present on the device
     */
    public int[] getDefaultPeripherals() {
        int[] perArrayCopy = new int[defaultPeripherals.length];
        System.arraycopy(defaultPeripherals, 0, perArrayCopy, 0, defaultPeripherals.length);
        return perArrayCopy;
    }

    /**
     * @return HW profile ID
     */
    public int getHwProfileID() {
        return hwProfleID;
    }

    /**
     * @return HW profile version
     */
    public int getHwProfileVersion() {
        return hwProfileVersion;
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
        strBuilder.append(" DPA protocol version: " + dpaProtocolVersion + NEW_LINE);
        strBuilder.append(" Number of user defined peripherals: " + userDefPeripheralsNum + NEW_LINE);
        strBuilder.append(" Default peripherals: " + getDefaultPeripheralsString() + NEW_LINE);
        strBuilder.append(" HW profile ID: " + hwProfleID + NEW_LINE);
        strBuilder.append(" HW profile version: " + hwProfileVersion + NEW_LINE);
        strBuilder.append(" Flags: " + flags + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
