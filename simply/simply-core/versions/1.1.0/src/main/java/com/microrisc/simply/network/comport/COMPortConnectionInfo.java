
package com.microrisc.simply.network.comport;

/**
 * Connection information specific to COM-port, on which is network connected.
 * 
 * @author Michal Konopa
 */
public interface COMPortConnectionInfo {

    /**
     * @return the COM-port name for access to the network
     */
    String getCOMPortName();
    
}
