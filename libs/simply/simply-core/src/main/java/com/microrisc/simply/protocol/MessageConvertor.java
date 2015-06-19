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

package com.microrisc.simply.protocol;

import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.NetworkData;

/**
 * Interface for conversion between Protocol Layer and Network Layer messages.
 * 
 * @author Michal Konopa
 */
public interface MessageConvertor {
    /**
     * Converts specified call request to used application protocol format and
     * returns it.
     * @param callRequest call request to convert
     * @return request converted to message of used protocol
     * @throws SimplyException if an error has occured during conversion
     */
    short[] convertToProtoFormat(CallRequest callRequest) throws SimplyException;
    
    /**
     * Converts specified message of used protocol to Device Object format and
     * returns it.
     * @param networkData network data containing protocol message to convert
     * @return converted form of specified protocol message data
     * @throws SimplyException if an error has occured during conversion
     */
    AbstractMessage convertToDOFormat(NetworkData networkData) throws SimplyException;
}
