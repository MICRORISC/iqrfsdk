
package com.microrisc.simply.iqrf.dpa.v210.types;

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
        
        this.members = new HashSet<Integer>();
        this.membersList = new LinkedList<Integer>();
        
        for ( Integer nodeId : membersList ) {
            this.membersList.add(new Integer(nodeId));
            this.members.add(nodeId);
        }
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
        List<Integer> listToReturn = new LinkedList<Integer>();
        for ( Integer nodeId : membersList ) {
            listToReturn.add(new Integer(nodeId));
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
