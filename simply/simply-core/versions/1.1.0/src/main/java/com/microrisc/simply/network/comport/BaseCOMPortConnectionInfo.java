
package com.microrisc.simply.network.comport;

import com.microrisc.simply.network.AbstractNetworkConnectionInfo;

/**
 * Base class of implementation of COM-port connection information.
 * 
 * @author Michal Konopa
 */
public class BaseCOMPortConnectionInfo
extends AbstractNetworkConnectionInfo implements COMPortConnectionInfo {
    /** COM-port for access to the network. */
    protected String comPortName;
    
    
    /**
     * Protected constructor.
     * @param comPortName COM-port for access to the network 
     */
    public BaseCOMPortConnectionInfo(String comPortName) {
        this.comPortName = comPortName;
    }

    /**
     * @return the COM-port number for access to the network
     */
    @Override
    public String getCOMPortName() {
        return comPortName;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "port name=" + comPortName +  
                " }");
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof COMPortConnectionInfo) ) {
            return false;
        }
        
        COMPortConnectionInfo comPortConnectionInfo = (COMPortConnectionInfo) obj;
        return (this.comPortName.equals(comPortConnectionInfo.getCOMPortName()));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.comPortName != null ? this.comPortName.hashCode() : 0);
        return hash;
    }
}    

