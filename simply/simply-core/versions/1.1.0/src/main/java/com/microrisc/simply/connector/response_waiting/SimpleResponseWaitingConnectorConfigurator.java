/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
