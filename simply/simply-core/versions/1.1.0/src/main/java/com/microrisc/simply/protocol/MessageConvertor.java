
package com.microrisc.simply.protocol;

import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.NetworkData;

/**
 * Interface for conversion between Protocol Layer and Network Layer messages.
 * 
 * @author Michal Konopa
 */
public interface MessageConvertor {
    /**
     * Converts specified call request to used application protocol format and
     * returns it.
     * @param callRequest call request to convert
     * @return request converted to message of used protocol
     * @throws SimplyException if an error has occured during conversion
     */
    short[] convertToProtoFormat(CallRequest callRequest) throws SimplyException;
    
    /**
     * Converts specified message of used protocol to Device Object format and
     * returns it.
     * @param networkData network data containing protocol message to convert
     * @return converted form of specified protocol message data
     * @throws SimplyException if an error has occured during conversion
     */
    AbstractMessage convertToDOFormat(NetworkData networkData) throws SimplyException;
}
