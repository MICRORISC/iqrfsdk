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

package com.microrisc.simply.iqrf.dpa.asynchrony;

import com.microrisc.simply.AbstractMessage;

/**
 * Simple implementation of 
 * {@code DPA_AsynchronousMessage.DPA_AsynchronousMessageSource} interface.
 * 
 * @author Michal Konopa
 */
public final class SimpleDPA_AsynchronousMessageSource 
implements DPA_AsynchronousMessage.DPA_AsynchronousMessageSource 
{
    /** Reference to object, which implements Message Source. */
    private final AbstractMessage.MessageSource source;

    /** Peripheral number. */
    private final int peripheralNum;

    
    /**
     * Creates new object of DPA asynchronous message source.
     * @param source "basic" asynchronous message source
     * @param peripheralNum number of source peripheral
     */
    public SimpleDPA_AsynchronousMessageSource(
            AbstractMessage.MessageSource source, int peripheralNum
    ) {
        this.source = source;
        this.peripheralNum = peripheralNum;
    }
    
    /**
     * @return network ID
     */
    @Override
    public String getNetworkId() {
        return source.getNetworkId();
    }

    /**
     * @return node ID
     */
    @Override
    public String getNodeId() {
        return source.getNodeId();
    }

    /**
     * @return peripheral ID
     */
    @Override
    public int getPeripheralNumber() {
        return peripheralNum;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                source.toString() + 
                "peripheral number=" + peripheralNum + 
                " }");
    }
    
}
