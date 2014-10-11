
package com.microrisc.simply.errors;

/**
 * Errors, which encounters during processing of a method call requests comming 
 * from connector to application protocol packets.
 * 
 * @author Michal Konopa
 */
public class ProcessingRequestAtProtocolLayerError extends AbstractCallRequestProcessingError {
    private final CallRequestProcessingErrorType errorType = 
            CallRequestProcessingErrorType.PROCESSING_REQUEST_AT_PROTOCOL_LAYER; 
    
    public ProcessingRequestAtProtocolLayerError() {
    }
    
    public ProcessingRequestAtProtocolLayerError(String message) {
        super(message);
    }
    
    public ProcessingRequestAtProtocolLayerError(Throwable cause) {
        super(cause);
    }
    
    @Override
    public CallRequestProcessingErrorType getErrorType() {
        return errorType;
    }
}
