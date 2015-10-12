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
 * States of LED.
 * 
 * @author Michal Konopa
 */
public enum LED_State {
    OFF    (0x00),
    ON     (0x01);
    
    // state
    private final int state;
    
    
    private LED_State(int state) {
        this.state = state;
    }
    
    /**
     * @return integer value of LED state.
     */
    public int getStateValue() {
        return state;
    }
}
