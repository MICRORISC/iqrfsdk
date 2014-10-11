
package com.microrisc.simply.iqrf.dpa.types;

/**
 * HW Profile ID.
 * 
 * @author Michal Konopa
 */
public enum HWProfileID {
    /** No HW Profile implemented. */
    NONE            (0x00),
    
    /** User HW Profile ID area. */
    USER_AREA       (0x0101),
    
    /** Reserved HW Profile ID area. */
    RESERVED_AREA   (0xF000),
    
    /** Reserved HW Profile ID. */
    RESERVED        (0xFFFE),
    
    /** Use this type at memory write message to override HW Profile ID check. */
    DO_NOT_CHECK    (0xFFFF)
    ;
    
    // HW Profile code
    private final int code;
    
    
    private HWProfileID(int code) {
        this.code = code;
    }
    
    /**
     * Returns integer value of HW Profile ID.
     * @return integer value of HW Profile ID.
     */
    public int getCodeValue() {
        return code;
    }
}
