/*
 * Copyright 2014 MICRORISC s.r.o..
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

/**
 * DPA confirmation.
 * 
 * @author Michal Konopa
 */
public final class DPA_Confirmation {
    /** DPA value. */
    private final DPA_Parameter dpaValue;
    
    /** Number of hops used to deliver the DPA request to the addressed node. */
    private final int hops;
    
    /** 
     * Timeslot length used to deliver the DPA request to the addressed node. 
     * Please note that the timeslot used to deliver the response message from 
     * node to coordinator can have a different length. 
     */
    private final int timeslotLength;
    
    /**
     * Number of hops used to deliver the DPA response from the addressed 
     * node back to coordinator. In case of broadcast this parameter is 0 as 
     * there is no response.
     */
    private final int hopsResponse;
    
    
    /**
     * Creates new object of DPA Confirmation.
     * @param dpaValue DPA value
     * @param hops Number of hops used to deliver the DPA request to the addressed node.
     * @param timeslotLength Timeslot length used to deliver the DPA request to the addressed node.
     * @param hopsResponse Number of hops used to deliver the DPA response from the addressed 
     *                     node back to coordinator. In case of broadcast this parameter is 0 as 
     *                     there is no response.
     */
    public DPA_Confirmation(
            DPA_Parameter dpaValue, int hops, int timeslotLength, int hopsResponse
    ) {
        this.dpaValue = dpaValue;
        this.hops = hops;
        this.timeslotLength = timeslotLength;
        this.hopsResponse = hopsResponse;
    }

    /**
     * @return the DPA value
     */
    public DPA_Parameter getDpaValue() {
        return dpaValue;
    }

    /**
     * @return number of hops used to deliver the DPA request to the addressed node
     */
    public int getHops() {
        return hops;
    }

    /**
     * @return timeslot length used to deliver the DPA request to the addressed node
     */
    public int getTimeslotLength() {
        return timeslotLength;
    }

    /**
     * @return Number of hops used to deliver the DPA response from the addressed 
     *         node back to coordinator. In case of broadcast this parameter is 0 as 
     *         there is no response.
     */
    public int getHopsResponse() {
        return hopsResponse;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" DPA value: " + dpaValue + NEW_LINE);
        strBuilder.append(" Hops: " + hops + NEW_LINE);
        strBuilder.append(" Timeslot length: " + timeslotLength + NEW_LINE);
        strBuilder.append(" Hops response: " + hopsResponse + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
}
