
package com.microrisc.simply.errors;

/**
 * Error, which has encountered in processing of call request in network - 
 * network dependent error.
 * 
 * @author Michal Konopa
 */
public class NetworkInternalError extends AbstractCallRequestProcessingError {
    private final CallRequestProcessingErrorType errorType = 
            CallRequestProcessingErrorType.NETWORK_INTERNAL; 
    
    public NetworkInternalError() {
    }
    
    public NetworkInternalError(String message) {
        super(message);
    }
    
    public NetworkInternalError(Throwable cause) {
        super(cause);
    }
    
    @Override
    public CallRequestProcessingErrorType getErrorType() {
        return errorType;
    }
}
