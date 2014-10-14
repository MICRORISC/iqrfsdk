

package com.microrisc.simply.di_services;

import java.util.UUID;

/**
 * Generic interface to call DI methods in asynchronous manner. 
 * 
 * @author Michal Konopa
 */
public interface GenericAsyncCallable {
    /**
     * Calls specified method with specified arguments and returns unique
     * identifier of that call.
     * @param methodId ID of a called method
     * @param args arguments of the method
     * @return unique identifier of the method call <br>
     *         {@code null} if an error has occured during this call processing
     */
    UUID call(Object methodId, Object[] args);
}
