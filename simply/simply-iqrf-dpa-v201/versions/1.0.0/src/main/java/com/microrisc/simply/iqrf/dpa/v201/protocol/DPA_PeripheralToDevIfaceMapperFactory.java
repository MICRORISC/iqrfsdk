

package com.microrisc.simply.iqrf.dpa.v201.protocol;

import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapperFactory;

/**
 * Factory for creation of mapping between standard DPA peripherals and Device Interfaces.
 * 
 * @author Michal Konopa
 */
public final class DPA_PeripheralToDevIfaceMapperFactory 
implements PeripheralToDevIfaceMapperFactory {
    
    /**
     * Returns mapping between standard DPA peripherals and Device Interfaces.
     * @return mapping between standard DPA peripherals and Device Interfaces
     * @throws Exception if an error has occured during the mapping creation
     */
    @Override
    public PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper() throws Exception {
        return new DPA_StandardPerToDevIfaceMapper();
    }
    
}
