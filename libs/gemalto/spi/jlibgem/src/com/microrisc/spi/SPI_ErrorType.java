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

/**
 * SPI errors constants.
 * 
 * @author Michal Konopa
 */
public final class SPI_ErrorType {
    
    private SPI_ErrorType() {}    
    
    /** I/O problem during communication. */
    public static final SPI_ErrorType IO_ERROR = new SPI_ErrorType();
    
    /** Error in format of read data. */
    public static final SPI_ErrorType FORMAT_ERROR = new SPI_ErrorType();
    
    /** 
     * Protocol error.
     * Details can be found in a description of com.cintetion.io.SpiConnection interface.
     */
    public static final SPI_ErrorType PROTOCOL_ERROR = new SPI_ErrorType();
    
    /** Incomming message has incorrect ID.  */
    public static final SPI_ErrorType INCORRECT_MESSAGE_ID = new SPI_ErrorType();
    
    /** Bad data was received.  */
    public static final SPI_ErrorType BAD_DATA = new SPI_ErrorType();
}
