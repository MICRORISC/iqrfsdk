

package com.microrisc.simply.iqrf.dpa.asynchrony;

import com.microrisc.simply.AbstractMessage;

/**
 * Simple implementation of 
 * {@code DPA_AsynchronousMessage.DPA_AsynchronousMessageSource} interface.
 * 
 * @author Michal Konopa
 */
public final class SimpleDPA_AsynchronousMessageSource 
implements DPA_AsynchronousMessage.DPA_AsynchronousMessageSource 
{
    /** Reference to object, which implements Message Source. */
    private final AbstractMessage.MessageSource source;

    /** Peripheral number. */
    private final int peripheralNum;

    
    /**
     * Creates new object of DPA asynchronous message source.
     * @param source "basic" asynchronous message source
     * @param peripheralNum number of source peripheral
     */
    public SimpleDPA_AsynchronousMessageSource(
            AbstractMessage.MessageSource source, int peripheralNum
    ) {
        this.source = source;
        this.peripheralNum = peripheralNum;
    }
    
    /**
     * @return network ID
     */
    @Override
    public String getNetworkId() {
        return source.getNetworkId();
    }

    /**
     * @return node ID
     */
    @Override
    public String getNodeId() {
        return source.getNodeId();
    }

    /**
     * @return peripheral ID
     */
    @Override
    public int getPeripheralNumber() {
        return peripheralNum;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                source.toString() + 
                "peripheral number=" + peripheralNum + 
                " }");
    }
    
}
