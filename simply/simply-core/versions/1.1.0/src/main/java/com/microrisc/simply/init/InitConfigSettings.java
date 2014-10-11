
package com.microrisc.simply.init;

/**
 * Initialization configuration settings.
 * 
 * @author Michal Konopa
 * 
 * @param <T> type of general settings
 * @param <U> type of settings related to each of networks
 */
public interface InitConfigSettings<T extends Object, U extends Object> {

    /**
     * @return settings not directly related to any of networks
     */
    T getGeneralSettings();

    /**
     * @return settings for each network
     */
    U getNetworksSettings();
    
}
