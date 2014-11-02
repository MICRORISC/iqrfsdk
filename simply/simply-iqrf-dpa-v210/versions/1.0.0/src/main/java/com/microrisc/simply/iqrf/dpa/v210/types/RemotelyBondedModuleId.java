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

package com.microrisc.simply.iqrf.dpa.v210.types;

/**
 * Module ID of the remotely bonded node.
 * 
 * @author Michal Konopa
 */
public final class RemotelyBondedModuleId {
    /** Module ID. */
    private final short[] moduleId;
    
    /** User data. */
    private final short[] userData;
    
    
    /**
     * Creates new remotely bonded module ID.
     * @param moduleId module ID of the remotely bonded node
     * @param userData optional bonding user data specified at Reset Custom DPA Handler event
     */
    public RemotelyBondedModuleId(short[] moduleId, short[] userData) {
        this.moduleId = new short[moduleId.length];
        System.arraycopy(moduleId, 0, this.moduleId, 0, moduleId.length);
        
        this.userData = new short[userData.length];
        System.arraycopy(userData, 0, this.userData, 0, userData.length);
    }

    /**
     * @return the module ID
     */
    public short[] getModuleId() {
        short[] moduleIdToReturn = new short[moduleId.length];
        System.arraycopy(moduleId, 0, moduleIdToReturn, 0, moduleId.length);
        return moduleIdToReturn;
    }

    /**
     * @return the user data
     */
    public short[] getUserData() {
        short[] userDataToReturn = new short[userData.length];
        System.arraycopy(userData, 0, userDataToReturn, 0, userData.length);
        return userDataToReturn;
    }
   
}
