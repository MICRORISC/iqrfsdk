/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.logic;

import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkState;

/**
 * Listener for {@link AutonetworkState autonetwork states} while autonetwork
 * embedded algorithm is running.
 *
 * @author Martin Strouhal
 */
public interface AutonetworkStateListener {

   /**
    * Called on receive {@link AutonetworkState}.
    * 
    * @param state which was received
    */
   public void onAutonetworkState(AutonetworkState state);

   /**
    * Free up resources.
    */
   public void destroy();
}
