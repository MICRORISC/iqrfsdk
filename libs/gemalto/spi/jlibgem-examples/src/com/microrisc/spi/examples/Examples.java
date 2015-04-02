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

package com.microrisc.spi.examples;

import com.microrisc.spi.BaudRate;
import com.microrisc.spi.SPI_Master;
import com.microrisc.spi.SPI_MasterFactory;
import com.microrisc.spi.VoidType;
import com.microrisc.spi.iqrf_protocol.SPI_Status;
import java.io.IOException;
import javax.microedition.midlet.*;

/**
 * Simple example of using IQRF SPI driver.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class Examples extends MIDlet {
    // SPI Master - used for communication with connected SPI device
    private SPI_Master spiMaster = null;
    
    // prints specified message and destroys the application
    private void printAndDestroyApp(String msg) {
        System.out.println(msg);
        destroyApp(true);
        notifyDestroyed();
    }
    
    
    /**
     * Makes at most a specified number of attempts to wait for SPI device to be 
     * in the communication mode.
     * @param maxAttemptsNum maximal number of attempts to make
     * @param timeToWait time to wait between individual attempts, in[ms]
     * @return status of connected SPI device, can be {@code null}
     */
    private SPI_Status waitForCommMode(int maxAttemptsNum, long timeToWait) {
        SPI_Status spiStatus = null;
        for ( int checkId = 0; checkId < maxAttemptsNum; checkId++ ) {
            spiStatus = spiMaster.getSlaveStatus();
            if ( spiStatus != null ) {
                if ( spiStatus.getValue() == SPI_Status.READY_COMM_MODE ) {
                    break;
                }
            }
        
            try {
                Thread.sleep(timeToWait);
            } catch (InterruptedException ex) {
                System.err.println("Waiting for SPI status interrupted");
            }
        }
        return spiStatus;
    }
    
    /**
     * Makes at most a specified number of attempts to wait for SPI device to be 
     * in the data ready mode.
     * @param maxAttemptsNum maximal number of attempts to make
     * @param timeToWait time to wait between individual attempts, in[ms]
     * @return status of connected SPI device, can be {@code null}
     */
    private SPI_Status waitForDataReadyMode(int maxAttemptsNum, long timeToWait) {
        SPI_Status spiStatus = null;
        for ( int checkId = 0; checkId < maxAttemptsNum; checkId++ ) {
            spiStatus = spiMaster.getSlaveStatus();
            if ( spiStatus != null ) {
                if ( spiStatus.isDataReady() ) {
                    break;
                };
            }
            
            try {
                Thread.sleep(timeToWait);
            } catch (InterruptedException ex) {
                System.err.println("Waiting for SPI status interrupted");
            }
        }
        return spiStatus;
    }
    
    public void startApp() {
        // getting SPI master  
        try {
            spiMaster = SPI_MasterFactory.getMaster(BaudRate.BaudRate_250);
        } catch ( Exception ex ) {
            printAndDestroyApp("Creating SPI Master failed: " + ex);
            return;
        }
        
        // waiting for SPI device to be in communication mode before read/write data to/from it
        SPI_Status spiStatus = waitForCommMode(10, 10);
        if ( spiStatus == null ) {
            printAndDestroyApp("SPI mode not available.");
            return;
        }
        
        if ( spiStatus.getValue() != SPI_Status.READY_COMM_MODE ) {
            printAndDestroyApp(
                    "Could not read/write because device is not in"
                    + "communication mode. SPI device mode: " + spiStatus.getValue()
            );
            return;
        }
  
        // sending data to SPI device
        for ( int i = 0; i < 1; i++ ) {
            spiStatus = waitForCommMode(10, 10);
            if ( spiStatus == null ) {
                printAndDestroyApp("SPI mode not available.");
                return;
            }
            
            // before sending data the module has to be in communication mode            
            if ( spiStatus.getValue() != SPI_Status.READY_COMM_MODE ) {
                printAndDestroyApp(
                        "TR is not in communication mode. "
                        + "SPI device mode: " + spiStatus.getValue()
                );
                return;
            }
            
            // sending some data to the device
            VoidType result = spiMaster.sendData( new short[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 } );
            if ( result == null ) {
                printAndDestroyApp("Error while sending data: " + spiMaster.getLastError().getDescr());
                return;
            }

            // checking SPI status to see if a reception of sent data was successfull 
            // on the device side 
            spiStatus = spiMaster.getSlaveStatus();
            if ( spiStatus == null ) {
                printAndDestroyApp("Error while checking SPI status: " + spiMaster.getLastError().getDescr());
                return;
            }
        
            if ( spiStatus.getValue() == SPI_Status.NOT_READY_CRCM_OK ) {
                //System.out.println("Data successfully received by SPI device.");
            } 

            if ( spiStatus.getValue() == SPI_Status.NOT_READY_CRCM_ERROR ) {
                System.out.println("CRCM error.");
            }
        }        
        
        // wait for data to read 10s
        System.out.println("Waiting for data to read: ");
        
        spiStatus = waitForDataReadyMode(1000, 10);
        if ( spiStatus == null ) {
            printAndDestroyApp("SPI mode not available.");
            return;
        }
        
        if ( !spiStatus.isDataReady() ) {
            printAndDestroyApp("Data not ready.");
            return;
        }
        
        int dataLen = 0;
        if ( spiStatus.getValue() == 0x40 ) {
            dataLen = 64;
        } else {
           dataLen = spiStatus.getValue() - 0x40;
        }
        System.out.println("Data length: " + dataLen);
        
        short[] receivedData = spiMaster.readData(dataLen);
        if ( receivedData == null ) {
            printAndDestroyApp("Error while receiving data: " + spiMaster.getLastError().getDescr());
            return;
        }
        
        for ( int i = 0; i < receivedData.length; i++ ) {
            System.out.print(receivedData[i]);
        }
        System.out.println();
        
        destroyApp(true);
        notifyDestroyed();
    }
    
    public void pauseApp() {
        if ( spiMaster != null ) {
            try {
                spiMaster.destroy();
            } catch ( IOException ex ) {
                System.err.println("Destroying the SPI master failed while app pausing: " + ex);
            }
        }
        spiMaster = null;
    }
    
    public void destroyApp(boolean unconditional) {
        if ( spiMaster != null ) {
            try {
                spiMaster.destroy();
            } catch ( IOException ex ) {
                System.err.println("Destroying the SPI master failed while app destroying: " + ex);
            }
        }
        spiMaster = null;
    }
}
