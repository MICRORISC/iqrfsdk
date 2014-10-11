
package com.microrisc.simply.init;

import com.microrisc.simply.ConnectionStack;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * Simple implementation class of Simply initialization objects.
 * 
 * @author Michal Konopa
 */
public final class SimpleInitObjects 
implements InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> {
    /** Connection stack. */
    private final ConnectionStack connectionStack;
    
    /** Implementation classes mapper. */
    private final ImplClassesMapper implClassMapper;
    
    /** Configuration settings. */
    private final InitConfigSettings<Configuration, Map<String, Configuration>> configSettings;
    
    
    /**
     * Creates new {@code SimpleInitObjects} object.
     * @param connectionStack connection stack to include in the object
     * @param implClassMapper implementation classes mapper
     * @param configSettings configuration settings needed for initialization process
     */
    public SimpleInitObjects(
            ConnectionStack connectionStack, 
            ImplClassesMapper implClassMapper,
            InitConfigSettings<Configuration, Map<String, Configuration>> configSettings 
    ) {
        this.connectionStack = connectionStack;
        this.implClassMapper = implClassMapper;
        this.configSettings = configSettings;
    }

    /**
     * @return connection stack
     */
    @Override
    public ConnectionStack getConnectionStack() {
        return connectionStack;
    }

    /**
     * @return implementation classes mapper
     */
    @Override
    public ImplClassesMapper getImplClassMapper() {
        return implClassMapper;
    }

    /**
     * @return configuration needed for initialization process
     */
    @Override
    public InitConfigSettings<Configuration, Map<String, Configuration>> getConfigSettings() {
        return configSettings;
    }
    
}
