package com.microrisc.rpi.io;

/**
 * Access to IO functionality on Raspberry platform.
 * 
 * @author Rostislav Spinar
 */
public interface IO {
    
    /** Level of signal. */
    static enum Level {
        LOW     (0),
        HIGH    (1);
        
        private final int level;
        
        private Level(int level) {
            this.level = level;
        }
        
        /**
         * Returns integer value of this level.
         * @return integer value of this level 
         */
        public int getIntValue() {
            return level;
        }
    }
          
    /**
     * Available ports, numbering according to revision 2.
     */
    static enum Port {
        GPIO2   (2),
        GPIO3   (3),
        GPIO4   (4),
        GPIO5   (5),
        GPIO6   (6),
        CE1     (7),
        CE0     (8),
        MISO    (9),
        MOSI    (10),
        SCLK    (11),
        GPIO12  (12),
        GPIO13  (13),
        GPIO14  (14),
        GPIO15  (15),
        GPIO16  (16),
        GPIO17  (17),
        GPIO18  (18),
        GPIO19  (19),
        GPIO20  (20),
        GPIO21  (21),
        LED     (22),
        RESET   (23),
        GPIO24  (24),
        GPIO25  (25),
        GPIO26  (26),
        GPIO27  (27);
        
        private final int portNumber;
        
        private Port(int portNumber) {
            this.portNumber = portNumber;
        }
        
        /**
         * Returns integer value of this port.
         * @return integer value of this port.
         */
        public int getIntValue() {
            return portNumber;
        }
    }
    
    /**
     * Data direction.
     */
    static enum Direction {
        INPUT   (0),
        OUTPUT  (1);
        
        private final int directionValue;
        
        private Direction(int directionValue) {
            this.directionValue = directionValue;
        }
        
        /**
         * Returns integer value of this direction.
         * @return integer value of this direction.
         */
        public int getIntValue() {
            return directionValue;
        }
    }
    
    /**
     * Setup direction of IO port.
     * @param port number of port to setup
     * @param direction input or output
     */
    void set(IO.Port port, IO.Direction direction) throws IOException;
    
    /**
     * Writes specified data to specified port.
     * @param port number of port, which the data to write to
     * @param value value to write to the port
     */
    void write(IO.Port port, IO.Level value) throws IOException;
    
    /**
     * Reads data from specified port.
     * @param port number of port to read data from
     * @return data read from port
     */
    int read(IO.Port port) throws IOException;
    
    /**
     * Resets TR module in RPI HW reduction.
     */
    void resetTR() throws IOException;
    
    /**
     * Terminates usage of this library and frees up used resources.
     */
    void destroy();
}
