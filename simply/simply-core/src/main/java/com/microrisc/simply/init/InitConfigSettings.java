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
