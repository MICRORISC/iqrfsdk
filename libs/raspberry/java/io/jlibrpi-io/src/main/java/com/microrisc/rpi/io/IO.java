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
     * Available pins, numbering according to revision 2.
     */
    static enum Pin {
        GPIO2   (2),
        GPIO3   (3),
        GPIO4   (4),
        GPIO5   (5),
        GPIO6   (6),
        BUTTON  (7),
        CE0     (8),
        MISO    (9),
        MOSI    (10),
        SCLK    (11),
        GPIO12  (12),
        GPIO13  (13),
        TXD     (14),
        RXD     (15),
        GPIO16  (16),
        GPIO17  (17),
        GPIO18  (18),
        GPIO19  (19),
        GPIO20  (20),
        GPIO21  (21),
        LED     (22),
        RESET   (23),
        IO1     (24),
        IO2     (25),
        GPIO26  (26),
        GPIO27  (27);
        
        private final int pinNumber;
        
        private Pin(int pinNumber) {
            this.pinNumber = pinNumber;
        }
        
        /**
         * Returns integer value of this pin.
         * @return integer value of this pin.
         */
        public int getIntValue() {
            return pinNumber;
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
     * Setup direction of IO pin.
     * @param pin number of pin to setup
     * @param direction input or output
     */
    void set(IO.Pin pin, IO.Direction direction) throws IOException;
    
    /**
     * Writes specified data to specified pin.
     * @param pin number of pin, which the data to write to
     * @param value value to write to the pin
     */
    void write(IO.Pin pin, IO.Level value) throws IOException;
    
    /**
     * Reads data from specified pin.
     * @param pin number of pin to read data from
     * @return data read from pin
     */
    int read(IO.Pin port) throws IOException;
    
    /**
     * Resets TR module in RPI HW reduction.
     */
    void resetTr() throws IOException;
    
    /**
     * Terminates usage of this library and frees up used resources.
     */
    void destroy();
}
