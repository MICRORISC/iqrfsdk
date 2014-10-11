
package com.microrisc.simply.errors;

/**
 * Information about errors, which encounters during processing of Device Object
 * method call.
 * 
 * @author Michal Konopa
 */
public interface CallRequestProcessingError {
    /**
     * Returns type of processing error.
     * @return type of processing error.
     */
    CallRequestProcessingErrorType getErrorType();
}
