
package com.microrisc.simply.errors;

/**
 * Types of errors, which encounter during processing of a call requests.
 * 
 * @author Michal Konopa
 */
public enum CallRequestProcessingErrorType {
    /** Dispatching call request to connector failed. */
    DISPATCHING_REQUEST_TO_CONNECTOR,
    
    /** Dispatching call request to protocol layer failed. */
    DISPATCHING_REQUEST_TO_PROTOCOL_LAYER,
    
    /** Processing of a call request at protocol layer failed. */
    PROCESSING_REQUEST_AT_PROTOCOL_LAYER,
    
    /** Processing of a response at protocol layer failed. */
    PROCESSING_RESPONSE_AT_PROTOCOL_LAYER,
    
    /** Network internal error. */
    NETWORK_INTERNAL;
}
