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

import com.microrisc.simply.typeconvertors.AbstractConvertor;

/**
 * Stores data of mapping from packet into Java value. 
 * 
 * @author Michal Konopa
 */
public final class PacketToValueMapping {
    /** Starting position of data to convert. */
    private int startingPosition = 0;
    
    /** Length of data to convert. */
    private int length = 0;
    
    /** Convertor to use.  */
    private AbstractConvertor convertor = null;
    
    /** Indicates, wheather to take for conversion a source packet up to its end. */
    private boolean upToEnd = false;
    
    
    /**
     * Creates new mapping data. The data to be converted starts at specified
     * starting position and spans up the end of packet.
     * 
     * @param startingPosition starting position of data to convert
     * @param convertor convertor to use
     */
    public PacketToValueMapping(int startingPosition, AbstractConvertor convertor) {
        this.startingPosition = startingPosition;
        this.convertor = convertor;
        this.upToEnd = true;
    }
    
    /**
     * Creates new mapping data.
     * @param startingPosition starting position of data to convert
     * @param length length of data to convert
     * @param convertor convertor to use
     */
    public PacketToValueMapping(
            int startingPosition, int length, AbstractConvertor convertor
    ) {
        this.startingPosition = startingPosition;
        this.length = length;
        this.convertor = convertor;
        this.upToEnd = false;
    }

    /**
     * @return the starting position of data to convert
     */
    public int getStartingPosition() {
        return startingPosition;
    }

    /**
     * Returns the length of data to convert. If the returned value of {@code 
     * isUpToEnd} returns {@code true}, value returned by this method is irrelevant.
     * 
     * @return the length of data to convert
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Indicates, wheather to take for conversion a source packet up to end. If 
     * this is the case, the returned value of {@code getLength} method is 
     * irrelevant.
     * 
     * @return indication, if to take the remainder of source packet for conversion
     */
    public boolean isUpToEnd() {
        return upToEnd;
    }
            
    /**
     * @return the convertor to use
     */
    public AbstractConvertor getConvertor() {
        return convertor;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "startig position=" + startingPosition + 
                ", length=" + length +
                ", convertor=" + convertor +
                " }");
    }
}
