
package com.microrisc.simply.asynchrony;

/**
 * Listener for asynchronous messages comming from connected networks.
 * Intended for direct usage from user's application code.
 * 
 * @param <T> type of asynchronous message
 * 
 * @author Michal Konopa
 */
public interface AsynchronousMessagesListener<T extends BaseAsynchronousMessage> 
{
    /**
     * Will be called, when new asynchronous message has come from connected networks.
     * @param message arrived message
     */
    void onAsynchronousMessage(T message);
}
