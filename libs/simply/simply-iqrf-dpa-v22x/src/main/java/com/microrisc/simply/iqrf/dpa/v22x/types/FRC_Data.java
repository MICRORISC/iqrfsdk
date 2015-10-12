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

package com.microrisc.simply.iqrf.dpa.v22x.types;

import java.util.Arrays;

/**
 * FRC data collected from nodes.
 * 
 * @author Michal Konopa
 */
public final class FRC_Data {
    /** 
     * Return code of the sendFRC() IQRF OS function. See IQRF OS documentation
     * for more information.
     */
    private final int status;
    
    /**
     * Data collected from the nodes.
     */
    private final short[] data;
    
    
    /**
     * Creates new FRC data object. 
     * @param status Return code of the sendFRC() IQRF OS function.
     * @param data Data collected from the nodes.
     */
    public FRC_Data(int status, short[] data) {
        this.status = status;
        this.data = new short[ data.length ];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     * @return Return code of the sendFRC() IQRF OS function
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return Data collected from the nodes
     */
    public short[] getData() {
        short[] dataToReturn = new short[ data.length ];
        System.arraycopy(data, 0, dataToReturn, 0, data.length);
        return dataToReturn;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Status: " + status + NEW_LINE);
        strBuilder.append(" Data: " + Arrays.toString(data) + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    /**
     * Returns pretty formated string information. 
     * @return pretty formated string information.
     */
    public String toPrettyFormatedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("Status: " + status + NEW_LINE);
        strBuilder.append("Data: " + Arrays.toString(data) + NEW_LINE);
        
        return strBuilder.toString();
    }
    
}
