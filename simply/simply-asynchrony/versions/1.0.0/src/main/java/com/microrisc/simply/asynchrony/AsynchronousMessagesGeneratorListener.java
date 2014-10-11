

package com.microrisc.simply.asynchrony;

/**
 * Listener of data comming from generator of asynchronous messages. 
 * 
 * @param <T> type of asynchronous messages
 * 
 * @author Michal Konopa
 */
public interface AsynchronousMessagesGeneratorListener
<T extends BaseAsynchronousMessage>
{
    /**
     * Will be called, when new asynchronous message has come from generator.
     * @param message arrived message
     */
    void onAsynchronousMessage(T message);
}
