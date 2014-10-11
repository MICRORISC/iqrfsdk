
package com.microrisc.simply.errors;

/**
 * Call request has not been dispatched from connector to protocol layer.
 * 
 * @author Michal Konopa
 */
public class DispatchingRequestToProtocolLayerError 
extends AbstractCallRequestProcessingError {
    private final CallRequestProcessingErrorType errorType = 
            CallRequestProcessingErrorType.DISPATCHING_REQUEST_TO_PROTOCOL_LAYER; 
    
    public DispatchingRequestToProtocolLayerError() {
    }
    
    public DispatchingRequestToProtocolLayerError(String message) {
        super(message);
    }
    
    public DispatchingRequestToProtocolLayerError(Throwable cause) {
        super(cause);
    }
    
    @Override
    public CallRequestProcessingErrorType getErrorType() {
        return errorType;
    }
}
