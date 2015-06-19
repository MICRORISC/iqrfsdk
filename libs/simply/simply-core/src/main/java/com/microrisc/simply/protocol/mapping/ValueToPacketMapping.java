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
 * Base class of classes, which descibe mapping from Java values to
 * protocol layer packets.
 * 
 * @author Michal Konopa
 */
public class ValueToPacketMapping {
    /** Starting position of converted value in protocol packet. */
    private int startingPosition = 0;
    
    /** Convertor to use for conversion of Java value to sequence of bytes. */
    private AbstractConvertor convertor = null;
    
    
    /**
     * Creates new value - to - protocol mapping.
     * @param startingPosition starting position of converted value in the protocol packet
     * @param convertor Convertor to use for conversion of Java value
     */
    public ValueToPacketMapping(int startingPosition, AbstractConvertor convertor) {
        this.startingPosition = startingPosition;
        this.convertor = convertor;
    }
    
    /**
     * @return starting position of converted value in protocol packet
     */
    public int getStartingPosition() {
        return startingPosition;
    }

    /**
     * @return the convertor
     */
    public AbstractConvertor getConvertor() {
        return convertor;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "starting position =" + startingPosition + 
                ", convertor=" + convertor + 
                " }");
    }
}
