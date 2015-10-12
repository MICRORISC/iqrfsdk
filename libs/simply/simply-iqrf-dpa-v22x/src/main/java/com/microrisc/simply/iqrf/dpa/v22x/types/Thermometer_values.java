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

import java.io.Serializable;

/**
 * Encapsulates information about Thermometer result.
 * 
 * @author Michal Konopa
 */
public final class Thermometer_values implements Serializable {
    /** Value in Celsius degree. */
    private final short value;
        
    /** Fractional part of value. */
    private final byte fractialValue; 
    
    
    /**
     * Creates new {@code Thermometer_values} object.
     * @param value integer part in Celsius degree, not rounded. See return value 
     * of getTemperature() OS function. If the temperature sensor is not installed 
     * (see HWP Configuration) then the returned value is 0x80 = -128 Celsius degree.
     * @param fractialValue fractional part of Celsius degree value 
     */
    public Thermometer_values(short value, byte fractialValue) {
        this.value = value;
        this.fractialValue = fractialValue;
    }

    /**
     * Integer part in Celsius degree, not rounded. See return value 
     * of getTemperature() OS function. If the temperature sensor is not installed 
     * (see HWP Configuration) then the returned value is 0x80 = -128 Celsius degree.
     * @return integer part in Celsius degree, not rounded
     */
    public short getValue() {
        return value;
    }

    /**
     * @return fractional part of Celsius degree value
     */
    public byte getFractialValue() {
        return fractialValue;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Value: " + value + NEW_LINE);
        strBuilder.append(" Fractional part: " + fractialValue + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormattedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("Value: " + value + NEW_LINE);
        strBuilder.append("Fractional part: " + fractialValue + NEW_LINE);
        
        return strBuilder.toString();
    }
}
