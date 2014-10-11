package com.microrisc.simply.types;

import com.microrisc.simply.SimplyException;

/**
 * Error, which occcur during process of conversion between Java types and IQRF 
 * types.
 * 
 * @author Michal Konopa
 */
public class ValueConversionException extends SimplyException {
    public ValueConversionException(String message) {
        super(message);
    }
    
    public ValueConversionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ValueConversionException(Throwable cause) {
        super(cause);
    }
}
