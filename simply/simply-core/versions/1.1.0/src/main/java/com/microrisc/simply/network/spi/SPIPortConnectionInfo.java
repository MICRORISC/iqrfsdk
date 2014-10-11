
package com.microrisc.simply.network.spi;

/**
 * Connection information specific to SPI-port, on which is network connected.
 * 
 * @author Rostislav Spinar
 */
public interface SPIPortConnectionInfo {

    /**
     * @return the SPI-port name for access to the network
     */
    String getSPIPortName();
}
