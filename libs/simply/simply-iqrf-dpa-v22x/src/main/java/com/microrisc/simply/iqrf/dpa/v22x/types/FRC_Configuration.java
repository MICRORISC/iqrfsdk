/*
 * Copyright 2015 MICRORISC s.r.o..
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
 * Encapsulates configuration of FRC peripheral.
 *
 * @author Martin Strouhal
 */
public class FRC_Configuration {

    /**
     * FRC_RESPONSE_TIME defines maximum time reserved for preparing return FRC
     * value.
     */
    public enum FRC_RESPONSE_TIME {
                
        /** Time 40ms. */
        TIME_40_MS(40, 0b00000000),
        /** Time 320ms. */
        TIME_320_MS(320, 0b00010000),
        /** Time 640ms. */
        TIME_640_MS(640, 0b00100000),
        /** Time 1280ms. */
        TIME_1280_MS(1280, 0b00110000),
        /** Time 2560ms. */
        TIME_2560_MS(2560, 0b01000000),
        /** Time 5120ms. */
        TIME_5120_MS(5120, 0b01010000),
        /** Time 10240ms. */
        TIME_10240_MS(10240, 0b01100000),
        /** Time 20480ms. */
        TIME_20480_MS(20480, 0b01110000);

        private int responseTime;
        private int idValue;
        
        private FRC_RESPONSE_TIME(int time, int idValue){
            this.responseTime = time;
            this.idValue = idValue;
        }
        
        public int getRepsonseTimeInInt(){
            return responseTime;
        }
        
        public int getIdValue(){
           return idValue;
        }
        
        /**
         * Returns {@link FRC_RESPONSE_TIME} for time specified in parameters in
         * miliseconds. If isn't possible find correct time, the biggest time is
         * returned.
         *
         * @param timeInMS repsonse time in miliseconds
         * @return {@link FRC_RESPONSE_TIME}
         */
        public static FRC_RESPONSE_TIME getResponseTimeFor(int timeInMS) {
            FRC_RESPONSE_TIME times[] = FRC_RESPONSE_TIME.values();
            for (FRC_RESPONSE_TIME time : times) {
                if(time.getRepsonseTimeInInt() == timeInMS){
                    return time;
                }
            }
            // if any time wasn't found
            return TIME_20480_MS;
        }                
    }

    private final FRC_RESPONSE_TIME responseTime;

    /**
     * Create new FRC Configuration with specified FRC response time. See
     * {@link FRC_RESPONSE_TIME}.
     *
     * @param responseTime to save into configuration
     */
    public FRC_Configuration(FRC_RESPONSE_TIME responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * Returns response time saved in configuration.
     *
     * @return {@link FRC_RESPONSE_TIME}
     */
    public FRC_RESPONSE_TIME getResponseTime() {
        return responseTime;
    }
}
