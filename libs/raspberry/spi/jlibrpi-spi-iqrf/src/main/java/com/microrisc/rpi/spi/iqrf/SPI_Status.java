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

/**
 * Status of SPI Slave device. Individual constants correspond to ones described
 * in document: SPI. Implementation in IQRF TR modules. User Guide.
 * <a href="http://www.iqrf.cz/weben/downloads.php?id=85">SPI in IQRF TR modules</a>  
 * on page 4.
 * 
 * @author Michal Konopa
 */
public final class SPI_Status {
    public static final int NOT_ACTIVE_DISABLED     = 0x0;
    public static final int SUSPENDED               = 0x07;
    public static final int NOT_READY_CRCM_OK       = 0x3F;
    public static final int NOT_READY_CRCM_ERROR    = 0x3E;
    public static final int READY_COMM_MODE         = 0x80;
    public static final int READY_PROG_MODE         = 0x81;
    public static final int READY_DEBUG_MODE        = 0x82;
    public static final int NOT_ACTIVE_HW_ERROR     = 0xFF;        
    
    /** Status value. */
    private final int value;
    
    /** SPI data ready indication. */
    private final boolean dataReady; 
    
    
    /**
     * Creates new SPI Status object. 
     * @param value integer value of the SPI status
     * @param dataReady SPI data ready indication
     */
    SPI_Status(int value, boolean dataReady) {
        this.value = value;
        this.dataReady = dataReady;
    }
    
    /**
     * Returns value of status.
     * @return value of status.
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Returns {@code true} if the status is SPI data ready. Otherwise, it returns
     * {@code false}.
     * @return {@code true} if the status is SPI data ready <br>
     *         {@code false} otherwise
     */
    public boolean isDataReady() {
        return dataReady;
    }
}
