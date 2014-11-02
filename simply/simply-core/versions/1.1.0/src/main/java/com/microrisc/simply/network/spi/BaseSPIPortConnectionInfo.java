/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
