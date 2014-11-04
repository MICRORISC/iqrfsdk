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

package com.microrisc.cdc.examples;

import com.microrisc.cdc.*;

/**
 * Example demonstrates getting information about connected USB device and 
 * inner TR module.
 */
public class Info {
    static public void main(String[] args) {
        J_CDCImpl myCDC = null;
        
        try {
            // creating CDC object, which will communicate via /dev/ttyACM0
            //myCDC = new J_CDCImpl("/dev/ttyACM0");
            myCDC = new J_CDCImpl("COM4");
            
            // communication testing
            if (myCDC.test()) {
                System.out.println("Test OK");
            } else {
                System.out.println("Test FAILED");
                myCDC.destroy();
                return;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            if (myCDC != null) {
                myCDC.destroy();
            }
            return;
        }
        
        try {
            // getting SPI status of module
            J_SPIStatus jStatus = myCDC.getSPIStatus();
            if (jStatus.isDataReady()) {
                System.out.println("SPI data ready: " + jStatus.getDataReadyValue());
            } else {
                System.out.println("SPI mode: " + jStatus.getSpiMode());
            }
            System.out.println();
            
            // getting information about connected USB device
            J_DeviceInfo jDevInfo = myCDC.getUSBDeviceInfo();
            System.out.println("Device Info: ");
            System.out.println("type, length: " + jDevInfo.getType() + 
                    ", " + jDevInfo.getType().length());
            System.out.println("firmware version, length: " + jDevInfo.getFirmwareVersion() + 
                    ", " + jDevInfo.getFirmwareVersion().length());
            System.out.println("serial number, length: " + jDevInfo.getSerialNumber() + 
                    ", " + jDevInfo.getSerialNumber().length());
            
            System.out.println();
            
            // getting information about module inside the connected USB device
            J_ModuleInfo jModInfo = myCDC.getTRModuleInfo();
            System.out.println("Module Info: ");
            
            System.out.print("ID: ");
            short[] serialNumber = jModInfo.getSerialNumber();
            for (int i = 0; i <= serialNumber.length-1; i++) {
                System.out.format("%02X", serialNumber[i]);
            }
            System.out.println();
            
            int lowerNibble = jModInfo.getOsVersion() & 0xF;
            int upperNibble = jModInfo.getOsVersion() & (0xF << 4);
            upperNibble >>= 4; 
            
            System.out.print("IQRF OS: ");
            System.out.format("ver %1$x.%2$02x", upperNibble, lowerNibble);
            
            System.out.print("(");
            short[] osBuild = jModInfo.getOsBuild();
            for (int i = osBuild.length-1; i >= 0; i--) {
                System.out.format("%02X", osBuild[i]);
            }
            System.out.println(")");
            
            System.out.print("MCU: ");
            if (jModInfo.getPICType() == 3) {
                System.out.println("PIC16F886");
            } else if (jModInfo.getPICType() == 4) {
                System.out.println("PIC16LF1938");
            } else {
                System.out.println("unknown");
            }
        } catch(J_CDCSendException ex) {
            System.out.println("Send error occurred: " + ex.toString());
            // send exception processing...
        } catch(J_CDCReceiveException ex) {
            System.out.println("Receive error occured: " + ex.toString());
            // receive exception processing...
        } catch(Exception ex) {
            System.out.println("Other Error occured: " + ex.toString());
            // other exception processing...
        }
        
        // terminate library and free up used resources
        myCDC.destroy();
    }
}
