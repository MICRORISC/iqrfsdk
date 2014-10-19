

package com.microrisc.simply.iqrf.dpa.v210.types;

/**
 * Access to collected bits of FRC command belonging to one concrete node.
 * 
 * @author Michal Konopa
 */
public interface FRC_CollectedBits {
    /**
     * Returns value of Bit.0.
     * @return value of Bit.0
     */
    byte getBit0();
    
    /**
     * Returns value of Bit.1.
     * @return value of Bit.1
     */
    byte getBit1();
}
