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

package com.microrisc.simply.asynchrony;


/**
 * Entry point for access the functionality of asynchronous messaging from user's code.
 * 
 * @param <T> type of asynchronous message
 * @param <V> type of required properties of asynchronous messages
 * 
 * @author Michal Konopa
 */
public interface AsynchronousMessagingManager
<T extends BaseAsynchronousMessage, V extends AsynchronousMessageProperties> 
{
    /**
     * Registers specified listener for getting asynchronous messages.
     * @param listener listener to register 
     */
    void registerAsyncMsgListener(AsynchronousMessagesListener<T> listener);
    
    /**
     * Registers specified listener for getting asynchronous messages. Only
     * messages with specified properties will be delivered to the listener.
     * @param listener listener to register 
     * @param msgProps properties of asynchronous messages to get
     */
    void registerAsyncMsgListener(AsynchronousMessagesListener<T> listener, V msgProps);
    
    /**
     * Unregisters specified listener from this getting asynchronous messages.
     * @param listener listener to unregister 
     */
    void unregisterAsyncMsgListener(AsynchronousMessagesListener<T> listener);
}
