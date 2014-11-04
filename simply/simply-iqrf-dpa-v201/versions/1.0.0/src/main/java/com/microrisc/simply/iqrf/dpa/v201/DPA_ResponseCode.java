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

package com.microrisc.simply.iqrf.dpa.v201;

/**
 * Response codes.
 * 
 * @author Michal Konopa
 */
public enum DPA_ResponseCode {
    /** No error. */
    NO_ERROR            (0),
    
    /** General fail. */
    GENERAL_FAIL        (1),
    
    /** Incorrect PCmd. */
    PCMD_ERROR          (2),
    
    /** Incorrect PNum. */
    PNUM_ERROR          (3),
    
    /** Incorrect Address. */
    ADDRESS_ERROR       (4),
    
    /** Incorrect data length. */
    DATA_LEN_ERROR      (5),
    
    /** Incorrect data. */
    DATA_ERROR          (6),
    
    /** Incorrect HW Profile type used. */
    HWPROFILE_ERROR     (7),
    
    /** Incorrect Nadr. */
    NADR_ERROR          (8),
    
    /** Data from interface consumed by Custom DPA handler. */
    CUSTOM_HANDLER      (9),
    
    /** Error code used to mark confirmation. */
    CONFIRMATION        ( 0xFF )
    ;
    
    
    /** response code */
    private final int code;
    
    private DPA_ResponseCode(int code) {
        this.code = code;
    }
    
    /**
     * Returns integer value of response code.
     * @return integer value of response code.
     */
    public int getCodeValue() {
        return code;
    }
}
