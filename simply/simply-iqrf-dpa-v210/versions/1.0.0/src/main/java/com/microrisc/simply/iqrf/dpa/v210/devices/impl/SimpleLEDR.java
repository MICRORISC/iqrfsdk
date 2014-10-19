
package com.microrisc.simply.iqrf.dpa.v210.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.v210.devices.LEDR;

/**
 * Simple {@code LEDR} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleLEDR 
extends SimpleGeneralLED implements LEDR {
    public SimpleLEDR(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
}
