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
 * Baud rate constants.
 * 
 * @author Michal Konopa
 */
public final class BaudRate {
    private final int baudRate;
    
    private BaudRate(int baudRate) {
        this.baudRate = baudRate;
    }
    
    public int getValue() {
        return baudRate;
    }
    
    public static final BaudRate BaudRate_100 = new BaudRate(100);
    public static final BaudRate BaudRate_250 = new BaudRate(250);
}
