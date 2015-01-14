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
     * @return result of DO method call identified by {@code callId}
     */
    <T> T getCallResultInDefaultWaitingTimeout(UUID callId, Class<T> resultClass);
    
    /**
     * Returns results of DO method call, which is identified by specified 
     * method call ID. Returns result immediately, it doesn't wait for any timeout.
     * @param <T> type of result
     * @param callId unique identifier of performed DO method call
     * @param resultClass class of result
     * @return result of DO method call identified by {@code callId}
     */
    <T> T getCallResultImmediately(UUID callId, Class<T> resultClass);
    
    /**
     * Returns results of DO method call, which is identified by specified 
     * method call ID. Blocks, until results for specified method call is 
     * present. Can block for a potentially infinite long time.
     * @param <T> type of result
     * @param callId unique identifier of performed DO method call
     * @param resultClass class of result
     * @return result of DO method call identified by {@code callId}
     */
    <T> T getCallResultInUnlimitedWaitingTimeout(UUID callId, Class<T> resultClass);
}
 