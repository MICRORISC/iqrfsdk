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

package com.microrisc.spi.cinterion_protocol;

/**
 * 'Protocol error in xth byte' response message.
 * 
 * @author Michal Konopa
 */
public final class ProtocolErrorResponseMessage extends AbstractResponseMessage {
    
    /** Faulty byte. */
    private final int faultyByte;
    
    /**
     * Creates new 'Protocol error in xth byte' response message with specified ID.
     * @param id ID of this message
     * @param faultyByte faulty byte
     */
    public ProtocolErrorResponseMessage(char id, int faultyByte) {
        super(id);
        this.faultyByte = faultyByte;
    }

    /**
     * @return the faulty byte
     */
    public int getFaultyByte() {
        return faultyByte;
    }
    
}
