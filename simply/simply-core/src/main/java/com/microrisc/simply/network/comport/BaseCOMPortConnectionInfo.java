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

