
package com.microrisc.simply.iqrf.dpa.v201.types;

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
        /** Minor number. */
        private short minorNumber = 0;
        
        /** Major number. */
        private short majorNumber = 0;
        
        /**
         * Creates new Protocol Version object.
         * @param minorVersion minor number
         * @param majorVersion major number
         */
        public DPA_ProtocolVersion(short minorVersion, short majorVersion) {
            this.minorNumber = minorVersion;
            this.majorNumber = majorVersion;
        }

        /**
         * @return the minor number
         */
        public short getMinorNumber() {
            return minorNumber;
        }

        /**
         * @return the major number
         */
        public short getMajorNumber() {
            return majorNumber;
        }
        
        @Override
        public String toString() {
            return ("{ " +
                    "minor number=" + minorNumber + 
                    ", major number=" + majorNumber +
                    " }");
        }
    }
    
    /** DPA protocol version. */
    private final DPA_ProtocolVersion dpaProtocolVersion;
    
    /** Number of user defined peripherals. */
    private final short userDefPeripheralsNum;
    
    /** Numbers of default peripherals present on the device. */
    private final int[] defaultPeripherals;
    
    /** HW Profile Type. 0x0000 if not present. */
    private final int hwProfileType;
    
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
     * @param hwProfleType HW profile type
     * @param hwProfileVersion HW profile version
     * @param flags flags
     */
    public PeripheralEnumeration(DPA_ProtocolVersion dpaProtocolVersion, 
            short userDefPeripheralsNum, int[] defaultPeripherals, int hwProfleType, 
            int hwProfileVersion, int flags 
    ) {
        this.dpaProtocolVersion = dpaProtocolVersion;
        this.userDefPeripheralsNum = userDefPeripheralsNum;
        this.defaultPeripherals = defaultPeripherals;
        this.hwProfileType = hwProfleType;
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
     * @return HW Profile Type
     */
    public int getRequestHwProfileType() {
        return hwProfileType;
    }

    /**
     * @return HW Profile Version
     */
    public int getRequestHwProfileVersion() {
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
        strBuilder.append(" HW profile type: " + hwProfileType + NEW_LINE);
        strBuilder.append(" HW profile version: " + hwProfileVersion + NEW_LINE);
        strBuilder.append(" Flags: " + flags + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
