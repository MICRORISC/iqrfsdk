

package com.microrisc.simply.asynchrony;

/**
 * Base abstract class of asynchronous messaging managers.
 * 
 * @param <T> type of asynchronous message
 * @param <V> type of required properties of asynchronous messages
 * 
 * @author Michal Konopa
 */
public abstract class AbstractAsynchronousMessagingManager
<T extends BaseAsynchronousMessage, V extends AsynchronousMessageProperties>
implements AsynchronousMessagingManager<T, V>, AsynchronousMessagesGeneratorListener<T> 
{   
    /** Reference to used asynchronous messages properties checker. */
    protected AsynchronousMessagePropertiesChecker<T, V> propChecker = null;
    
    
    /**
     * Creates new asynchronous messaging manager, which will be using 
     * specified asynchronous messages checker.
     * @param propChecker asynchronous messages checker to use
     */
    protected AbstractAsynchronousMessagingManager(
            AsynchronousMessagePropertiesChecker<T, V> propChecker
    ) {
        this.propChecker = propChecker;
    }
}
