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
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.AbstractConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting between Java {@code FRC_Command} type
 * and IQRF DPA protocol bytes.
 *
 * @author Rostislav Spinar
 */
public final class FRC_SelectCommandConvertor extends AbstractConvertor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FRC_SelectCommandConvertor.class);

    private FRC_SelectCommandConvertor() {
    }

    /** Singleton. */
    private static final FRC_SelectCommandConvertor instance = new FRC_SelectCommandConvertor();

    /**
     * @return {@code FRC_CommandConvertor} instance
     */
    @ConvertorFactoryMethod
    static public FRC_SelectCommandConvertor getInstance() {
        return instance;
    }

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);

        if (!(value instanceof FRC_Command)) {
            throw new ValueConversionException("Value to convert is not of FRC_Command type.");
        }

        FRC_Command frcCmd = (FRC_Command) value;

        short[] selectedNodes = getConvertedSelectedNodes(frcCmd);

        short[] protoValue = new short[1 + frcCmd.getUserData().length + selectedNodes.length];
        protoValue[0] = (short) frcCmd.getId();

        System.arraycopy(selectedNodes, 0, protoValue, 1, selectedNodes.length);

        System.arraycopy(frcCmd.getUserData(), 0, protoValue,
                1 + selectedNodes.length, frcCmd.getUserData().length);

        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    /**
     * Convert selected nodes in speicified FRC command to proto value.
     *
     * @param cmd frc command with selected nodes
     * @return proto value of selected nodes
     * @throws ValueConversionException if frc command doesn't contain any
     * selected nodes
     */
    private short[] getConvertedSelectedNodes(FRC_Command cmd) throws ValueConversionException {
        Node[] nodes = cmd.getSelectedNodes();

        if (nodes == null) {
            throw new ValueConversionException("FRC command to convert doesn't contain selected nodes.");
        }

        //converts Nodes array into array of true / false
        int actualNodeIndex = 0;
        boolean[] truthMap = new boolean[239];
        for (int i = 0; i < truthMap.length; i++) {
            int id = Integer.parseInt(nodes[actualNodeIndex].getId());
            //check if id of node is same as index [of node] in truth map
            if (id == i) {
                //set in truth map, that this node is selected and increment pointer on actual exploring node
                truthMap[i] = true;
                actualNodeIndex++;
                if (actualNodeIndex >= nodes.length) {
                    break;
                }
            }
        }
        
        //create proto value form true and false values
        short[] protoValue = new short[30];

        for (int i = 0; i < 30; i++) {
            StringBuilder build = new StringBuilder();
            for (int j = i != 29 ? 7 : 6; j >= 0; j--) {
                int mapIndex = (i * 8) + j;
                build.append(truthMap[mapIndex] ? "1" : "0");
            }
            protoValue[i] = Byte.parseByte(build.toString(), 2);
        }
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
