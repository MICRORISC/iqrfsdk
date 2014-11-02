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

package com.microrisc.simply.iqrf.dpa.asynchrony;

import com.microrisc.simply.asynchrony.AbstractAsynchronousMessagingManager;
import com.microrisc.simply.asynchrony.AsynchronousMessagePropertiesChecker;
import com.microrisc.simply.asynchrony.AsynchronousMessagesListener;
import com.microrisc.simply.asynchrony.SimpleAsynchronousMessagingManager;

/**
 * DPA implementation of {@code AsynchronousMessagingManager} interface.
 * 
 * @author Michal Konopa
 */
public final class DPA_AsynchronousMessagingManager
extends AbstractAsynchronousMessagingManager<
            DPA_AsynchronousMessage, 
            DPA_AsynchronousMessageProperties
        > 
{
    
    private final SimpleAsynchronousMessagingManager<
                DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties
            > simpleManager;
    
    /**
     * Creates new DPA asynchronous messaging manager with specified checker to
     * use.
     * @param checker checker to use
     */
    public DPA_AsynchronousMessagingManager(
            AsynchronousMessagePropertiesChecker<
                    DPA_AsynchronousMessage, 
                    DPA_AsynchronousMessageProperties
            > checker
    ) {
        super(checker);
        simpleManager = new SimpleAsynchronousMessagingManager<>(checker);
    }
    
    /**
     * Creates new DPA asynchronous messaging manager. The manager will be using
     * {@code DPA_AsynchronousMessagePropertiesChecker} checker.
     */
    public DPA_AsynchronousMessagingManager() {
        this( new DPA_AsynchronousMessagePropertiesChecker() );
    }
    
    @Override
    public void registerAsyncMsgListener(
            AsynchronousMessagesListener<DPA_AsynchronousMessage> listener
    ) {
        simpleManager.registerAsyncMsgListener(listener);
    }

    @Override
    public void registerAsyncMsgListener(
            AsynchronousMessagesListener<DPA_AsynchronousMessage> listener, 
            DPA_AsynchronousMessageProperties msgProps
    ) {
        simpleManager.registerAsyncMsgListener(listener, msgProps);
    }

    @Override
    public void unregisterAsyncMsgListener(
            AsynchronousMessagesListener<DPA_AsynchronousMessage> listener
    ) {
        simpleManager.unregisterAsyncMsgListener(listener);
    }

    @Override
    public void onAsynchronousMessage(DPA_AsynchronousMessage message) {
        simpleManager.onAsynchronousMessage(message);
    }

}
