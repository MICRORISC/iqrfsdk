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

/**
 * DPA Params (DPA Parameters) is one byte parameter stored at the coordinator 
 * RAM that configures network behavior. Default value 0x00 is set upon 
 * coordinator reset.
 * 
 * @author Michal Konopa
 */
public final class DPA_Parameter {
    /** 
     * Specifies which type of DPA Value is returned inside every DPA Response 
     * or DPA Confirmation messages.
     * - 00: lastRSSI IQRF OS variable <br>
     * - 01: value returned by getSupplyVoltage() IQRF OS call <br>
     * - 10: system <br>
     * - 11: User specified DPA Value <br>
     */
    public enum DPA_ValueType {
        /** lastRSSI IQRF OS variable */
        LAST_RSSI           (0),
        
        /** value returned by getSupplyVoltage() IQRF OS call */
        GET_SUPPLY_VOLTAGE  (1),
        
        /** system information  */
        SYSTEM              (2),
        
        /** user specified value */
        USER_SPECIFIED      (3);
        
        private final int valueType;
        
        
        private DPA_ValueType(int valueType) {
            this.valueType = valueType;
        }
        
        /**
         * @return integer value of value type.
         */
        public int getValueType() {
            return valueType;
        } 
    }
    
    /**
     * DPA Value type used.
     */
    private DPA_ValueType dpaValueType;
    
    /**
     * It allows to easily diagnose the network behavior based on following LED 
     * activities:
     * - Red LED flashes: when Node or Coordinator receive network message <br>
     * - Green LED flashes: when Coordinator sends network message or when Node 
     *                      routes network message
     */
    private boolean isLedActivityOn = false;
    
    /**
     * If 1, then instead of using ideal timeslot length a fixed 200 ms long 
     * timeslot is used. It allows to easier track network behavior.
     */
    private boolean isFixedTimeslotUsed = false;
    
    
    /**
     * Creates new DPA Parameters object.
     * @param dpaValueType DPA Value type.
     * @param isLedActivityOn specifies, if led activity is on
     * @param fixedTimeslotUsed specified, if fixed timeout is used
     */
    public DPA_Parameter(DPA_ValueType dpaValueType, boolean isLedActivityOn, 
            boolean fixedTimeslotUsed
    ) {
        this.dpaValueType = dpaValueType;
        this.isLedActivityOn = isLedActivityOn;
        this.isFixedTimeslotUsed = fixedTimeslotUsed;
    }
    
    
    /**
     * @return the DPA Value type
     */
    public DPA_ValueType getDpaValueType() {
        return dpaValueType;
    }
    
    /**
     * Sets DPA Value type according to specified parameter.
     * @param dpaValueType the DPA Value type to set
     */
    public void setDpaValueType(DPA_ValueType dpaValueType) {
        this.dpaValueType = dpaValueType;
    }
    
    /**
     * @return {@code true} if LED activity is on <br>
     *         {@code false}, otherwise
     */
    public boolean isLedActivityOn() {
        return isLedActivityOn;
    }
    
    /**
     * Sets if to use LED activity. 
     * @param ledActivityOn
     */
    public void setLedActivityOn(boolean ledActivityOn) {
        this.isLedActivityOn = ledActivityOn;
    }
    
    /**
     * @return {@code true} if fixed timeslot is used <br>
     *         {@code false}, otherwise
     */
    public boolean isFixedTimeslotUsed() {
        return isFixedTimeslotUsed;
    }
    
    /**
     * Sets if to use fixed timeslot.
     * @param fixedTimeslotUsed
     */
    public void useFixedTimeslot(boolean fixedTimeslotUsed) {
        this.isFixedTimeslotUsed = fixedTimeslotUsed;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" DPA value type: " + dpaValueType + NEW_LINE);
        strBuilder.append(" LED activity on: " + isLedActivityOn + NEW_LINE);
        strBuilder.append(" Fixed timeslot used: " + isFixedTimeslotUsed + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormattedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("DPA value type: " + dpaValueType + NEW_LINE);
        strBuilder.append("LED activity on: " + isLedActivityOn + NEW_LINE);
        strBuilder.append("Fixed timeslot used: " + isFixedTimeslotUsed + NEW_LINE);
        
        return strBuilder.toString();
    }
}
