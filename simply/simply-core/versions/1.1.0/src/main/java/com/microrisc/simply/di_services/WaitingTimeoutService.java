
package com.microrisc.simply.di_services;

/**
 * Access to waiting timeout for DO method calls. 
 * 
 * @author Michal Konopa
 */
public interface WaitingTimeoutService {
    /**
     * Returns default timeout ( in ms ) to wait for result from DO method calls.
     * @return default timeout ( in ms ) to wait for result from DO method calls.
     */
    long getDefaultWaitingTimeout();

    /**
     * Sets default timeout ( in ms ) to wait for result from DO method calls. 
     * @param timeout default waiting timeout to set
     */
    void setDefaultWaitingTimeout(long timeout);
}
