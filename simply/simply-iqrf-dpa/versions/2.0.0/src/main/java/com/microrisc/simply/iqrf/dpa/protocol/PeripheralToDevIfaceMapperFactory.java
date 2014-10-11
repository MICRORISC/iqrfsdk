

package com.microrisc.simply.iqrf.dpa.protocol;

/**
 * Factory for {@code PeripheralToDevIfaceMapper} objects.
 * 
 * @author Michal Konopa
 */
public interface PeripheralToDevIfaceMapperFactory {
    /**
     * Creates peripheral to device interface mapping implementation.
     * @return peripheral to device interface mapping implementation
     * @throws Exception if an error has occured during creation 
     */
    PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper() throws Exception;
}
