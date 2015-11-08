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

import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between {@code RemotelyBondedModuleId} 
 * objects and protocol packets.
 * 
 * @author Michal Konopa
 */
public final class RemotelyBondedModuleIdConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(RemotelyBondedModuleIdConvertor.class);
    
    private RemotelyBondedModuleIdConvertor() {}
    
    /** Singleton. */
    private static final RemotelyBondedModuleIdConvertor instance = 
            new RemotelyBondedModuleIdConvertor();
    
    
    /**
     * @return {@code RemotelyBondedModuleIdConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public RemotelyBondedModuleIdConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 6;
    
    // postitions of fields
    static private final int MODULE_ID_POS = 0;
    static private final int MODULE_ID_LENGTH = 4;
    static private final int USER_DATA_POS = 4;
    static private final int USER_DATA_LENGTH = 2;
    
    
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        short[] moduleId = new short[MODULE_ID_LENGTH];
        System.arraycopy(protoValue, MODULE_ID_POS, moduleId, 0, MODULE_ID_LENGTH);
        
        short[] userData = new short[USER_DATA_LENGTH];
        System.arraycopy(protoValue, USER_DATA_POS, userData, 0, USER_DATA_LENGTH);
        
        RemotelyBondedModuleId remoteBondedModuleId = new RemotelyBondedModuleId(
                moduleId, userData
        );
        
        logger.debug("toObject - end: {}", remoteBondedModuleId);
        return remoteBondedModuleId;
    }
}
