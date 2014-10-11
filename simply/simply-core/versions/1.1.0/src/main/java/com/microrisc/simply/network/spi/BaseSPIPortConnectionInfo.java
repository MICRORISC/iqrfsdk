
package com.microrisc.simply.network.spi;

import com.microrisc.simply.network.AbstractNetworkConnectionInfo;

/**
 * Base class of implementation of SPI-port connection information.
 * 
 * @author Rostislav Spinar
 */
public class BaseSPIPortConnectionInfo
extends AbstractNetworkConnectionInfo implements SPIPortConnectionInfo {
    /** SPI-port for access to the network. */
    protected String spiPortName;
    
    
    /**
     * Protected constructor.
     * @param spiPortName SPI-port for access to the network 
     */
    public BaseSPIPortConnectionInfo(String spiPortName) {
        this.spiPortName = spiPortName;
    }

    /**
     * @return the SPI-port number for access to the network
     */
    @Override
    public String getSPIPortName() {
        return spiPortName;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "port name=" + spiPortName +  
                " }");
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof SPIPortConnectionInfo) ) {
            return false;
        }
        
        SPIPortConnectionInfo spiPortConnectionInfo = (SPIPortConnectionInfo) obj;
        return (this.spiPortName.equals(spiPortConnectionInfo.getSPIPortName()));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.spiPortName != null ? this.spiPortName.hashCode() : 0);
        return hash;
    }
}    
