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
package com.microrisc.simply.iqrf.dpa.v220.types;

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
        TIME_40_MS,
        /** Time 320ms. */
        TIME_320_MS,
        /** Time 640ms. */
        TIME_640_MS,
        /** Time 1280ms. */
        TIME_1280_MS,
        /** Time 2560ms. */
        TIME_2560_MS,
        /** Time 5120ms. */
        TIME_5120_MS,
        /** Time 10240ms. */
        TIME_10240_MS,
        /** Time 20480ms. */
        TIME_20480_MS;
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
