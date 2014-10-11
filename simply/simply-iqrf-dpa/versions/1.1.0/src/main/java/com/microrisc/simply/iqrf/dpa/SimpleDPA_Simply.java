

package com.microrisc.simply.iqrf.dpa;

import com.microrisc.simply.BaseSimply;
import com.microrisc.simply.ConnectionStack;
import com.microrisc.simply.Network;
import com.microrisc.simply.asynchrony.AsynchronousMessagesGenerator;
import com.microrisc.simply.asynchrony.AsynchronousMessagesGeneratorListener;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of {@code DPA_Simply} interface.
 * @author Michal Konopa
 */
public final class SimpleDPA_Simply 
extends BaseSimply implements DPA_Simply {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SimpleDPA_Simply.class);
    
    /** Broadcast services object. */
    private BroadcastServices broadcastServices = null;
    
    /** Asynchronous messaging manager. */
    private AsynchronousMessagingManager<
                DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties
            > asyncManager = null;
    
    
    /**
     * Creates new DPA Simply object.
     * @param connStack connection stack
     * @param networksMap map of networks IDs to networks themselves
     * @param broadcastServices broadcast services implementation object to use
     * @param asyncManager asynchronous messaging manager
     */
    public SimpleDPA_Simply(
            ConnectionStack connStack, 
            Map<String, Network> networksMap,
            BroadcastServices broadcastServices, 
            AsynchronousMessagingManager<
                    DPA_AsynchronousMessage, 
                    DPA_AsynchronousMessageProperties
            > asyncManager
    ) {
        super(connStack, networksMap);
        this.broadcastServices = broadcastServices;
        this.asyncManager = asyncManager;
        ((AsynchronousMessagesGenerator)connStack.getConnector()).registerListener(
                (AsynchronousMessagesGeneratorListener) asyncManager
        );
    }
    
    @Override
    public BroadcastServices getBroadcastServices() {
        return broadcastServices;
    }
    
    @Override
    public AsynchronousMessagingManager<
                DPA_AsynchronousMessage, 
                DPA_AsynchronousMessageProperties
           > getAsynchronousMessagingManager() 
    {
        return asyncManager;
    }
    
    @Override
    public void destroy() {
        logger.debug("destroy - start: ");
        
        super.destroy();
        broadcastServices = null;
        
        ((AsynchronousMessagesGenerator)connStack.getConnector()).unregisterListener(
                (AsynchronousMessagesGeneratorListener) asyncManager
        );
        asyncManager = null;
        
        logger.info("Destroy complete");
        logger.debug("destroy - end ");
    }

}
