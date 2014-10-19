

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
     * @return
     * @throws Exception 
     */
    @Override
    public PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper() throws Exception {
        return new DPA_StandardPerToDevIfaceMapper();
    }
    
}
