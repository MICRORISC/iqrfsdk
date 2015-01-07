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

package com.microrisc.simply.protocol;

import com.microrisc.simply.protocol.mapping.PacketFragment;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates sequential process of a creation of final protocol layer packet.
 * <p>
 * Final request packet is created by consecutive adding of packet fragments.
 * Each fragment is defined by 2 properties:
 * - starting position in the final packet
 * - own data of that fragment
 * 
 * @author Michal Konopa
 */
public final class RequestPacketCreator {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(RequestPacketCreator.class);
    
    /**
     * Returns maximum position of data of specified packet fragments. 
     * @param packetFragments 
     */
    private static int getMaxPosition(List<PacketFragment> packetFragments) {
        logger.debug("getMaxPosition - start: packetFragments{}", packetFragments);
        
        int maxPos = 0;
        for (PacketFragment fragment : packetFragments) {
            int pos = fragment.getStartingPosition() + fragment.getData().length;
            if (pos > maxPos) {
                maxPos = pos;
            }
        }
        
        logger.debug("getMaxPosition - end: {}", maxPos);
        return maxPos;
    }
    
    
    /**
     * Creates and returns complete request packet of protocol layer.
     * @param packetFragments packet fragments 
     * @return complete request packet of protocol layer
     */
    public static short[] createRequestPacket(List<PacketFragment> packetFragments) {
        logger.debug("createRequestPacket - start: packetFragments{}", packetFragments);
        
        short[] finalPacket = new short[getMaxPosition(packetFragments)]; 
        for (PacketFragment packetFragment : packetFragments) {
            System.arraycopy(packetFragment.getData(), 0, finalPacket, 
                    packetFragment.getStartingPosition(), packetFragment.getData().length);
        }
        
        logger.debug("createRequestPacket - end: {}", finalPacket);
        return finalPacket;
    }
}
