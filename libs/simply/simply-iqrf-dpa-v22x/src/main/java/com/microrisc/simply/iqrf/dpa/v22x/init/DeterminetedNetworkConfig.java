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
package com.microrisc.simply.iqrf.dpa.v22x.init;

import com.microrisc.simply.iqrf.RF_Mode;
import com.microrisc.simply.iqrf.dpa.v22x.types.OsInfo;

/**
 *  Network speicific configuration funcionality.
 * 
 * @author Martin Strouhal
 */
public interface DeterminetedNetworkConfig {
    
    /**
     * Returns used TR_TypeSeries in network.
     * @return {@link OsInfo.TR_Type.TR_TypeSeries}
     */
    OsInfo.TR_Type.TR_TypeSeries getTRSeries();
    
    /**
     * Returns used RF_Mode in network.
     * @return {@link RF_Mode}
     */
    RF_Mode getRFMode();
}
