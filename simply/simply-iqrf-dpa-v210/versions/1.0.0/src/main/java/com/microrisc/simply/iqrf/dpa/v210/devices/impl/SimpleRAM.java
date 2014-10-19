
package com.microrisc.simply.iqrf.dpa.v210.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.v210.devices.RAM;

/**
 * Simple {@code RAM} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleRAM 
extends SimpleGeneralMemory implements RAM {
    public SimpleRAM(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
}
