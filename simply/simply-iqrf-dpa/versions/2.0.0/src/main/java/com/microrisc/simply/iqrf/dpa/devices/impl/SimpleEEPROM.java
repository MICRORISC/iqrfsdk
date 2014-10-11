
package com.microrisc.simply.iqrf.dpa.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.devices.EEPROM;

/**
 * Simple EEPROM implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleEEPROM 
extends SimpleGeneralMemory implements EEPROM {
    public SimpleEEPROM(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
}
