
package com.microrisc.simply.network.udp;

import java.net.InetAddress;

/**
 * UDP connection information.
 * 
 * @author Michal Konopa
 */
public interface UDPConnectionInfo {
    /**
     * Returns target address.
     * @return target address
     */
    InetAddress getAddress();
    
    /**
     * Returns target port.
     * @return target port.
     */
    int getPort();
}
