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

package com.microrisc.spi;

import com.microrisc.spi.iqrf_protocol.SPI_Status;
import java.io.IOException;

/**
 * SPI Master.
 * 
 * @author Michal Konopa
 */
public interface SPI_Master 
    extends 
        ResponseTimeoutService,
        ErrorService
{
    /**
     * Returns currently used baud rate.
     * @return currently used baud rate
     */
    BaudRate getBaudRate();
    
    /**
     * Returns SPI status information of slave. Waits for a response for default 
     * timeout. If an error has occured, {@code null} is returned.
     * @return SPI status information of slave.
     * @return {@null}, if an error has occured
     */
    SPI_Status getSlaveStatus();
    
    /**
     * Returns SPI status information of slave. Waits for a response for a specified 
     * timeout. If an error has occured, {@code null} is returned.
     * @param responseTimeout timeout for waiting for a response (in ms)
     * @return SPI status information of slave.
     * @return {@code null}, if an error has occured
     */
    SPI_Status getSlaveStatus(long responseTimeout);
    
    /**
     * Sends specified data to slave. Waits for a response for default timeout.
     * If an error has occured, {@code null} is returned.
     * @param data data to be sent to slave
     * @return {@code VoidType} object, if method has performed correctly 
     * @return {@null}, if an error has occured
     */
    VoidType sendData(short[] data);
    
    /**
     * Sends specified data to slave. Waits for a response for a specified timeout.
     * If an error has occured, {@code null} is returned.
     * @param data data to be sent to slave
     * @param responseTimeout timeout for waiting for a response (in ms)
     * @return {@code VoidType} object, if method has performed correctly 
     * @return {@null}, if an error has occured
     */
    VoidType sendData(short[] data, long responseTimeout);
    
    /**
     * Reads data from slave. Waits for a response for default timeout. 
     * If an error has occured, {@code null} is returned.
     * @param dataLen length of data to read
     * @return data read from slave.
     * @return {@null}, if an error has occured
     */
    short[] readData(int dataLen);
    
    /**
     * Reads data from slave. Waits for a response for a specified timeout. 
     * If an error has occured, {@code null} is returned.
     * @param dataLen length of data to read
     * @param responseTimeout timeout for waiting for a response (in ms)
     * @return data read from slave.
     * @return {@null}, if an error has occured
     */
    short[] readData(int dataLen, long responseTimeout);
    
    /**
     * Terminates communication with slave and frees up used resources.
     * @throws IOException if an I/O error occurs
     */
    void destroy() throws IOException;
    
}
