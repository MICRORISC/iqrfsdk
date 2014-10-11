/*
 * Public CDC Library
 * Copyright (C) 2012 MICRORISC s.r.o., www.microrisc.com
 * IQRF platform details: www.iqrf.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
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
