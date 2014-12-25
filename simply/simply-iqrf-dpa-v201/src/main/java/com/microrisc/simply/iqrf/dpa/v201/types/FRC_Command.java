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

package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * FRC commands common functionality.
 * 
 * @author Michal Konopa
 */
public interface FRC_Command {
    /**
     * Returns ID of command. Specifies data to be collected.
     * @return ID of FRC command
     */
    int getId();
    
    /**
     * User data that are available at IQRF OS array variable DataOutBeforeResponseFRC 
     * at FRC Value event. The length is from 2 to 30 bytes.
     * @return user data
     */
    short[] getUserData();
}
