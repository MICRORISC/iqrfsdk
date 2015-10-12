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

package com.microrisc.simply.iqrf.dpa.v22x.devices.impl;

/**
 * Helper class to check if input values conforms to various data types.
 * <p>
 * Main usage is for checking Device Interfaces method arguments.
 * 
 * @author Michal Konopa
 */
class DataTypesChecker {
    /** Lower value of Byte. */
    public static final int BYTE_LOWER_BOUND = 0x00;
    
    /** Upper value of Byte. */
    public static final int BYTE_UPPER_BOUND = 0xFF;
    
    public static boolean isByteValue(int value) {
        return (value >= BYTE_LOWER_BOUND) && (value <= BYTE_UPPER_BOUND);
    }
    
}
