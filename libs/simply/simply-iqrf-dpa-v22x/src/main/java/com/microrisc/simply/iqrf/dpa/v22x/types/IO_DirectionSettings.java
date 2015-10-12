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
 * Encapsulates settings of IO information.
 * 
 * @author Michal Konopa
 */
public final class IO_DirectionSettings {
    /** Port to setup a direction to. 0=TRISA, 1=TRISB,  */
    private final int port;
    
    /** Masks pins of the port. */
    private final int mask;
    
    /** Actual direction bits for the masked pins. 0=output, 1=input. */
    private final int value;
    
    
    /**
     * Creates new objects encapsulating IO settings.
     * @param port a) Specifies port to setup a direction to. 0x00=TRISA, 0x01=TRISB,
     *                ...(predefined symbols PNUM_IO_TRISx) or  <br>
     *             b) Specifies port to setup a pull-up. 0x11=WPUB, 0x14=WPUE 
     *                ...(predefined symbols PNUM_IO_WPUx)
     * @param mask Masks pins of the port.
     * @param value a) Actual direction bits for the masked pins. 0=output, 1=input, ... or <br>
     *              b) Pull-up state. 0=disabled, 1=enabled.
     */
    public IO_DirectionSettings(int port, int mask, int value) {
        this.port = port;
        this.mask = mask;
        this.value = value;
    }

    /**
     * @return port to setup a direction to. 0=TRISA, 1=TRISB
     */
    public int getPort() {
        return port;
    }

    /**
     * @return masks of pins of the port
     */
    public int getMask() {
        return mask;
    }

    /**
     * @return actual direction bits for the masked pins. 0=output, 1=input.
     */
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Port: " + port + NEW_LINE);
        strBuilder.append(" Mask: " + mask + NEW_LINE);
        strBuilder.append(" Value: " + value + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
