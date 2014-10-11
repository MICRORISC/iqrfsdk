
package com.microrisc.simply.errors;

/**
 * Base class of method call processing errors.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractCallRequestProcessingError 
extends Exception
implements CallRequestProcessingError 
{
    protected AbstractCallRequestProcessingError() {
    }
    
    protected AbstractCallRequestProcessingError(String message) {
        super(message);
    }
    
    protected AbstractCallRequestProcessingError(Throwable cause) {
        super(cause);
    }
}
