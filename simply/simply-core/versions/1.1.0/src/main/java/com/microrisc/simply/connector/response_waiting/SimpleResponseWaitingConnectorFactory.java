
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
