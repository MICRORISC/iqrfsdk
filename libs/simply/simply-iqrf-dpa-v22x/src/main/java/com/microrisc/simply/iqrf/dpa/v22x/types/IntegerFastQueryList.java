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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * List of values with fast querying about presence of its members. 
 * 
 * @author Michal Konopa
 */
public final class IntegerFastQueryList {
    /** Indication members presence. */
    private final Set<Integer> members;
    
    /** List of members. */
    private final List<Integer> membersList;
    
    
    /**
     * Creates new bonded nodes object. 
     * @param membersList list of IDs of bonded nodes
     * @throws IllegalArgumentException if nodes list is {@code null}
     */
    public IntegerFastQueryList(List<Integer> membersList) {
        if ( membersList == null ) {
            throw new IllegalArgumentException("Nodes list cannot be null");
        }
        
        this.members = new HashSet<>();
        this.membersList = new LinkedList<>();
        
        for ( int nodeId : membersList ) {
            this.membersList.add(nodeId);
            this.members.add(nodeId);
        }
    }
    
    /**
     * Returns the size of the list.
     * @return the size of the list.
     */
    public int getSize() {
        return members.size();
    }
    
    /**
     * Returns indication, wheather specified member is on the list.
     * @param member member whose presence to examine
     * @return {@code true} if specified member is present in this list
     *         {@code false}, otherwise
     */
    public boolean isPresent(int member) {
        return members.contains(member);
    }
    
    /**
     * Returns list of all members.
     * @return list of all members.
     */
    public List<Integer> getList() {
        List<Integer> listToReturn = new LinkedList<>();
        for ( Integer nodeId : membersList ) {
            listToReturn.add(nodeId);
        }
        return listToReturn;
    }
    
    public String membersListToString() {
        if ( membersList.isEmpty() ) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for ( Integer nodeId : membersList ) {
            sb.append(nodeId);
            sb.append(", ");
        }
        sb.delete(sb.length()-2, sb.length());
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Members: " + membersListToString() + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
}
