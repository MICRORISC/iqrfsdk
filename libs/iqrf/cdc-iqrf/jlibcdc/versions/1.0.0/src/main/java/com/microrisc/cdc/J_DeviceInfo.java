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
