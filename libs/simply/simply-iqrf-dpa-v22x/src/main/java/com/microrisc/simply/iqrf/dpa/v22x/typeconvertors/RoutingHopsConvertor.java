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

import com.microrisc.simply.iqrf.dpa.v22x.types.RoutingHops;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion between array of bytes and 
 * {@code RoutingHops} objects.
 * 
 * @author Michal Konopa
 */
public final class RoutingHopsConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(RoutingHopsConvertor.class);
    
    private RoutingHopsConvertor() {}
    
    /** Singleton. */
    private static final RoutingHopsConvertor instance = new RoutingHopsConvertor();
    
    
    /**
     * @return {@code RoutingHopsConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public RoutingHopsConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 2;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    // postitions of fields
    static private final int REQUEST_HOPS_POS = 0;
    static private final int RESPONSE_HOPS_POS = 1;
    
    
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);
        
        if ( !(value instanceof RoutingHops) ) {
            throw new ValueConversionException("Value to convert has not proper type.");
        }
        
        short[] protoValue = new short[TYPE_SIZE];
        RoutingHops routingHops = (RoutingHops)value;
        protoValue[REQUEST_HOPS_POS] = (short)routingHops.getRequestHops();
        protoValue[RESPONSE_HOPS_POS] = (short)routingHops.getResponseHops();
               
        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        int requestHops = protoValue[REQUEST_HOPS_POS];
        int responseHops = protoValue[RESPONSE_HOPS_POS];
        RoutingHops routingHops = new RoutingHops(requestHops, responseHops);
        
        logger.debug("toObject - end: {}", routingHops);
        return routingHops;
    }
    
}
