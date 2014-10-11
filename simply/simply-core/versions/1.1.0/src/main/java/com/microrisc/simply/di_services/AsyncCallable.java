package com.microrisc.simply.di_services;

import java.util.UUID;

/**
 * Access to device interface in an asynchronous manner. 
 * 
 * @author Michal Konopa
 */
public interface AsyncCallable extends WaitingTimeoutService {
    /**
     * Returns results of DO method call, which is identified by specified 
     * method call ID. Blocks, until results for specified method call is 
     * present or the specified timeout has elapsed.
     * @param <T> type of result
     * @param callId unique identifier of performed DO method call
     * @param resultClass class of result
     * @param timeout timeout ( in ms ) to wait for the results
     * @return results of DO method call identified by {@code callId}
     */
    <T> T getCallResult(UUID callId, Class<T> resultClass, long timeout);
    
    /**
     * Returns results of DO method call, which is identified by specified 
     * method call ID. Blocks, until results for specified method call is 
     * present, or the <b>default</b> waiting timeout has elapsed.
     * @param <T> type of result
     * @param callId unique identifier of performed DO method call
     * @param resultClass class of result
     * @return results of DO method call identified by {@code callId}
     */
    <T> T getCallResultInDefaultWaitingTimeout(UUID callId, Class<T> resultClass);
    
    /**
     * Returns results of DO method call, which is identified by specified 
     * method call ID. Returns result immediately, it doesn't wait for any timeout.
     * @param <T> type of result
     * @param callId unique identifier of performed DO method call
     * @param resultClass class of result
     * @return results of DO method call identified by {@code callId}
     */
    <T> T getCallResultImmediately(UUID callId, Class<T> resultClass);
}
 