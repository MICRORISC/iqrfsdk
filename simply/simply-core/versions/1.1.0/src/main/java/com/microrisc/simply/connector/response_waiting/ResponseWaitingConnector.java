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

package com.microrisc.simply.connector.response_waiting;

import com.microrisc.simply.connector.Connector;

/**
 * Connector, which waits for a response before sending next request.
 * 
 * @author Michal Konopa
 */
public interface ResponseWaitingConnector extends Connector {
    /**
     * Sets pause between subsequent attempts to send request to underlaying
     * network.
     * @param attemptPause pause between subsequent attempts to send request to
     *                underlaying network [in miliseconds].
     */
    void setAttemptPause(long attemptPause);
    
    /**
     * Returns pause between subsequent attempts to send request to underlaying
     * network.
     * @return Pause between subsequent attempts to send request to underlaying
     *         network [in miliseconds].
     */
    long getAttemptPause();

    
    /**
     * Sets minimal pause between sending requests.
     * @param betweenSendPause minimal pause between sending requests [in miliseconds].
     */
    void setBetweenSendPause(long betweenSendPause);
    
    /**
     * Returns minimal pause between sending requests.
     * @return Minimal pause between sending requests [in miliseconds].
     */
    long getBetweenSendPause();

    /**
     * Sets number of maximal attempts of sending request to underlaying network.
     * @param maxSendAttempts number of maximal attempts of sending request to
     *                        underlaying network.
     */
    void setMaxSendAttempts(int maxSendAttempts);
    
    /**
     * Returns number of maximal attempts of sending request to underlaying
     * network.
     * @return number of maximal attempts of sending request to underlaying network.
     */
    int getMaxSendAttempts();

    
    /**
     * Sets timeout for waiting for a response from underlaying network.
     * @param responseTimeout timeout for waiting for a response from underlaying
     *                        network [in miliseconds].
     */
    void setResponseTimeout(long responseTimeout);
    
    /**
     * Returns timeout for waiting for a response from underlaying network.
     * @return timeout for waiting for a response from underlaying network
     *         [in miliseconds].
     */
    long getResponseTimeout();

}
