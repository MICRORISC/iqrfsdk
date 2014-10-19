

package com.microrisc.simply.iqrf.dpa.v201.init;

import com.microrisc.simply.init.InitObjects;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;

/**
 * Provides access to objects, which are needed in the process of initialization 
 * of Simply DPA.
 * 
 * @author Michal Konopa
 * @param <T> type of configuration settings
 */
public interface DPA_InitObjects<T extends Object> extends InitObjects<T> {
    /**
     * Returns DPA Peripherals to Device Interfaces mapper
     * @return DPA Peripherals to Device Interfaces Mapper
     */
    PeripheralToDevIfaceMapper getPeripheralToDevIfaceMapper();
}
