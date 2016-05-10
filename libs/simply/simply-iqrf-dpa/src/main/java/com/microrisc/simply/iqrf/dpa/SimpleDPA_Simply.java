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

package com.microrisc.simply.iqrf.dpa;

import com.microrisc.simply.BaseSimply;
import com.microrisc.simply.ConnectionStack;
import com.microrisc.simply.Network;
import com.microrisc.simply.asynchrony.AsynchronousMessagesGenerator;
import com.microrisc.simply.asynchrony.AsynchronousMessagesGeneratorListener;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import com.microrisc.simply.services.Service;
import java.util.HashMap;
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
    
    /** Map of services. */
    private final Map<Class, Service> servicesMap;
    
    
    /**
     * Creates new DPA Simply object.
     * @param connStack connection stack
     * @param networksMap map of networks IDs to networks themselves
     * @param broadcastServices broadcast services implementation object to use
     * @param asyncManager asynchronous messaging manager
     * @param servicesMap map of services
     */
    public SimpleDPA_Simply(
            ConnectionStack connStack, 
            Map<String, Network> networksMap,
            BroadcastServices broadcastServices, 
            AsynchronousMessagingManager<
                    DPA_AsynchronousMessage, 
                    DPA_AsynchronousMessageProperties
            > asyncManager,
            Map<Class, Service> servicesMap
    ) {
        super(connStack, networksMap);
        this.broadcastServices = broadcastServices;
        this.asyncManager = asyncManager;
        ((AsynchronousMessagesGenerator)connStack.getConnector()).registerListener(
                (AsynchronousMessagesGeneratorListener) asyncManager
        );
        this.servicesMap = new HashMap<>(servicesMap);
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
    public <T> T getService(Class<T> service) {
        if ( servicesMap.containsKey(service)) {
            return (T)servicesMap.get(service);
        }
        return null;
    }       
    
    @Override
    public Map<Class, Service> getServicesMap() {
        return new HashMap<>(servicesMap);
    }
    
    @Override
    public void destroy() {
        logger.debug("destroy - start: ");
        
        ((AsynchronousMessagesGenerator)connStack.getConnector()).unregisterListener(
                (AsynchronousMessagesGeneratorListener) asyncManager
        );
        asyncManager = null;
        
        super.destroy();
        broadcastServices = null;
        
        servicesMap.clear();
        
        logger.info("Destroy complete");
        logger.debug("destroy - end ");
        
        // think about stopping logback context
    }

}
