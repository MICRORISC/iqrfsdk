
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Module ID of the remotely bonded node.
 * 
 * @author Michal Konopa
 */
public class RemotelyBondedModuleId {
    /** Module ID. */
    private final short[] moduleId;
    
    /** User data. */
    private final short[] userData;
    
    
    /**
     * Creates new remotely bonded module ID.
     * @param moduleId module ID of remotely bonded node.
     * @param userData bonding user data specified at Reset Custom DPA Handler event
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
