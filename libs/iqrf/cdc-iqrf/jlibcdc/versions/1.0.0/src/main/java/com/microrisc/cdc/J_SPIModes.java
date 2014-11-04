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
