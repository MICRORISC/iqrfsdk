
package com.microrisc.simply.connector.response_waiting;

/**
 * Provides access to settings for a response waiting connector.
 * 
 * @author Michal Konopa
 */
public interface ResponseWaitingSettings {
    /**
     * Returns pause between subsequent attempts to send request to underlaying
     * network.
     * @return Pause between subsequent attempts to send request to underlaying
     *         network [in miliseconds].
     */
    long getAttemptPause();
    
    /**
     * Returns minimal pause between sending requests.
     * @return Minimal pause between sending requests [in miliseconds].
     */
    long getBetweenSendPause();
    
    /**
     * Returns number of maximal attempts of sending request to underlaying
     * network.
     * @return number of maximal attempts of sending request to underlaying network.
     */
    int getMaxSendAttempts();
    
    /**
     * Returns timeout for waiting for a response from underlaying network.
     * @return timeout for waiting for a response from underlaying network
     *         [in miliseconds].
     */
    long getResponseTimeout();
}
