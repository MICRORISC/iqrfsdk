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

import java.io.IOException;

/**
 * Factory class for creating SPI Master objects.
 * 
 * @author Michal Konopa
 */
public final class SPI_MasterFactory {
    /** Default device identifier. */
    public static final String DEFAULT_DEVICE_ID = "0";
    
    /**
     * Returns SPI Master implementation with baud rate of 100 and specified clock mode.
     * @return SPI Master implementation
     * @throws IOException if an I/O error occurs
     */
    public static SPI_Master getMaster() throws IOException {
        return getMaster(BaudRate.BaudRate_100);
    } 
    
    /**
     * Returns SPI Master implementation with specified baud rate and clock mode.
     * @param baudRate baud rate to use
     * @return SPI Master implementation
     * @throws IOException if an I/O error occurs
     */
    public static SPI_Master getMaster(BaudRate baudRate) throws IOException {
        return new SimpleSPI_Master(DEFAULT_DEVICE_ID, baudRate);
    }
    
}
