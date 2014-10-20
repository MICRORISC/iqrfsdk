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
 * Encapsulates USB device identification. Immutable.
 * <p>
 * Peer class for DeviceInfo structure.
 * 
 * @version     1.0
 */
public class J_DeviceInfo {
    /** Device type. */
    private String type;
    
    /** Firmware version. */
    private String firmwareVersion;
    
    /** Serial number. */
    private String serialNumber;
    
    
    /**
     * Creates new instance of J_DeviceInfo and sets its members according to
     * specified parameters.
     * @param type device type
     * @param firmwareVersion firmware version
     * @param serialNumber serial number
     */
    public J_DeviceInfo(String type, String firmwareVersion, String serialNumber) {
        this.type = type;
        this.firmwareVersion = firmwareVersion;
        this.serialNumber = serialNumber;
    }
    
    /**
     * Returns device type.
     * @return device type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the firmware version.
     * @return firmware version
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * Returns the serial number.
     * @return the serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }
}
