

package com.microrisc.simply.iqrf.dpa.v210.di_services;

import com.microrisc.simply.iqrf.dpa.v210.types.DPA_AdditionalInfo;
import java.util.UUID;

/**
 * Provides access to additional DPA information from incomming messages.
 * 
 * @author Michal Konopa
 */
public interface DPA_AdditionalInfoService {
    /**
     * Returns additional DPA information relating to the specified processed call.
     * @param callId ID of method call, which get additional information to
     * @return additional DPA information relating to the specified processed call.
     */
    DPA_AdditionalInfo getDPA_AdditionalInfo(UUID callId);
    
    /**
     * Returns additional DPA information relating to the last processed call.
     * @return additional DPA information relating to the last processed call.
     */
    DPA_AdditionalInfo getDPA_AdditionalInfoOfLastCall();
}
