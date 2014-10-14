
package com.microrisc.simply.connector;

import com.microrisc.simply.ProtocolLayerService;

/**
 * Base class for connectors factories.
 * 
 * @author Michal Konopa
 * @param <T> type of protocol layer service
 * @param <U> type of configuration
 * @param <V> type of connector to create
 */
public abstract class AbstractConnectorFactory
<T extends ProtocolLayerService, U extends Object, V extends Connector> {
    /**
     * Returns connector implementation.
     * @param protocolLayer protocol layer to use
     * @param configuration configuration of connector
     * @return network layer implementation
     * @throws Exception if an error has occured
     */
    public abstract V getConnector(
            T protocolLayer, 
            U configuration
    ) throws Exception;
}
