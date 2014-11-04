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

package com.microrisc.cdc;

/**
 * Response information of "DS-command". Precise meaning of constants can be
 * found in "CDC Implementation in IQRF USB devices User Guide".
 * <p>
 * Peer class for DSResponse.
 * 
 * @version     1.0
 */
public enum J_DSResponse {
    /** Data successfully sent to TR module. */
    OK      (0x0),
    /** 
     * Denotes one of the following situations: <br> 
     * - communication failure(checksum error)<br>
     * - length of message is out of range,<br> 
     * - data length mismatch 
     */
    ERR     (0x1),
    /** 
     * Denotes one of the following situations: <br> 
     * - SPI is busy<br>
     * - communication is just running,<br> 
     * - TR module is not in communication mode 
     */
    BUSY    (0x2);
    
    /** Numeric value of response. */
    private final int respValue;
    
    J_DSResponse(int respValue) {
        this.respValue = respValue;
    }
    
    /**
     * Returns numeric value of response.
     * @return numeric value of response.
     */
    public int getRespValue() {
        return this.respValue;
    }
}
