
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Type of the collected information is specified by a byte called FRC command. 
 * Currently IQRF OS enabled to collect either 2 bits from all (up to 239) nodes 
 * or 1 byte from up to 63 nodes having logical addresses 1-63.
 * 
 * @author Rostislav Spinar
 * @author Michal Konopa
 */
public final class FRC_Command {
    
    /**
     * Type of information collection.
     */
    // bit 7
    public enum CollectionType {
        /**
         * Collecting 2 bits from the routing node in the network (up to 239 nodes). 
         */
        TWOBITS  (0x00),
                
        /**
         * Collecting 1 Byte from the routing nodes in the network (up to 63 nodes).
         */
        ONEBYTE  (0x80);
        
        private final int value;
        
        
        private CollectionType(int value) {
            this.value = value;
        }
        
        /**
         * @return integer value.
         */
        public int getValue() {
            return value;
        } 
    }
    
    /**
     * Type of information to collect.
     */
    // bits 0-6
    /*
    public enum InfoType {
        //byte command, result byte=TEMPERATURE
        TEMPERATURE                     (152),
        TEMPERATURE_SLEEP               (153);
        
        private final int value;
        
        private InfoType(int value) {
            this.value = value;
        }
        
        public int getCommandValue() {
            return value;
        }
    }
    */
    
    /**
     * Predefined commands, which have fixed collect type.
     */
    public enum PredefinedCommand {
        /**
         * Collects bits. Bit 0 is 1 when node is accessible, bit1 is 1 if the 
         * node provided pre-bonding to a new node.
         */
        PREBONDING      (0x00),
        
        /**
         * Collects bits. Bit 0 is 1 when node is accessible, bit1 is 1 when 
         * there is some data available for reading from UART or SPI peripheral.
         */
        UART_SPI_DATA   (0x01),
        
        /**
         * Bit command, result bit.0 == 1, result bit.1 == 1 when button is 
         * pressed, otherwise bit.1 == 0
         */
        BUTTON_PRESSED          (0x08),
        BUTTON_PRESSED_SLEEP    (0x09),
        
        /**
         * Bit command, result bit.0 == 1, result bit.1 == 1 when LEDG is on, 
         * otherwise bit.1 == 0
         */
        LEDG_ON                 (0x18),
        LEDG_ON_SLEEP           (0x19),
        
        /**
         * byte command, result byte=VRN
         */
        VRN                     (0x88),
        VRN_SLEEP               (0x89),
        
        /**
         * Collects bytes. Result byte equals to the temperature value read by getTemperature() 
         * IQRF OS method. If resulting temperature is 0 Celsius degree, that would normally 
         * equal to value 0, then a fixed value 0x7F is returned instead. This makes
         * possible to distinguish between devices reporting 0 Celsius degree and devices not 
         * reporting at all. Device would normally never return a temperature 
         * corresponding to the value 0x7F, because +127 Celsius degree is out of working 
         * temperature range.
         */
        TEMPERATURE                     (0x80),
        
        /**
         * Shows how return 16bit value even having its bytes equal to 0. This 
         * command does not do sleeping, but user data is used to specify which 
         * byte of 16bit value to return).
         */
        RETURN_VALUE                    (0x98);
        
        
        private final int cmdValue;
        
        private PredefinedCommand(int cmdValue) {
            this.cmdValue = cmdValue;
        }
        
        /**
         * @return int value of the command.
         */
        public int getCommandValue() {
            return cmdValue;
        }
    }
    
    // checks also for unsupported predefined commands
    private static PredefinedCommand checkPredefinedCommand(PredefinedCommand preCommand) {
        if ( preCommand == null ) {
            throw new IllegalArgumentException("Predefined command cannot be null");
        }
        return preCommand;
    }
    
    // limits of command value
    private static final int CMD_VALUE_BOTTOM_LIMIT = 0;
    private static final int CMD_VALUE_UPPER_LIMIT = 255;
    
    /** Final value to use for command byte. */
    private final int cmdValue;
    
    private static int checkCommandValue(int cmdValue) {
        if ( ( cmdValue < CMD_VALUE_BOTTOM_LIMIT ) || 
               ( cmdValue > CMD_VALUE_UPPER_LIMIT ) 
           ) {
            throw new IllegalArgumentException("Command value out of bounds");
        }
        return cmdValue;
    }
    
    
    /**
     * Creates new FRC Command object.
     * @param cmdValue command value to use
     * @throws IllegalArgumentException if specified command value is out of [0..255] bounds 
     */
    public FRC_Command(int cmdValue) {
        this.cmdValue = checkCommandValue(cmdValue);
    }
    
    /**
     * Creates new FRC Command object.
     * @param collectType type of collection
     * @param infoType type of information to collect
     * @throws IllegalArgumentException if specified information type value is out 
     *         of [0..255] bounds or if specified type of collection doesn't match 
     *         to specified type of information to collect
     */
    public FRC_Command( CollectionType collectType, int infoType ) {
        checkCommandValue(infoType);
        if ( collectType == CollectionType.TWOBITS ) {
            if ( (infoType & CollectionType.ONEBYTE.getValue()) == 
                    CollectionType.ONEBYTE.getValue() 
               ) {
                throw new IllegalArgumentException(
                        "Type of collection doesnt't match to type of information"
                );
            }
        }
        this.cmdValue = infoType | collectType.getValue();
    }
    
    /**
     * Creates new FRC Command object using predefined command
     * @param preCommand predefined command
     * @throws IllegalArgumentException if {@code preCommand} is {@code null}
     */
    public FRC_Command( PredefinedCommand preCommand ) {
        this.cmdValue = checkPredefinedCommand(preCommand).getCommandValue();
    }
    
    /**
     * Returns final command value.
     * @return final command value.
     */
    public int getCommandValue() {
        return cmdValue;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" FRC command: " + cmdValue + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormattedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(" FRC command: " + cmdValue + NEW_LINE);
        
        return strBuilder.toString();
    }
}
