
package com.microrisc.simply.network;

import com.microrisc.simply.SimplyException;

/**
 * Base class, which describes errors and exceptions that occurs on network layer
 * stage of communication.
 * 
 * @author Michal Konopa
 */
public class NetworkLayerException extends SimplyException {
    public NetworkLayerException(String message) {
        super(message);
    }
    
    public NetworkLayerException(Throwable cause) {
        super(cause);
    }
}
