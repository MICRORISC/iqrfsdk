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

package com.microrisc.simply.iqrf.dpa.v22x.protocol;

/**
 * Encapsulates error which occurs when a time limited state of the 
 * Protocol State Machine ( aka PSM ) timeouted during processing of the new 
 * incomming event.
 * <p>
 * <b>Time limited states</b> are states, which the PSM can reside in only for 
 * a limited amount of time. If no event, which cause a transition of the PSM 
 * into new state comes in, the PSM passes in corresponding error state.<br>
 * Time limited stated of the Protocol State Machine: <br>
 * - {@code WAITING_FOR_CONFIRMATION} <br>
 * - {@code WAITING_FOR_RESPONSE} <br>
 * 
 * @author Michal Konopa
 */
public class StateTimeoutedException extends Exception {
    public StateTimeoutedException() {
        super();
    }
    
    public StateTimeoutedException(String message) {
        super(message);
    }
    
    public StateTimeoutedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public StateTimeoutedException(Throwable cause) {
        super(cause);
    }
}
