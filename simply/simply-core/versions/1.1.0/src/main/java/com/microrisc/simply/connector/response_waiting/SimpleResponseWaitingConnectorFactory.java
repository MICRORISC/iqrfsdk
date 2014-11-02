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

import com.microrisc.simply.ProtocolLayerService;
import com.microrisc.simply.connector.AbstractConnectorFactory;
import org.apache.commons.configuration.Configuration;

/**
 * Simple response waiting connector factory.
 * 
 * @author Michal Konopa
 */
public final class SimpleResponseWaitingConnectorFactory 
extends AbstractConnectorFactory<ProtocolLayerService, Configuration, ResponseWaitingConnector> {

    @Override
    public ResponseWaitingConnector getConnector(ProtocolLayerService protocolLayerService, 
            Configuration configuration
    ) throws Exception {
        SimpleResponseWaitingConnector connector = new SimpleResponseWaitingConnector(protocolLayerService);
        new SimpleResponseWaitingConnectorConfigurator().configure(connector, configuration);
        return connector;
    }
    
}
