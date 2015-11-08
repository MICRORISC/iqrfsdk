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

import com.microrisc.simply.iqrf.dpa.DPA_ResponseCode;

/**
 * Encapsulates additional information comming from DPA network messages.  
 * 
 * @author Michal Konopa
 */
public final class DPA_AdditionalInfo {
    /** HW profile. */
    private final int hwProfile;
    
    /** Response code. */
    private final DPA_ResponseCode responseCode;
    
    /** DPA value. */
    private final int dpaValue;
    
    
    /**
     * Creates new additional info encapsulating specified informations.
     * @param hwProfile HW profile
     * @param responseCode response code
     * @param dpaValue DPA value
     */
    public DPA_AdditionalInfo(int hwProfile, DPA_ResponseCode responseCode, int dpaValue) 
    {
        this.hwProfile = hwProfile;
        this.responseCode = responseCode;
        this.dpaValue = dpaValue;
    }
    
    /**
     * Returns HW profile relating to the last incomming result.
     * @return HW profile relating to the last incomming result.
     */
    public int getHwProfile() {
        return hwProfile;
    }
    
    /**
     * Returns DPA value relating to the last incomming result.
     * @return DPA value relating to the last incomming result.
     */
    public int getDPA_Value() {
        return dpaValue;
    }
    
    /**
     * Returns response code relating to the last incomming result.
     * @return response code relating to the last incomming result.
     */
    public DPA_ResponseCode getResponseCode() {
        return responseCode;
    }
    
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" HW profile ID: " + hwProfile + NEW_LINE);
        strBuilder.append(" Response code: " + responseCode + NEW_LINE);
        strBuilder.append(" DPA value: " + dpaValue + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormattedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("HW profile ID: " + hwProfile + NEW_LINE);
        strBuilder.append("Response code: " + responseCode + NEW_LINE);
        strBuilder.append("DPA value: " + dpaValue + NEW_LINE);
        
        return strBuilder.toString();
    }
    
}
