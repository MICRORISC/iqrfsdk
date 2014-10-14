
package com.microrisc.simply.iqrf.dpa;

import com.microrisc.simply.ConnectionStack;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.HashMapCallRequestProcessingInfoContainer;
import com.microrisc.simply.HashMapResultsContainer;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.Network;
import com.microrisc.simply.asynchrony.AsynchronousMessagesGenerator;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.config.ConfigurationReader;
import com.microrisc.simply.connector.Connector;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastServicesDefaultImpl;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastingConnectorService;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import com.microrisc.simply.iqrf.dpa.init.SimpleDPA_InitObjects;
import com.microrisc.simply.iqrf.dpa.init.DPA_InitObjectsFactory;
import com.microrisc.simply.iqrf.dpa.init.DPA_Initializer;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * Factory for creating DPA Simply.
 * <p>
 * Uses {@code DPA_SimplyInnerObjectsFactory} and returns {@code SimpleDPA_Simply}
 * implementation of Simply.
 * 
 * @author Michal Konopa
 */
public final class DPA_SimplyFactory {
    /** Simply singleton. */
    private static DPA_Simply dpaSimply = null;
    
    
    /**
     * Creates and returns broadcast services implementation object.
     * @param configuration source configuration
     * @param connectorService connector to use
     * @return DPA broadcaster object
     * @throws SimplyException if specified connector doesn't support DPA Broadcasting 
     */
    private static BroadcastServices createBroadcastServices(
            Configuration configuration, ConnectorService connectorService
    ) throws SimplyException {
        if ( !(connectorService instanceof BroadcastingConnectorService) ) {
            throw new SimplyException("Connector doesn't support broadcasting.");
        }
        
        int capacity = configuration.getInt(
                "dpa.broadcasting.resultsContainer.capacity", 
                HashMapResultsContainer.DEFAULT_CAPACITY
        );
        
        long maxTimeDuration = configuration.getLong(
                "dpa.broadcasting.resultsContainer.maxTimeDuration",
                HashMapResultsContainer.DEFAULT_MAX_TIME_DURATION
        );
        
        return new BroadcastServicesDefaultImpl(
            (BroadcastingConnectorService) connectorService, 
            new HashMapCallRequestProcessingInfoContainer(capacity, maxTimeDuration)
        );
    }
    
    /**
     * Creates new asynchronous messaging manager and returns it.
     * @param connector connector to use
     * @return asynchronous messaging manager
     * @throws SimplyException if specified connector doesn't support asynchronous
     *         messages generator
     */
    private static AsynchronousMessagingManager<
                        DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties
                   > createAsynchronousMessagingManager
    (Connector connector) throws SimplyException {
        if ( !(connector instanceof AsynchronousMessagesGenerator) ) {
            throw new SimplyException("Connector doesn't support asynchronous messages generator.");
        }
        return new DPA_AsynchronousMessagingManager();
    }
    
    /**
     * Returns instance of Simply.
     * @param configFile configuration file with setting for creating Simply
     * @return DPA Simply
     * @throws com.microrisc.simply.SimplyException if an error has occured 
     *         during creating of DPA Simply object
     */
    public static DPA_Simply getSimply(String configFile) throws SimplyException {
        if ( dpaSimply != null ) {
            return dpaSimply;
        }
        
        ConnectionStack connStack = null;
        Map<String, Network> networkMap = null;
        BroadcastServices broadcastServices = null;
        AsynchronousMessagingManager<
                DPA_AsynchronousMessage, 
                DPA_AsynchronousMessageProperties
        > asyncManager = null;
        
        try {
            Configuration configuration = ConfigurationReader.fromFile(configFile);
            SimpleDPA_InitObjects initObjects = 
                    (new DPA_InitObjectsFactory()).getInitObjects(configuration);
            networkMap = new DPA_Initializer().initialize(initObjects);
            connStack = initObjects.getConnectionStack();
            broadcastServices = createBroadcastServices(configuration, connStack.getConnector());
            asyncManager = createAsynchronousMessagingManager(connStack.getConnector());
        } catch ( Exception e ) {
            throw new SimplyException(e);
        }

        dpaSimply = new SimpleDPA_Simply(connStack, networkMap, broadcastServices, asyncManager);
        return dpaSimply;
    }
}
