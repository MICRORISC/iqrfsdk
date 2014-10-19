

package com.microrisc.simply.iqrf.dpa.v201.di_services.method_id_transformers;

import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v201.devices.Coordinator;
import java.util.EnumMap;
import java.util.Map;

/**
 * Standard method ID transformer for Coordinator. 
 * 
 * @author Michal Konopa
 */
public final class CoordinatorStandardTransformer implements MethodIdTransformer {
    /**
     * Mapping of method IDs to theirs string representations.
     */
    private static final Map<Coordinator.MethodID, String> methodIdsMap = 
            new EnumMap<Coordinator.MethodID, String>(Coordinator.MethodID.class);
    
    private static void initMethodIdsMap() {
        methodIdsMap.put(Coordinator.MethodID.GET_ADDRESSING_INFO, "1");
        methodIdsMap.put(Coordinator.MethodID.GET_DISCOVERED_NODES, "2");
        methodIdsMap.put(Coordinator.MethodID.GET_BONDED_NODES, "3");
        methodIdsMap.put(Coordinator.MethodID.CLEAR_ALL_BONDS, "4");
        methodIdsMap.put(Coordinator.MethodID.BOND_NODE, "5");
        methodIdsMap.put(Coordinator.MethodID.REMOVE_BONDED_NODE, "6");
        methodIdsMap.put(Coordinator.MethodID.REBOND_NODE, "7");
        methodIdsMap.put(Coordinator.MethodID.RUN_DISCOVERY, "8");
        methodIdsMap.put(Coordinator.MethodID.SET_DPA_PARAM, "9");
        methodIdsMap.put(Coordinator.MethodID.SET_HOPS, "10");
        methodIdsMap.put(Coordinator.MethodID.DISCOVERY_DATA, "11");
        methodIdsMap.put(Coordinator.MethodID.BACKUP, "12");
        methodIdsMap.put(Coordinator.MethodID.RESTORE, "13");
        methodIdsMap.put(Coordinator.MethodID.AUTHORIZE_BOND, "14");
        methodIdsMap.put(Coordinator.MethodID.BRIDGE, "15");
        methodIdsMap.put(Coordinator.MethodID.ENABLE_REMOTE_BONDING, "16");
        methodIdsMap.put(Coordinator.MethodID.READ_REMOTELY_BONDED_MODULE_ID, "17");
        methodIdsMap.put(Coordinator.MethodID.CLEAR_REMOTELY_BONDED_MODULE_ID, "18");
    }
    
    static  {
        initMethodIdsMap();
    }

    /** Singleton. */
    private static final CoordinatorStandardTransformer instance = new CoordinatorStandardTransformer();
    
    
    /**
     * @return CoordinatorStandardTransformer instance 
     */
    static public CoordinatorStandardTransformer getInstance() {
        return instance;
    }
    
    @Override
    public String transform(Object methodId) {
        if ( !(methodId instanceof Coordinator.MethodID) ) {
            throw new IllegalArgumentException(
                    "Method ID must be of type Coordinator.MethodID."
            );
        }
        return methodIdsMap.get((Coordinator.MethodID) methodId);
    }
}
