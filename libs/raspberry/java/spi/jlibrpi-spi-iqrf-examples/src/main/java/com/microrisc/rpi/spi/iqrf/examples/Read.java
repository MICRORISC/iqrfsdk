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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Examples of simple usage of SPI. 
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class Read {
    
    // try to wait for date ready state in specified timeout
    private static SPI_Status tryToWaitForDataReadyState(SPI_Master spiMaster, long timeout) 
        throws SPI_Exception, InterruptedException 
    {
        SPI_Status spiStatus = null;
        long endTime = System.currentTimeMillis() + timeout;
        do {
            // getting slave status
            spiStatus = spiMaster.getSlaveStatus();
            System.out.println("SPI status: " + Integer.toHexString(spiStatus.getValue()));

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
            // getting reference to SPI master
            spiMaster = new SimpleSPI_Master("/dev/spidev0.0");
        } catch (SPI_Exception e) {
            System.err.println("Creation of SPI Master failed: " + e);
            System.exit(1);
        }
            
        for ( int cycle = 0; cycle < cycles; cycle++ ) {
            System.out.println("Waiting for data: ");
            SPI_Status spiStatus = null;
            try {
                spiStatus = tryToWaitForDataReadyState(spiMaster, 5000);

                // if SPI not ready in 5000 ms, end 
                if ( !spiStatus.isDataReady() ) {
                    System.out.println("Try to wait for data ready state failed.");
                    spiMaster.destroy();
                    System.exit(2);
                }

                short[] dataRead = spiMaster.readData(getDataLen(spiStatus));
                printData(dataRead);

                // getting slave status
                spiStatus = spiMaster.getSlaveStatus();
                System.out.println("SPI status after reading: " + Integer.toHexString(spiStatus.getValue()));
            } catch (SPI_Exception e) {
                System.err.println("Error: " + e);
            }

            Thread.sleep(500);
        }
        
        // termination and resources free up
        spiMaster.destroy();
    }
}
