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
 * Encapsulates device current SPI mode. Values and meaning of constituent 
 * constants are in accordance with the table in IQRF SPI User's guide 
 * (chapter SPI status).
 * <p>
 * Peer class for SPIModes enum.
 * 
 * @version     1.0
 */
public enum J_SPIModes {
    DISABLED        (0x0), 
    SUSPENDED       (0x07), 
    BUFF_PROTECT    (0x3F), 
    CRCM_ERR        (0x3E), 
    READY_COMM      (0x80), 
    READY_PROG      (0x81),
    READY_DEBUG     (0x82),
    SLOW_MODE       (0x83),
    HW_ERROR        (0xFF);
    
    /** Numeric value of mode. */
    private final int mode;
    
    J_SPIModes(int mode) {
        this.mode = mode;
    }
    
    /**
     * Returns numeric value of mode.
     * @return numeric value of mode.
     */
    public int getMode() {
        return this.mode;
    }
}
