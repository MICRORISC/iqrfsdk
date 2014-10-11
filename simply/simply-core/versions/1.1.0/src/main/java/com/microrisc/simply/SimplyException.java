
package com.microrisc.simply;

/**
 * Main exception class of errors, which encounter during Simply initialization,
 * runnning and destruction.
 * 
 * @author Michal Konopa
 */
public class SimplyException extends Exception {
    public SimplyException(String message) {
        super(message);
    }
    
    public SimplyException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SimplyException(Throwable cause) {
        super(cause);
    }
}
