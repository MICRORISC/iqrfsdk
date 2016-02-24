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

/**
 * Provides services for communcation between connector and network layer. 
 * 
 * @author Michal Konopa
 */
public interface ProtocolLayerService {
    /**
     * Registers specified protocol listener, to which will be the messages sent
     * from the layer.
     * @param listener listener to register
     */
    public void registerListener(ProtocoLayerListener listener);
    
    /**
     * Unregisters currently registered protocol listener, to which are the 
     * messages sent from the layer.
     */
    public void unregisterListener();
    
    /**
     * Sends specified call request to protocol layer.
     * @param request call request to send 
     * @throws SimplyException if an error has occured during request sending
     */
    public void sendRequest(CallRequest request) throws SimplyException;
    
    /**
     * Sends specified call request to protocol layer.
     * @param request call request to send
     * @param procTime timeout to wait for response 
     * @throws SimplyException if an error has occured during request sending
     */
    public void sendRequest(CallRequest request, long procTime) throws SimplyException;
}
