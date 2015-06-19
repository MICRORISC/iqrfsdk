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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Describes set of values, which can be at specific position in protocol packet.
 * 
 * @author Michal Konopa
 */
public final class PacketPositionValues {
    /** Position in packet. */
    private int position = 0;
    
    /** Set of values at the position. */
    private final Set<Short> values = new HashSet<>();
    
    
    /**
     * Adds specified values into this object values. 
     * @param newValues new values to add
     */
    private void addNewValues(Collection<Short> newValues) {
        Iterator<Short> iter = newValues.iterator();
        while (iter.hasNext()) {
            values.add(iter.next());
        }
    }
    
    private void addNewValues(short[] newValues) {
        for (short newValue : newValues) {
            values.add(newValue);
        }
    }
    
    private String getValuesString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("{");
        for (Short value : values) {
            sb.append(value.toString());
            sb.append(", ");
        }
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * Creates new {@code PacketPositionValue} object. At the specified position in
     * the packet can only be the specified value.
     * @param position postition in the packet
     * @param value value of byte at the position
     */
    public PacketPositionValues(int position, short value) {
        this.position = position;
        this.values.add(value);
    }
    
    /**
     * Creates new {@code PacketPositionValue} object. At the specified position in
     * the packet can be any one of the values from the specified collection.
     * @param position postition in the packet
     * @param values values, which can be at specified position
     */
    public PacketPositionValues(int position, Collection<Short> values) {
        this.position = position;
        addNewValues(values);
    }
    
    /**
     * Creates new {@code PacketPositionValue} object. At the specified position in
     * the packet can be any one of the values from the specified array.
     * @param position postition in the packet
     * @param values values, which can be at specified position
     */
    public PacketPositionValues(int position, short[] values) {
        this.position = position;
        addNewValues(values);
    }
    
    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Returns {@code true}, if specified value can be at the position. Otherwise
     * return {@code false}.
     * @param value value to check for
     * @return {@code true}, if specified value can be at the position <br>
     *         {@code false} otherwise
     */
    public boolean canBeAtPosition(short value) {
        return values.contains(value);
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "position=" + position + 
                ", values=" + getValuesString() + 
                " }");
    }
}
