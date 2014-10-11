
package com.microrisc.simply.errors;

/**
 * Error describes situation, when call request has failed to dispatch itself 
 * to connector.
 * 
 * @author Michal Konopa
 */
public class DispatchingRequestToConnectorError extends AbstractCallRequestProcessingError {
    private final CallRequestProcessingErrorType errorType = 
            CallRequestProcessingErrorType.DISPATCHING_REQUEST_TO_CONNECTOR; 
    
    
    public DispatchingRequestToConnectorError() {
    }
    
    public DispatchingRequestToConnectorError(String message) {
        super(message);
    }
    
    public DispatchingRequestToConnectorError(Throwable cause) {
        super(cause);
    }
    
    @Override
    public CallRequestProcessingErrorType getErrorType() {
        return errorType;
    }
}
