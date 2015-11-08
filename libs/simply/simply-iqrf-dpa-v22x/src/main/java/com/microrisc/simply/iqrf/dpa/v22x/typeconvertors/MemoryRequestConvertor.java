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
package com.microrisc.simply.iqrf.dpa.v22x.typeconvertors;

import com.microrisc.simply.Node;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Command;
import com.microrisc.simply.iqrf.dpa.v22x.types.MemoryRequest;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between Java {@code MemoryRequest} type
 * and IQRF DPA protocol bytes.
 *
 * @author Martin Strouhal
 */
public final class MemoryRequestConvertor extends AbstractConvertor {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(MemoryRequestConvertor.class);

    private MemoryRequestConvertor() {
    }

    /**
     * Singleton.
     */
    private static final MemoryRequestConvertor instance = new MemoryRequestConvertor();

    /**
     * @return {@code MemoryRequestConvertor} instance
     */
    @ConvertorFactoryMethod
    static public MemoryRequestConvertor getInstance() {
        return instance;
    }    

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);

        if (!(value instanceof MemoryRequest)) {
            throw new ValueConversionException("Value to convert is not of MemoryRequest type.");
        }

        MemoryRequest request = (MemoryRequest) value;
        short[] protoValue = new short[5 + request.getLength()];

        int address = request.getMemoryAddress();
        protoValue[1] = (short) (address & 0xFF);
        address >>= 2;
        protoValue[0] = (short) (address & 0xFF);
        protoValue[2] = (short) request.getPnum();
        protoValue[3] = (short) request.getPcmd();
        protoValue[4] = (short) request.getLength();

        System.arraycopy(request.getData(), 0, protoValue, 5, request.getLength());

        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported.");
    }
}
