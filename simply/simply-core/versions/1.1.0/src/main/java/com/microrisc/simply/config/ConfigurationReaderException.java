
package com.microrisc.simply.config;

/**
 * Base class of configuration reader exceptions.
 * 
 * @author Michal Konopa
 */
public class ConfigurationReaderException extends Exception {
    public ConfigurationReaderException(String message) {
        super(message);
    }
    
    public ConfigurationReaderException(Throwable cause) {
        super(cause);
    }
}
