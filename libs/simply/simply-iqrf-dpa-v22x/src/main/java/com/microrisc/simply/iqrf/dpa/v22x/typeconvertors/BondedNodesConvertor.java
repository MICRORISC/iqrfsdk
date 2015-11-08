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

import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNodes;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for conversion from IQRF DPA bit array of bonded nodes 
 * to {@code Integer[]} values  
 * 
 * @author Michal Konopa
 */
public final class BondedNodesConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BondedNodesConvertor.class);
    
    
    private BondedNodesConvertor() {
    }
    
    /** Singleton. */
    private static final BondedNodesConvertor instance = new BondedNodesConvertor();
    
    
    /**
     * @return {@code BondedNodesConvertor} instance 
     */
    @ConvertorFactoryMethod
    static public BondedNodesConvertor getInstance() {
        return instance;
    }
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 32;
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Currently not supported");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        // maximal bonded node number
        final int MAX_BONDED_NODE_NUMBER = 0xEF; 
        final int MAX_BYTES_USED = (int)Math.ceil(MAX_BONDED_NODE_NUMBER / 8.0);
        
        List<Integer> bondedNodesList = new LinkedList<>();
        
        for (int byteId = 0; byteId < 32; byteId++) {
            if (byteId >= MAX_BYTES_USED) {
                break;
            }
            
            if (protoValue[byteId] == 0) {
                continue;
            }
            
            int bitComp = 1;
            for (int bitId = 0; bitId < 8; bitId++) {
                if ((protoValue[byteId] & bitComp) == bitComp) {
                    bondedNodesList.add(byteId * 8 + bitId);
                }
                bitComp *= 2;
            }
        }
        BondedNodes bondedNodes = new BondedNodes(bondedNodesList);
        
        logger.debug("toObject - end: {}", bondedNodes);
        return bondedNodes;
    }
    
}
