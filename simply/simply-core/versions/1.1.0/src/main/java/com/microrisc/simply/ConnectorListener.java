package com.microrisc.simply;

import java.util.UUID;

/**
 * Listener of information about a processing of a call requests sended by 
 * various objects ( mainly by Device Objects ) to underlaying network via 
 * {@code ConnectorService } interface.
 * 
 * @author Michal Konopa
 */
public interface ConnectorListener {
    /**
     * Will be called by connector to inform listener about processing of a call request 
     * executed by that DO.
     * @param procInfo information about processing of executed call request.
     * @param callId unique ID of the executed call request
     */
    void onCallRequestProcessingInfo(CallRequestProcessingInfo procInfo, UUID callId);
}
