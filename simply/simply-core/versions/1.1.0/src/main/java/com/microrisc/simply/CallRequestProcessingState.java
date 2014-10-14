

package com.microrisc.simply;

/**
 * Call request processing states.
 * 
 * @author Michal Konopa
 */
public enum CallRequestProcessingState {
    /** Request is waiting for processing. */
    WAITING_FOR_PROCESSING,
    
    /** Request has been already processed and it is currently waiting for a result. */
    WAITING_FOR_RESULT,
    
    /** Result for a waiting request has arrived. */
    RESULT_ARRIVED,
    
    /** Processing of a call request has been cancelled. */
    CANCELLED,
    
    /** an error has encountered during processing of a call request. */
    ERROR
}
