
package com.microrisc.simply.iqrf.dpa.v210.init;

import com.microrisc.simply.ConnectionStack;
import com.microrisc.simply.init.ImplClassesMapper;
import com.microrisc.simply.init.InitConfigSettings;
import com.microrisc.simply.init.SimpleInitObjects;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * DPA initialization objects.
 * 
 * @author Michal Konopa
 */
public final class SimpleDPA_InitObjects 
implements DPA_InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> {
    /** Simple init objects. */
    private final SimpleInitObjects simpleInitObjects;
    
    /** Peripheral to device interface mapper. */
    private final PeripheralToDevIfaceMapper peripheralToDevIfaceMapper;
    
    
    /**
     * Creates new {@code DPA_InitObjects} object.
     * @param connectionStack connection stack to include in the object
     * @param implClassMapper implementation classes mapper
     * @param configSettings configuration settings needed for initialization process
     * @param peripheralToDevIfaceMapper mapping of DPA peripherals to device interfaces
     */
    public SimpleDPA_InitObjects(
            ConnectionStack connectionStack, 
            ImplClassesMapper implClassMapper,
            InitConfigSettings<Configuration, Map<String, Configuration>> configSettings,
            PeripheralToDevIfaceMapper peripheralToDevIfaceMapper
    ) {
        this.simpleInitObjects = new SimpleInitObjects(connectionStack, 
            implClassMapper, configSettings
        );
        this.peripheralToDevIfaceMapper = peripheralToDevIfaceMapper;
    }

    
    @Override
    public PeripheralToDevIfaceMapper getPeripheralToDevIfaceMapper() {
        return peripheralToDevIfaceMapper;
    }

    @Override
    public ConnectionStack getConnectionStack() {
        return this.simpleInitObjects.getConnectionStack();
    }

    @Override
    public ImplClassesMapper getImplClassMapper() {
        return this.simpleInitObjects.getImplClassMapper();
    }

    @Override
    public InitConfigSettings<Configuration, Map<String, Configuration>> getConfigSettings() {
        return this.simpleInitObjects.getConfigSettings();
    }
}
