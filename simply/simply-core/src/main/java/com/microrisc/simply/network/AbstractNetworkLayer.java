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
 * Abstract base class for network layer.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractNetworkLayer implements NetworkLayer {
    /** Connection storage to use. */
    protected NetworkConnectionStorage connectionStorage;
    
    /**
     * Protected constructor. Sets used connection storage to the specified one. 
     * @param connectionStorage storage of information about connections to networks 
     */
    protected AbstractNetworkLayer(NetworkConnectionStorage connectionStorage) {
        this.connectionStorage = connectionStorage;
    }
}
