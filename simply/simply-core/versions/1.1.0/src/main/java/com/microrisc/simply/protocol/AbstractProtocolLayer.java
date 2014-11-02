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

package com.microrisc.simply.protocol;

import com.microrisc.simply.SimplyException;
import com.microrisc.simply.NetworkLayerListener;
import com.microrisc.simply.NetworkLayerService;
import com.microrisc.simply.ProtocoLayerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for implementation protocol layer.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractProtocolLayer 
implements ProtocolLayer, NetworkLayerListener {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AbstractProtocolLayer.class);
    
    /** Network layer service. */
    protected NetworkLayerService networkLayerService = null;
    
    /** Message convertor. */
    protected MessageConvertor msgConvertor = null;
    
    /** Registered listener. */
    protected ProtocoLayerListener listener = null;
    
    
    /**
     * Protected constructor.
     * @param networkLayerService network layer service
     * @param msgConvertor message convertor
     */
    protected AbstractProtocolLayer(NetworkLayerService networkLayerService, 
            MessageConvertor msgConvertor
    ) {
        this.networkLayerService = networkLayerService;
        this.msgConvertor = msgConvertor;
    }
    
    @Override
    public void registerListener(ProtocoLayerListener listener) {
        this.listener = listener;
        logger.info("Listener registered");
    }
    
    @Override
    public void unregisterListener() {
        this.networkLayerService.unregisterListener();
        this.listener = null;
        logger.info("Listener unregistered");
    }
    
    @Override
    public void start() throws SimplyException {
        logger.debug("start - start:");
        
        this.networkLayerService.registerListener(this);
        
        logger.info("Protocol layer started");
        logger.debug("start - end");
    }
    
    @Override
    public void destroy() {
        logger.debug("destroy - start:");
        
        unregisterListener();
        networkLayerService = null;
        
        logger.info("Destroyed");
        logger.debug("destroy - end");
    }
}
