

package com.microrisc.simply.iqrf.dpa.connector;

import com.microrisc.simply.protocol.ProtocolLayer;
import com.microrisc.simply.connector.AbstractConnectorFactory;
import com.microrisc.simply.connector.response_waiting.SimpleResponseWaitingConnectorConfigurator;
import org.apache.commons.configuration.Configuration;

/**
 * DPA connector factory.
 * 
 * @author Michal Konopa
 */
public final class DPA_ConnectorFactory 
extends AbstractConnectorFactory<ProtocolLayer, Configuration, DPA_Connector> 
{
    /**
     * Types of connectors.
     */
    private static enum ConnectorType {
        RESPONSE_WAITING
    }
    
    /**
     * Mapping of configuration strings to connector types.
     */
    private static enum ConnectorConfigMapping {
        RESPONSE_WAITING    ("responseWaiting", ConnectorType.RESPONSE_WAITING);
        
        private final String configString;
        private final ConnectorType connectorType;
        
        private ConnectorConfigMapping(String configString, ConnectorType connectorType) {
            this.configString = configString;
            this.connectorType = connectorType;
        }

        /**
         * @return the configuration string
         */
        public String getConfigString() {
            return configString;
        }

        /**
         * @return the connector type
         */
        public ConnectorType getConnectorType() {
            return connectorType;
        }
    }
    
     
    /**
     * Returns type of connector.
     * @param configuration source configuration to discover
     * @return
     * @throws Exception 
     */
    private ConnectorType getConnectorType(Configuration configuration) throws Exception {
        String connTypeStr = configuration.getString("connector.type", "");
        if (connTypeStr.equals("")) {
            throw new Exception("Connector type not specified");
        }
        
        for (ConnectorConfigMapping configMapping : ConnectorConfigMapping.values()) {
            if (configMapping.getConfigString().equals(connTypeStr)) {
                return configMapping.getConnectorType();
            }
        }
        throw new Exception("Unrecognized connector type");
    }
    
    private DPA_Connector getSimpleResponseWaitingConnector(
            ProtocolLayer protocolLayer, Configuration configuration
    ) {
        DPA_Connector connector = new DPA_Connector(protocolLayer);
        new SimpleResponseWaitingConnectorConfigurator().configure(connector, configuration);
        return connector;
    }
    
    
    @Override
    public DPA_Connector getConnector(ProtocolLayer protocolLayer, Configuration configuration) 
            throws Exception 
    {
        ConnectorType connectorType = getConnectorType(configuration);
        switch ( connectorType ) {
            case RESPONSE_WAITING: 
                return getSimpleResponseWaitingConnector(protocolLayer, configuration);
        }
        throw new Exception("Unsupported connector type: " + connectorType);
    }
}
