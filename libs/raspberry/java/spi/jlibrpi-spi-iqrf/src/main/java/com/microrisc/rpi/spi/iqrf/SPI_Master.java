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
 * SPI Master.
 * 
 * @author Michal Konopa
 */
public interface SPI_Master {
    /**
     * Returns SPI status information of slave.
     * @return SPI status information of slave.
     * @throws SPI_Exception, if an communication error has encountered
     */
    SPI_Status getSlaveStatus() throws SPI_Exception;
    
    /**
     * Sends specified data to slave.
     * @param data data to be sent to slave
     * @throws SPI_Exception, if an communication error has encountered
     */
    void sendData(short[] data) throws SPI_Exception;
    
    /**
     * Reads data from slave.
     * @param dataLen length of data to read
     * @return data read from slave.
     * @throws SPI_Exception, if an communication error has encountered
     */
    short[] readData(int dataLen) throws SPI_Exception;
    
    /**
     * Terminates communication with slave and frees up used resources.
     */
    void destroy();
}
