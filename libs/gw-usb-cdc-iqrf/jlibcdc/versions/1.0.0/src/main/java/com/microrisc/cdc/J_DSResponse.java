/*
 * Public CDC Library
 * Copyright (C) 2012 MICRORISC s.r.o., www.microrisc.com
 * IQRF platform details: www.iqrf.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package com.microrisc.cdc;

/**
 * Response information of "DS-command". Precise meaning of constants can be
 * found in "CDC Implementation in IQRF USB devices User Guide".
 * <p>
 * Peer class for DSResponse.
 * 
 * @version     1.0
 */
public enum J_DSResponse {
    /** Data successfully sent to TR module. */
    OK      (0x0),
    /** 
     * Denotes one of the following situations: <br> 
     * - communication failure(checksum error)<br>
     * - length of message is out of range,<br> 
     * - data length mismatch 
     */
    ERR     (0x1),
    /** 
     * Denotes one of the following situations: <br> 
     * - SPI is busy<br>
     * - communication is just running,<br> 
     * - TR module is not in communication mode 
     */
    BUSY    (0x2);
    
    /** Numeric value of response. */
    private final int respValue;
    
    J_DSResponse(int respValue) {
        this.respValue = respValue;
    }
    
    /**
     * Returns numeric value of response.
     * @return numeric value of response.
     */
    public int getRespValue() {
        return this.respValue;
    }
}
