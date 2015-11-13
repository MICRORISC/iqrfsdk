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

import com.microrisc.simply.typeconvertors.AbstractConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates all parameters of argument used in mapping DPA peripherals.
 * <p>
 * @author Martin Strouhal
 */
public final class FileMappingArgument {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(FileMappingArgument.class);

    private final short order, length;
    private final AbstractConvertor convertor;

    /**
     * Creates onjects with specified parameters.
     * <p>
     * @param order of data in DPA command
     * @param length of data in DPA command
     * @param convertor which will be used for converting data specifed by order
     * and length
     */
    public FileMappingArgument(short order, short length, AbstractConvertor convertor) {
        this.order = order;
        this.length = length;
        this.convertor = convertor;
    }

    /**
     * Returns order data in DPA command for this argument.
     * @return order of data
     */
    public short getOrder() {
        return order;
    }

    /**
     * Returns data length in DPA command for this argument.
     * @return data length
     */
    public short getLength() {
        return length;
    }

    /**
     * Return convertor which will be used for converting specified data
     * @return convertor
     */
    public AbstractConvertor getConvertor() {
        return convertor;
    }
}
