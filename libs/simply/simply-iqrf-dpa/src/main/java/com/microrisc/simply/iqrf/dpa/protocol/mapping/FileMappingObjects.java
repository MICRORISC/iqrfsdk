/*
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.protocol.mapping;

import com.microrisc.simply.protocol.mapping.ProtocolMapping;
import java.util.Map;

/**
 * Encapsulates together {@link ProtocolMapping} and map of peripheral numbers
 * to interface.
 * <p>
 * @author Martin Strouhal
 */
public final class FileMappingObjects {

    private final ProtocolMapping protocolMapping;
    private final Map<Integer, Class> peripheralToIface;

    /**
     * Creates new objects encapsulating specified {@link ProtocolMapping} and
     * map of peripherals numbers to interfaces.
     * <p>
     * @param protocolMapping mapping of protocol
     * @param peripheralToIface map of peripherals to interfaces
     */
    public FileMappingObjects(ProtocolMapping protocolMapping, Map<Integer, Class> peripheralToIface) {
        this.protocolMapping = protocolMapping;
        this.peripheralToIface = peripheralToIface;
    }

    /**
     * Retruns saved protocol mapping.
     * @return instance of {@link ProtocolMapping}
     */
    public ProtocolMapping getProtocolMapping() {
        return protocolMapping;
    }

    /**
     * Returns saved map.
     * @return map of peripherals to interfaces
     */
    public Map<Integer, Class> getPeripheralToIface() {
        return peripheralToIface;
    }
}