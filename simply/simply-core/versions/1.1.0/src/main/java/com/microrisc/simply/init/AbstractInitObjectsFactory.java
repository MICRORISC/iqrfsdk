
package com.microrisc.simply.init;

/**
 * Base abstract class for Simply initialization objects factories.
 * 
 * @author Michal Konopa
 * @param <T> type of configuration
 * @param <U> type of initialization objects
 */
public abstract class AbstractInitObjectsFactory
<T extends Object, U extends InitObjects> {
    
    /**
     * Returns Simply initialization objects.
     * @param configuration configuration of initialization objects
     * @return Simply initialization objects
     * @throws Exception if an error has occured
     */
    public abstract U getInitObjects(T configuration) throws Exception;
}
