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

package com.microrisc.spi.iqrf_protocol;

/**
 * Check SPI status response packet.
 * 
 * @author Michal Konopa
 */
public final class CheckSPI_StatusResponse extends AbstractResponsePacket {

    /** Length (in bytes). */
    public static final int LENGTH = 1;
    
    private final SPI_Status spiStatus;
    
    private static SPI_Status checkSpiStatus(SPI_Status spiStatus) {
        if ( spiStatus == null ) {
            throw new IllegalArgumentException("SPI status cannot be null");
        }
        return spiStatus;
    }
    
    /**
     * Creates new SPI response packet.
     * @param spiStatus SPI status
     * @throws IllegalArgumentException if {@code spiStatus} is {@code null}
     */
    public CheckSPI_StatusResponse(SPI_Status spiStatus) {
        this.spiStatus = checkSpiStatus(spiStatus);
    }

    /**
     * @return the SPI status
     */
    public SPI_Status getSpiStatus() {
        return spiStatus;
    }
}
