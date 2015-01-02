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

package com.microrisc.rpi.spi.iqrf;

import com.microrisc.rpi.spi.SPI_Exception;

/**
 * Simple SPI Master implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleSPI_Master implements SPI_Master {
    // native methods
    private native void stub_initialize(String masterId);
    private native SPI_Status stub_getSlaveStatus();
    private native void stub_sendData(short[] data);
    private native short[] stub_readData(int dataLen);
    private native void stub_destroy();
    
    /**
     * Loads supporting Java Stub .so library.
     */
    static {
        System.loadLibrary("rpi_spi_iqrf_javastub");
    }
    
    
    private String checkMasterId(String masterId) {
        if ( masterId == null ) {
            throw new IllegalArgumentException("Master ID cannot be null");
        }
        return masterId;
    }
    
    private short[] checkDataToSend(short[] dataToSend) {
        if ( dataToSend == null ) {
            throw new IllegalArgumentException("Data to send cannot be null");
        }
        if ( dataToSend.length == 0 ) {
            throw new IllegalArgumentException("Data to send cannot be of zero size");
        }
        return dataToSend;
    }
    
    private int checkLengthOfDataToRead(int dataLen) {
        if ( dataLen <= 0 ) {
            throw new IllegalArgumentException("Length of data to read cannot be <= 0 ");
        }
        return dataLen;
    }
    
    
    /**
     * Creates new SPI Master.
     * @param masterId master ID
     * @throws SPI_Exception if an error has occured during creation
     * @throws IllegalArgumentException if {@code masterId} is {@code null}
     */
    public SimpleSPI_Master(String masterId) throws SPI_Exception {
        stub_initialize( checkMasterId(masterId) );
    }
    
    @Override
    public SPI_Status getSlaveStatus() throws SPI_Exception {
        return stub_getSlaveStatus();
    }

    /**
     * @throws IllegalArgumentException if {@code data} is {@code null} or if
     *         {@code data} length is of zero size
     */
    @Override
    public void sendData(short[] data) throws SPI_Exception {
        stub_sendData( checkDataToSend(data) );
    }
    
    /**
     * @throws IllegalArgumentException if {@code dataLen} is less then or 
     *         equal to the 0
     */
    @Override
    public short[] readData(int dataLen) throws SPI_Exception {
        return stub_readData( checkLengthOfDataToRead(dataLen) );
    }
    
    @Override
    public void destroy() {
        try {
            stub_destroy();
        } catch (Exception e) {
            System.err.println("Error while destroying SPI master: " + e);
        }
    }   
}
