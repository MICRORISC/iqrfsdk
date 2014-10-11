
package com.microrisc.simply.iqrf.dpa.types;

/**
 * HW Profile Types.
 * 
 * @author Michal Konopa
 */
public enum HWProfileType {
    /** No HW Profile implemented. */
    NONE            (0x00),
    
    /** User HW Profile type area. */
    USER_AREA       (0x0101),
    
    /** Reserved HW Profile type area. */
    RESERVED_AREA   (0xF000),
    
    /** Reserved HW Profile type. */
    RESERVED        (0xFFFE),
    
    /** Use this type at memory write message to override HW Profile check. */
    DO_NOT_CHECK    (0xFFFF)
    ;
    
    // HW Profile code
    private final int code;
    
    
    private HWProfileType(int code) {
        this.code = code;
    }
    
    /**
     * Returns integer value of HW Profile code.
     * @return integer value of HW Profile code.
     */
    public int getCodeValue() {
        return code;
    }
}
