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
package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * AutonetworkPeripheral providing access to Autonetwork-embedded peripheral on
 * Coordinator.
 * <p>
 * @author Martin Strouhal
 */
@DeviceInterface
public interface AutonetworkPeripheral extends DPA_StandardServices,
        GenericAsyncCallable, MethodIdTransformer {

   /** Identifiers of this Device Interface's methods. */
   enum MethodID implements DeviceInterfaceMethodId {
      APPROVE,
      DISAPPROVE;
   }

   /**
    * Synchronous wrapper for {@link #async_approve() }
    *
    * @return {@link VoidType}
    */
   VoidType approve();

   /**
    * Sends method call request for approving node.
    *
    * @return unique identifier of sent request
    */
   UUID async_approve();


   /**
    * Synchronous wrapper for {@link #async_disapprove() }
    *
    * @return {@link VoidType}
    */
   VoidType disapprove();

   /**
    * Sends method call request for disapproving node.
    *
    * @return unique identifier of sent request
    */
   UUID async_disapprove();
}
