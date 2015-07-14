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

package com.microrisc.rpi.spi.iqrf.examples;

import com.microrisc.rpi.spi.SPI_Exception;
import com.microrisc.rpi.spi.iqrf.SPI_Master;
import com.microrisc.rpi.spi.iqrf.SPI_Status;
import com.microrisc.rpi.spi.iqrf.SimpleSPI_Master;
import java.util.Arrays;

/**
 * Examples of simple usage of SPI. 
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class WriteRead {
    
    // response flag
    private static boolean responseReceived = false;
    
    // try to wait for communication ready state in specified timeout
    private static SPI_Status tryToWaitForReadyState(SPI_Master spiMaster, long timeout) 
        throws SPI_Exception, InterruptedException 
    {
        SPI_Status spiStatus = null;
        long endTime = System.currentTimeMillis() + timeout;
        do {
            // getting slave status
            spiStatus = spiMaster.getSlaveStatus();
            System.out.println("SPI status:" + Integer.toHexString(spiStatus.getValue()));

            // read status every 10ms
            Thread.sleep(10);

            if ( System.currentTimeMillis() >= endTime ) {
                break;
            }
        } while ( spiStatus.getValue() != SPI_Status.READY_COMM_MODE );
        return spiStatus;
    }
    
    // try to wait for date ready state in specified timeout
    private static SPI_Status tryToWaitForDataReadyState(SPI_Master spiMaster, long timeout) 
        throws SPI_Exception, InterruptedException 
    {
        SPI_Status spiStatus = null;
        long endTime = System.currentTimeMillis() + timeout;
        do {
            // getting slave status
            spiStatus = spiMaster.getSlaveStatus();
            System.out.println("SPI status:" + Integer.toHexString(spiStatus.getValue()));

            // read status every 10ms
            Thread.sleep(10);

            if ( System.currentTimeMillis() >= endTime ) {
                break;
            }
        } while ( !spiStatus.isDataReady() );
        return spiStatus;
    }
    
    // counts and returns data length
    private static int getDataLen(SPI_Status spiStatus) {
        if ( spiStatus.getValue() == 0x40 ) {
            return 64;
        }
        return spiStatus.getValue() - 0x40;
    }
    
    // prints specified data on standard output
    private static void printData(short[] data) {
        System.out.print("Data: ");
        for ( short s : data ) {
            System.out.print(String.format("%02X ", s));
        }
        System.out.println();
    }
    
    private static void printTemperature(short[] data) {
        // positions of fields
        final int INT_VALUE_POS = 8;
        final int FULL_VALUE_POS = 9;
        final int FULL_VALUE_LENGTH = 2;
        
        // for DPA request read temperature
        final short[] CONFIRMATION_HEADER = { 0x01, 0x00, 0x0A, 0x00 };
        final short[] RESPONSE_HEADER = { 0x01, 0x00, 0x0A, 0x80 };
        
        // no data
        if (data == null || data.length == 0) {
            System.out.println("No data received\n");
            return;
        }
        
        // data length
        System.out.println("data length: " + data.length);
        
        // header of data
        short[] dataHeader = Arrays.copyOfRange(data, 0, 4);
        
        if (Arrays.equals(dataHeader, CONFIRMATION_HEADER)) {
            System.out.print("confirmation received: ");
        } else if (Arrays.equals(dataHeader, RESPONSE_HEADER)) {
            System.out.print("response received: ");
            responseReceived = true;
        } else {
            System.out.print("unknown type of message received: ");
        }
        
        // display raw data
        for (short sh : data) {
            System.out.print(Integer.toHexString(sh) + " ");
        }
        System.out.println();
        
        // parse data to get temperature
        if (responseReceived) {
            short value = data[INT_VALUE_POS];
            
            short[] fullTemperatureValue = new short[FULL_VALUE_LENGTH];
            System.arraycopy(data, FULL_VALUE_POS, fullTemperatureValue, 0, FULL_VALUE_LENGTH);
            byte fractialPart = (byte)(fullTemperatureValue[0] & 0x0F);
        
            System.out.println("Temperature = " + value + "." + fractialPart + " C");
        }
        
        System.out.println();
    }
    
    
    public static void main(String[] args) throws InterruptedException {
        final int MAX_CYCLES = 10;
        int cycles = MAX_CYCLES;
        if ( args.length > 0 ) {
            try {
                cycles = Integer.parseInt(args[0]);
            } catch ( NumberFormatException e ) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        }
        
        SPI_Master spiMaster = null;
        try {
            // initialization
            spiMaster = new SimpleSPI_Master("/dev/spidev0.0");
        } catch (SPI_Exception ex) {
            System.err.println("Error while creating SPI master: " + ex.getMessage());
            System.exit(1);
        }
        
        // waiting until the device will be ready to communicate
        try {    
            SPI_Status spiStatus = null;
            spiStatus = tryToWaitForReadyState(spiMaster, 1000);

            // if SPI not ready in 1000 ms, end 
            if ( spiStatus.getValue() != SPI_Status.READY_COMM_MODE ) {
                System.out.println("Try to wait for ready state failed");
                
                // try if there are any data to read 
                spiStatus = tryToWaitForDataReadyState(spiMaster, 1000);
                    
                // if SPI not data ready in 1000 ms, not interested here 
                if ( !spiStatus.isDataReady() ) {
                    System.out.println("Try to wait for data ready state failed.");
                    return;
                }
            }
        } catch (SPI_Exception e) {
            System.err.println("Waiting for SPI ready failed: " + e);
            spiMaster.destroy();
            System.exit(2);
        }
        
        for ( int cycle = 0; cycle < cycles; cycle++ ) {
            System.out.println("Sending Temperature request.");
            
            try {
                //spiMaster.sendData( new short[] { 0x01, 0x00, 0x06, 0x03, 0xFF, 0xFF } );
                short[] temperatureRequest = { 0x01, 0x00, 0x0A, 0x00, 0xFF, 0xFF };
                spiMaster.sendData( temperatureRequest );
                
                SPI_Status spiStatus = spiMaster.getSlaveStatus();
                System.out.println("SPI status after sending:" + Integer.toHexString(spiStatus.getValue()));

                short[] dataRead = {};
                System.out.println("Reading data: ");
                
                while (responseReceived == false) {
                    spiStatus = tryToWaitForDataReadyState(spiMaster, 1000);

                    // if SPI not data ready in 1000 ms, end 
                    if (!spiStatus.isDataReady()) {
                        System.out.println("Try to wait for data ready state failed.");
                        break;
                    }

                    dataRead = spiMaster.readData(getDataLen(spiStatus));
                    
                    //printData(dataRead);
                    printTemperature(dataRead);
                }
                
                // ready for next cycle
                responseReceived = false;
                
                // getting slave status
                spiStatus = spiMaster.getSlaveStatus();
                System.out.println("SPI status after reading:" + Integer.toHexString(spiStatus.getValue()));
            } catch (SPI_Exception e) {
                System.err.println("Error: " + e);
            }
            
            Thread.sleep(500);
        } 
        
        // termination and resources free up
        spiMaster.destroy();
    }
}
