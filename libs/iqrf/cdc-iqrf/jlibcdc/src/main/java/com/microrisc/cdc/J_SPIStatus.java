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

package com.microrisc.cdc;


/**
 * Encapsulates information about the current SPI state, in which is the TR module 
 * of connected USB device.
 * <p>
 * Peer class for SPIStatus structure.
 * 
 * @version     1.0
 */
public class J_SPIStatus {
    /** Determines, if current SPI status is: SPI data ready. */
    private boolean dataReady;
    
    /** Current SPI mode - except for SPI data ready mode. */
    private J_SPIModes spiMode;
    
    /** Exact value of SPI data ready mode, if it is active. */
    private int dataReadyValue;

    /**
     * If data ready is <code>true</code>, then parameter <code>spiModeValue</code> 
     * denotes value of SPI data ready (IQRF SPI User's guide, page 4). 
     * Otherwise <code>spiModeValue</code> is equal to one of the J_SPIModes 
     * constants.
     * @param dataReady indication of SPI data ready mode
     * @param spiModeValue SPI mode
     */
    public J_SPIStatus(boolean dataReady, int spiModeValue) {
        this.dataReady = dataReady;
        if (dataReady) {
            this.dataReadyValue = spiModeValue;
        } else {
            for(J_SPIModes mode : J_SPIModes.values()) {
                if (mode.getMode() == spiModeValue) {
                    spiMode = J_SPIModes.valueOf(mode.name());
                }
            }
        }
    }
    
    /**
     * Indicates, wheather this module is in SPI data ready mode.
     * @return <code>true</code>, if this module is in SPI data ready mode<br>
     *         <code>false</code> otherwise 
     */
    public boolean isDataReady() {
        return dataReady;
    }

    /**
     * Returns SPI mode of this TR module.
     * @return SPI mode value
     */
    public J_SPIModes getSpiMode() {
        return spiMode;
    }

    /**
     * Returns the precise value of SPI data ready mode.
     * @return precise SPI data ready mode value
     */
    public int getDataReadyValue() {
        return dataReadyValue;
    }
}
