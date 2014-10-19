

package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * Access to collected bytes of FRC command belonging to one concrete node.
 * 
 * @author Michal Konopa
 */
public interface FRC_CollectedBytes {
    /**
     * Returns value of byte.
     * @return value of byte
     */
    short getByte();
}
