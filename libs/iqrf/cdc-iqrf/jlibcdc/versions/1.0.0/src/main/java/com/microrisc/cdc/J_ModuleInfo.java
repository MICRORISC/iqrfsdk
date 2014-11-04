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
