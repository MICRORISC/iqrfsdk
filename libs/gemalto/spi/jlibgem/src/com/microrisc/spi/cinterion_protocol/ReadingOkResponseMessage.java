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
 * 'Reading X bytes OK' response message.
 * 
 * @author Michal Konopa
 */
public final class ReadingOkResponseMessage extends AbstractResponseMessage {
    /** Data, which was read. */
    private final short[] readData;
    
    /**
     * Creates new 'Reading X bytes OK' response message with specified ID.
     * @param id ID of this message
     * @param readData data, which was read
     */
    public ReadingOkResponseMessage(char id, short[] readData) {
        super(id);
        this.readData = readData;
    }

    /**
     * @return data, which was read
     */
    public short[] getReadData() {
        return readData;
    }
}
