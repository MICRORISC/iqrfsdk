
package com.microrisc.simply;

import com.microrisc.simply.config.AbstractConfigurator;
import org.apache.commons.configuration.Configuration;

/**
 * Simple configurator for Device Objects implementing Standard Services.
 * 
 * @author Michal Konopa
 */
public final class SimpleStandardServicesDeviceObjectConfigurator 
extends AbstractConfigurator<StandardServicesDeviceObject, Configuration>{

    @Override
    public void configure(StandardServicesDeviceObject devObject, Configuration configuration) {
        long defaultWaitingTimeout = configuration.getLong(
                "deviceObject.defaultWaitingTimeout", -1
        );
        if ( defaultWaitingTimeout != -1 ) {
            devObject.setDefaultWaitingTimeout(defaultWaitingTimeout);
        }
    }
    
}
