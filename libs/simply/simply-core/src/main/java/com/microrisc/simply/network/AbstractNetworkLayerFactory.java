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

package com.microrisc.simply.network;

/**
 * Base class for network layer factories.
 * 
 * @author Michal Konopa
 * @param <T> type of configuration to use
 * @param <U> type of network layer implementation to return
 */
public abstract class AbstractNetworkLayerFactory<T extends Object, U extends NetworkLayer> {
    /**
     * Returns network layer implementation.
     * @param connectionStorage network connection storage
     * @param configuration configuration to use
     * @return network layer implementation
     * @throws Exception if an error has occured
     */
    public abstract U getNetworkLayer(
            NetworkConnectionStorage connectionStorage, 
            T configuration
    ) throws Exception;
}
