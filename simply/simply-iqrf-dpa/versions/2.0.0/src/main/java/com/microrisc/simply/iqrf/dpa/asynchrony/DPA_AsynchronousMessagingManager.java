

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
        simpleManager = new SimpleAsynchronousMessagingManager<
                        DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties
                    >(checker);
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
