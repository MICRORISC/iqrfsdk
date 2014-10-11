
package com.microrisc.simply.connector.response_waiting;

import com.microrisc.simply.config.AbstractConfigurator;
import org.apache.commons.configuration.Configuration;

/**
 * Simple configurator for response waiting connector.
 * 
 * @author Michal Konopa
 */
public final class SimpleResponseWaitingConnectorConfigurator 
extends AbstractConfigurator<ResponseWaitingConnector, Configuration>{

    @Override
    public void configure(ResponseWaitingConnector connector, Configuration configuration) {
        int maxSendAttempts = configuration.getInt(
                "connector.type.responseWaiting.maxSendAttempts", -1 
        );
        if (maxSendAttempts != -1) {
            connector.setMaxSendAttempts(maxSendAttempts);
        }
        
        long responseTimeoutSetting = configuration.getLong(
                "connector.type.responseWaiting.responseTimeout", -1
        );
        if (responseTimeoutSetting != -1 ) {
            connector.setResponseTimeout(responseTimeoutSetting);
        }
        
        long attemptPause = configuration.getLong(
                "connector.type.responseWaiting.attemptPause", -1
        );
        if (attemptPause != -1 ) {
            connector.setAttemptPause(attemptPause);
        }
        
        long betweenSendPause = configuration.getLong(
                "connector.type.responseWaiting.betweenSendPause", -1
        );
        if (betweenSendPause != -1 ) {
            connector.setBetweenSendPause(betweenSendPause);
        }
    }
    
}
