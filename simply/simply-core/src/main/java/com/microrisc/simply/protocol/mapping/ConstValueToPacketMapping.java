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

package com.microrisc.simply.protocol.mapping;

/**
 * Describes mapping of constant Java value into packet of protocol layer.
 * 
 * @author Michal Konopa
 */
public final class ConstValueToPacketMapping {
    /** Starting position of converted value in protocol packet. */
    private int startingPosition = 0;
    
    /** Protocol packet representation of converted Java value. */
    private short[] convertedValue = null;
    
    
    /**
     * Creates new Constant Mapping object.
     * @param startingPosition starting position of converted value in protocol packet
     * @param convertedValue converted value
     */
    public ConstValueToPacketMapping(int startingPosition, short[] convertedValue) {
        this.startingPosition = startingPosition;
        this.convertedValue = convertedValue;
    }
    
    /**
     * @return the starting position of converted value in protocol packet
     */
    public int getStartingPosition() {
        return startingPosition;
    }
    
    /**
     * @return protocol packet representation of converted Java value
     */
    public short[] getConvertedValue() {
        return convertedValue;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "starting position=" + startingPosition + 
                ", converted value=" +  convertedValue + 
                " }");   
    }
}
