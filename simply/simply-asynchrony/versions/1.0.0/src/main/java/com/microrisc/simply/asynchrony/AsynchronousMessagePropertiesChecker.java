

package com.microrisc.simply.asynchrony;

/**
 * Checker of asynchronous message properties.
 * 
 * @param <T> type of asynchronous message
 * @param <V> type of required properties of asynchronous messages
 * 
 * @author Michal Konopa
 */
public interface AsynchronousMessagePropertiesChecker
<T extends BaseAsynchronousMessage, V extends AsynchronousMessageProperties>
{
    /**
     * Returns {@code true} if the specified message has specified properties.
     * Otherwise returns {@code false}.
     * @param message message to check for specified properties
     * @param reqProps required properties of the message
     * @return {@code true} if the specified message has specified properties <br>
     *         {@code false}, otherwise 
     */
    boolean messageHasRequiredProperties(T message, V reqProps);
}
