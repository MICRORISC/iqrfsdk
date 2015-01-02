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

package com.microrisc.cdc;

/**
 * Interface for receiving asynchronous messages from connected USB device. When 
 * asynchronous message comes, <code>onGetMessage</code> will be called. 
 * Data of the message will be passed in <code>data</code> parameter.
 * 
 * @version     1.0
 */
public interface J_AsyncMsgListener {
    /**
     * Will be called if asynchronous message comes. <br> 
     * If any error in the asynchronous message is discovered, <code>data</code> 
     * will be set to <code>null</code>.
     * @param data data of asynchronous message.
     */
    public void onGetMessage(short[] data);
    
}
