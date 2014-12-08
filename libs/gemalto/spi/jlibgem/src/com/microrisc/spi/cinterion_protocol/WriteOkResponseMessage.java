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
 * 'Write OK' response message.
 * 
 * @author Michal Konopa
 */
public final class WriteOkResponseMessage extends AbstractResponseMessage {
    /** Length of message ( in bytes ).  */
    public static final int LENGTH = 4;
    
    /**
     * Creates new 'Write OK' response message with specified ID.
     * @param id ID of this message
     */
    public WriteOkResponseMessage(char id) {
        super(id);
    }
}
