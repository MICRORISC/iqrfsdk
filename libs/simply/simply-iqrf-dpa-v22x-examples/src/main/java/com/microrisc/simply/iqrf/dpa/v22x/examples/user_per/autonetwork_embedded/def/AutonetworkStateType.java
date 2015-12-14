/*
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.autonetwork_embedded.def;

/**
 *
 * @author Martin Strouhal
 */
public enum AutonetworkStateType {

    START(0x00, "Autonetwork was started"), UNKNOWN(Integer.MAX_VALUE, "Unknown state.");

    private final int stateId;
    private final String info;

    private AutonetworkStateType(int stateId, String info) {
        this.stateId = stateId;
        this.info = info;
    }

    public static AutonetworkStateType getState(int stateId) {
        for (AutonetworkStateType e : values()) {
            if (e.stateId == stateId) {
                return e;
            }
        }
        return UNKNOWN;
    }

    public int getId() {
        return stateId;
    }

    public String getInfo() {
        return info;
    }
}
