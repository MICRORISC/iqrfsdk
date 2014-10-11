
package com.microrisc.simply.config;

/**
 * Base class of configurators.
 * 
 * @author Michal Konopa
 * @param <T> type of object to configure
 * @param <U> type of configuration
 */
public abstract class AbstractConfigurator<T extends Object, U extends Object> {
    /**
     * Configures specified object using specified configuration.
     * @param object object to configure
     * @param configuration configuration to use
     */
    public abstract void configure(T object, U configuration);
}
