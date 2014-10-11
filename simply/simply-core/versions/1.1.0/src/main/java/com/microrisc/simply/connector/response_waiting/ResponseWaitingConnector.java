
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
