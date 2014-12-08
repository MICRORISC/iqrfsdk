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

package com.microrisc.spi.iqrf_protocol;

/**
 * Parses responses incomming from SPI slave device.
 * 
 * @author Michal Konopa
 */
public final class ResponseParser {
    
    /** Singleton. */
    private static final ResponseParser instance = new ResponseParser();
    
    /**
     * Returns instance of the response parser.
     * @return instance of the response parser
     */
    public static ResponseParser getInstance() {
        return instance;
    }
    
    
    /** Position of data inside Read/Write packet.  */
    private static final int DATA_POS = 2;
    
    // parses specified SPI status value
    private SPI_Status parseSPI_StatusValue( int statValue ) {
        if ( 
                ( statValue < SPI_Status.NOT_ACTIVE_DISABLED )
                || ( statValue > SPI_Status.NOT_ACTIVE_HW_ERROR ) 
        ) {
            throw new IllegalArgumentException("Unknown value of SPI status: " + statValue);
        }
        
        switch ( statValue ) {
            case SPI_Status.NOT_ACTIVE_DISABLED:
            case SPI_Status.SUSPENDED:
            case SPI_Status.NOT_READY_CRCM_OK:
            case SPI_Status.NOT_READY_CRCM_ERROR:
            case SPI_Status.READY_COMM_MODE:
            case SPI_Status.READY_PROG_MODE:
            case SPI_Status.READY_DEBUG_MODE:
            case SPI_Status.NOT_ACTIVE_HW_ERROR:
                return new SPI_Status(statValue, false);
        }
        
        if ( 
             statValue >= SPI_Status.DATA_READY_BOTTOM_LIMIT
             && statValue <= (SPI_Status.DATA_READY_BOTTOM_LIMIT + SPI_Status.OS301D_MAX_DATA_LEN) 
        ) {
            return new SPI_Status(statValue, true);
        }
        
        throw new IllegalArgumentException("Unknown value of SPI status: " + statValue);
    }
    
    private short[] checkPacketData(short[] packetData) {
        if ( packetData == null ) {
            throw new IllegalArgumentException("Packet data cannot be null");
        } 
        return packetData;
    }
    
    /**
     * Parses specified packet source data and returns corresponding parsed 
     * representation. 
     * @param packetData packet data to parse
     * @return parsed packet representation corresponding to specified source data
     * @throws IllegalArgumentException if {@code packetData} is {@code null}
     * @throws ResponsePacketParseException if an error has occured during parsing
     */
    public AbstractResponsePacket parse(short[] packetData) 
            throws ResponsePacketParseException 
    {
        checkPacketData(packetData);
        
        // Check SPI response packet
        if ( packetData.length == 1 ) {
            SPI_Status spiStatus = parseSPI_StatusValue( packetData[0] & 0xFF );
            return new CheckSPI_StatusResponse(spiStatus);
        }
        
        if ( packetData.length < 3 ) {
            throw new ResponsePacketParseException("Invalid packet length: " + packetData.length);
        }
        
        // read/write response packet
        SPI_Status firstSpiStatus = parseSPI_StatusValue( packetData[0] & 0xFF );
        SPI_Status secondSpiStatus = parseSPI_StatusValue( packetData[1] & 0xFF );
        
        short[] data = new short[packetData.length - 3];
        System.arraycopy(packetData, DATA_POS, data, 0, data.length);
        
        short crcs = packetData[packetData.length - 1];
        
        ReadWriteResponse response = null;
        try { 
            response = new ReadWriteResponse(firstSpiStatus, secondSpiStatus, data, crcs);
        } catch ( CRC_VerificationException ex ) {
            throw new ResponsePacketParseException(ex);
        }
        return response;
    }
}
