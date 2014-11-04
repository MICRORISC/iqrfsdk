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

import com.microrisc.simply.network.NetworkLayerException;

/**
 * Services for low level access to the underlaying network.
 * 
 * @author Michal Konopa
 */
public interface NetworkLayerService {
    /**
     * Registers specified network listener, to which sent the data from 
     * network layer.
     * @param listener listener to register
     */
    void registerListener(NetworkLayerListener listener);
    
    /**
     * Unregisters currently registered network listener, which the data are 
     * sent to from the network.
     */
    void unregisterListener();
    
    /**
     * Sends specified network data to the network.
     * @param data data to send
     * @throws NetworkLayerException if an error has occured during sending
     *         the data
     */
    void sendData(NetworkData data) throws NetworkLayerException;
}
