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

package com.microrisc.simply.connector;

import com.microrisc.simply.ProtocoLayerListener;
import com.microrisc.simply.ProtocolLayerService;

/**
 * Base abstract class for all connector services. 
 * 
 * @author Michal Konopa
 */
public abstract class AbstractConnector
implements 
        Connector,
        ProtocoLayerListener 
{
    
    /** Reference to protocol layer service. */
    protected ProtocolLayerService protocolLayerService = null;
    
    /**
     * Checks specified protocol layer for validity.
     * @param protocolLayerService
     * @return {@code protocolLayerService}
     */
    private ProtocolLayerService checkProtocolLayer(ProtocolLayerService protocolLayerService) {
        if ( protocolLayerService == null ) {
            throw new IllegalArgumentException("Protocol layer service cannot be null");
        }
        return protocolLayerService;
    }
    
    /**
     * Sets protocol layer service communication object to the specified one.
     * @param protocolLayerService protocol layer service object to set
     * @throws IllegalArgumentException if {@code protocolLayerService} is {@code null}
     */
    protected AbstractConnector(ProtocolLayerService protocolLayerService) {
        this.protocolLayerService = checkProtocolLayer(protocolLayerService);
    }
}
