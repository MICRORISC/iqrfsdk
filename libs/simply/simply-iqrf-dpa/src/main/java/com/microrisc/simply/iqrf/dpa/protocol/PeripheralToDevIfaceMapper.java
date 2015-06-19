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

package com.microrisc.simply.iqrf.dpa.protocol;

import java.util.Set;

/**
 * Holds mapping between DPA Peripherals and corresponding Device Interfaces.
 * 
 * @author Michal Konopa
 */
public interface PeripheralToDevIfaceMapper {
    /**
     * Returns set of IDs of DPA peripherals, each of which there exists a concrete mapping for.
     * @return set of IDs of DPA peripherals, each of which there exists a concrete mapping for.
     */
    Set<Integer> getMappedPeripherals();
    
    
    /**
     * Returns set of Device Interfaces, each of which there exists a concrete mapping for.
     * @return set of Device Interfaces, each of which there exists a concrete mapping for.
     */
    Set<Class> getMappedDeviceInterfaces();
    
    /**
     * Returns Device Interface, which corresponds to specified peripheral.
     * @param perId ID of peripheral
     * @return Device Interface, which corresponds to specified peripheral <br>
     *         {@code null}, if DI was not found
     */
    Class getDeviceInterface(int perId);

    /**
     * Returns identifier of peripheral, which corresponds to specified DI.
     * @param devInterface Device Interface
     * @return identifier of peripheral, which corresponds to specified DI <br>
     *         {@code null}, if peripheral was not found
     */
    Integer getPeripheralId(Class devInterface);
    
}
