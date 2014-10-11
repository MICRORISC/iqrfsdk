
package com.microrisc.simply.init;

import com.microrisc.simply.Network;
import java.util.Map;

/**
 * Base class of Simply initializers.
 * 
 * @author Michal Konopa
 * @param <T> type of initialiazation objects
 * @param <U> type of initialized networks
 */
public abstract class AbstractInitializer
<T extends InitObjects, U extends Network> {
    /**
     * Creates and returns map of initialized networks to use in Simply. 
     * @param initObjects objects needed in the initialization's process of Simply
     * @return map of networks
     * @throws Exception if some error has occured during initialization process
     */
    public abstract Map<String, U> initialize(T initObjects) throws Exception;
}
