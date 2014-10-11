

package com.microrisc.simply.asynchrony;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple implementation of {@code AsynchronousMessagingManager} interface.
 * 
 * @param <T> type of asynchronous message
 * @param <V> type of required properties of asynchronous messages
 * 
 * @author Michal Konopa
 */
public final class SimpleAsynchronousMessagingManager
<T extends BaseAsynchronousMessage, V extends AsynchronousMessageProperties>
extends AbstractAsynchronousMessagingManager<T, V> 
{   
    /**
     * Connects listener with its required messages properties.
     */
    private class ListenerAndProps {
        AsynchronousMessagesListener<T> listener;
        V props;
        
        public ListenerAndProps(
                AsynchronousMessagesListener<T> listener, V props
        ) {
            this.listener = listener;
            this.props = props;
        } 
    }
    
    /** 
     * Registered asynchronous listeners, 
     * together with their required message properties. 
     */
    private final List<ListenerAndProps> regListenersAndProps;
    
    /** Synchronization for registered listeners. */
    private final Object regListenersAndPropsSynchro = new Object();
    
    
    /**
     * Creates new simple asynchronous messaging manager, which will be using 
     * specified asynchronous messages checker.
     * @param propChecker asynchronous messages checker to use
     */
    public SimpleAsynchronousMessagingManager(
            AsynchronousMessagePropertiesChecker<T, V> propChecker
    ) {
        super(propChecker);
        this.regListenersAndProps = new LinkedList<ListenerAndProps>();
    }
    
    @Override
    public void registerAsyncMsgListener(AsynchronousMessagesListener<T> listener) {
        synchronized ( regListenersAndPropsSynchro ) {
            for ( ListenerAndProps regListenerProp : regListenersAndProps ) {
                if ( listener == regListenerProp.listener ) {
                    return;
                }
            }
            regListenersAndProps.add( new ListenerAndProps(listener, null) );
        }
    }

    @Override
    public void registerAsyncMsgListener(AsynchronousMessagesListener<T> listener, V msgProps) {
        synchronized ( regListenersAndPropsSynchro ) {
            for ( ListenerAndProps regListenerProp : regListenersAndProps ) {
                if ( listener == regListenerProp.listener ) {
                    return;
                }
            }
            regListenersAndProps.add( new ListenerAndProps(listener, msgProps) );
        }
    }
   
    @Override
    public void unregisterAsyncMsgListener(AsynchronousMessagesListener<T> listener) {
        synchronized ( regListenersAndPropsSynchro ) {
            Iterator<ListenerAndProps> listenerAndPropsIter = regListenersAndProps.iterator();
            while ( listenerAndPropsIter.hasNext() ) {
                if ( listener == listenerAndPropsIter.next().listener ) {
                    listenerAndPropsIter.remove();
                    return;
                }
            }
        }
    }
    
    @Override
    public void onAsynchronousMessage(T message) {
        synchronized ( regListenersAndPropsSynchro ) {
            for ( ListenerAndProps regListenerAndProps : regListenersAndProps ) {
                if ( propChecker.messageHasRequiredProperties(message, regListenerAndProps.props ) ) {
                    regListenerAndProps.listener.onAsynchronousMessage(message);
                }
            }
        }
    }

}
