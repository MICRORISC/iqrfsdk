
package com.microrisc.simply.init;

import com.microrisc.simply.ConnectionStack;

/**
 * Provides access to objects, which are needed in the process of initialization 
 * of Simply.
 * 
 * @author Michal Konopa
 * @param <T> type of configuration settings
 */
public interface InitObjects<T extends Object> {

    /**
     * Connection stack.
     * @return connection stack
     */
    ConnectionStack getConnectionStack();

    /**
     * Implementation classes mapper.
     * @return implementation classes mapper
     */
    ImplClassesMapper getImplClassMapper();
    
    /**
     * Configuration settings.
     * @return configuration settings.
     */
    T getConfigSettings();
}
