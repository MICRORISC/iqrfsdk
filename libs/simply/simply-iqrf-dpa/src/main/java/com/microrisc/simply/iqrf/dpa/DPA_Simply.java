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

import com.microrisc.simply.Simply;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import com.microrisc.simply.services.ServicesAccessor;


/**
 * Extended Simply interface for access to DPA functionality.
 * 
 * @author Michal Konopa
 */
public interface DPA_Simply extends Simply, ServicesAccessor {
    /**
     * Returns access to broadcast services.
     * @return access to broadcast services
     */
    BroadcastServices getBroadcastServices();
    
    /**
     * Returns manager of asynchronous messaging.
     * @return manager of asynchronous messaging
     */
    AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> 
        getAsynchronousMessagingManager();
}
