
package com.microrisc.simply.init;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * Initializer configuration settings implementation by using map.
 * 
 * @author Michal Konopa
 */
public final class MapInitConfigSettings 
implements InitConfigSettings<Configuration, Map<String, Configuration>> {
    /** Settings not directly related to any of networks. */
    private final Configuration generalSettings;
    
    /** Map of settings for each network. */
    private final Map<String, Configuration> networksSettings;
    
    
    /**
     * Creates new object of initialization configuration settings.
     * @param generalSettings settings not directly related to any of networks
     * @param networksSettings settings for each network
     */
    public MapInitConfigSettings(Configuration generalSettings, 
            Map<String, Configuration> networksSettings
    ) {
        this.generalSettings = generalSettings;
        this.networksSettings = new HashMap<String, Configuration>(networksSettings);
    }

    /**
     * @return settings not directly related to any of networks
     */
    @Override
    public Configuration getGeneralSettings() {
        return generalSettings;
    }

    /**
     * @return settings for each network
     */
    @Override
    public Map<String, Configuration> getNetworksSettings() {
        return networksSettings;
    }
    
}
