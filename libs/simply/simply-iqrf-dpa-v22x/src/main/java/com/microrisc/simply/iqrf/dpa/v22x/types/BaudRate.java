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

package com.microrisc.simply.iqrf.dpa.v22x.types;

/**
 * Baud rate constants.
 * 
 * @author Michal Konopa
 */
public enum BaudRate {
    BR1200      (0x00),
    BR2400      (0x01),
    BR4800      (0x02),
    BR9600      (0x03),
    BR19200     (0x04),
    BR38400     (0x05),
    BR57600     (0x06),
    BR115200    (0x07);
    
    private final int baudRateConstant;
    
    
    private BaudRate(int baudRateConstant) {
        this.baudRateConstant = baudRateConstant;
    }
    
    /**
     * Returns value of constant. 
     * @return value of constant.
     */
    public int getBaudRateConstant() {
        return baudRateConstant;
    }
}
