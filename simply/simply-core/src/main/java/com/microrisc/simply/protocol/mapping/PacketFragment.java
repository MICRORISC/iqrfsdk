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
 * Describes fragment of packet data. 
 * 
 * @author Michal Konopa
 */
public final class PacketFragment {
    /** Position inside packet, where the data starts. */
    private int startingPosition = 0;
    
    /** Data. */
    private short[] data = null;
    
    
    /**
     * Constructor.
     * @param startingPosition position inside packet, where the data starts
     * @param data data
     */
    public PacketFragment(int startingPosition, short[] data) {
        this.startingPosition = startingPosition;
        this.data = data;
    }

    /**
     * @return the starting position
     */
    public int getStartingPosition() {
        return startingPosition;
    }

    /**
     * @return the data
     */
    public short[] getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "starting position=" + startingPosition + 
                ", data=" + data + 
                " }");
    }
}
