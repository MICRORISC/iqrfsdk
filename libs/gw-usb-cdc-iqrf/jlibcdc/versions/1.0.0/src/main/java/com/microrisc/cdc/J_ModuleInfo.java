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
 * Encapsulates TR module identification inside the USB device. Immutable.
 * <p>
 * Peer class for ModuleInfo structure.
 * 
 * @version     1.0
 */
public class J_ModuleInfo {
    
    /** Serial number. */
    private short[] serialNumber;
    
    /** OS version. */
    private short osVersion;
    
    /** PIC type. */
    private short PICType;
    
    /** OS build. */
    private short[] osBuild;
    
    
    /** Length of serial number array. */
    public static final int SER_NUMBER_LEN = 4;
    
    /** Length of OS build array. */
    public static final int OS_BUILD_LEN = 2;
    
    
    /**
     * Creates new object of J_ModuleInfo, initialized according to specified
     * parameters.
     * @param serialNumber serial number
     * @param osVersion OS version
     * @param PICType   PIC type
     * @param osBuild   OS build 
     */
    public J_ModuleInfo(short[] serialNumber, 
            short osVersion, short PICType, short[] osBuild) {
        this.serialNumber = new short[SER_NUMBER_LEN];
        System.arraycopy(serialNumber, 0, this.serialNumber, 0, SER_NUMBER_LEN);
        
        this.osVersion = osVersion;
        this.PICType = PICType;
        
        this.osBuild = new short[OS_BUILD_LEN];
        System.arraycopy(osBuild, 0, this.osBuild, 0, OS_BUILD_LEN);
    }
    
    /**
     * Returns the serial number of this TR module.
     * @return the serial number
     */
    public short[] getSerialNumber() {
        return serialNumber.clone();
    }

    /**
     * Returns version of operating system on this TR module.
     * @return version of OS
     */
    public short getOsVersion() {
        return osVersion;
    }

    /**
     * Returns the PIC type.
     * @return the PIC type
     */
    public short getPICType() {
        return PICType;
    }

    /**
     * Returns the number of OS build.
     * @return the number of OS build.
     */
    public short[] getOsBuild() {
        return osBuild.clone();
    }
}
