
package com.microrisc.simply.iqrf.dpa.v201.devices.impl;

import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.v201.devices.LEDG;

/**
 * Simple {@code LEDG} implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleLEDG 
extends SimpleGeneralLED implements LEDG {
    public SimpleLEDG(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
}
