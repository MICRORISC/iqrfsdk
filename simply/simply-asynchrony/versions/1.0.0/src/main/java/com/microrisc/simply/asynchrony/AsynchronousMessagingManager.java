

package com.microrisc.simply.asynchrony;


/**
 * Entry point for access functionality of asynchronous messaging from user's code.
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
