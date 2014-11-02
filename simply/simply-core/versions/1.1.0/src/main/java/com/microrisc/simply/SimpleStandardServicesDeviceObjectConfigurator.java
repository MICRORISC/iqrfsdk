/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
