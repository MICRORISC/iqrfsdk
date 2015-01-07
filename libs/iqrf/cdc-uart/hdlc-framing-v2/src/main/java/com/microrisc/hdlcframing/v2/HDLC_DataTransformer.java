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

package com.microrisc.hdlcframing.v2;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;

/**
 * Transforms data to and reads data from HDLC stuffing protocol.
 * 
 * @author Michal Konopa
 */
public final class HDLC_DataTransformer {   
    private static final short FLAG_SEQUENCE = 0x7e;
    private static final short CONTROL_ESCAPE = 0x7d;
    private static final short ESCAPE_BIT = 0x20;
    
    
    /** Checks specified request to be sent to HDLC. */
    private static void checkRequestData(short[] dataToTransform) {
        if ( dataToTransform == null ) {
            throw new IllegalArgumentException("Data to transform  cannot be NULL");
        }
    }
    
    /** Checks specified request to be sent to HDLC. */
    private static void checkUartDataFrame(short[] uartDataFrame) {
        if ( uartDataFrame == null ) {
            throw new IllegalArgumentException("Incomming UART data frame cannot be null");
        }
    }
    
    /**
     * Returns new value of CRC.
     * @param crc current value of CRC
     * @param value input data byte
     * @return updated value of CRC
     */
    private static short updateCRC(short crc, short value) {
        for ( int bitLoop = 8; bitLoop != 0; --bitLoop, value >>= 1 ) { 
            if ( ( ( crc ^ value ) & 0x01 ) != 0 ) {
                crc = (short)( ( crc >> 1 ) ^ 0x8C );
            } else {
                crc >>= 1;
            } 
        }
        return crc;
    }
    
    /**
     * Converts and adds the converted value of specified byte into specified list. 
     * @param dataItem data item to convert to HDLC protocol
     * @param resultList target list to add the converted value into
     */
    private static void convertAndAddByte(short dataItem, List<Short> resultList) {
        if ( ( dataItem == FLAG_SEQUENCE ) || ( dataItem == CONTROL_ESCAPE ) ) {
            resultList.add( CONTROL_ESCAPE );
            Short secByte = (short) ( dataItem ^ ESCAPE_BIT );
            resultList.add(secByte);
        } else {
            resultList.add( dataItem );
        }
    }

    public static short[] transformToHLDCFormat(short[] dataToTransform) {
        checkRequestData(dataToTransform);
        List<Short> convertedDataList = new LinkedList<>();
        
        // begining byte
        convertedDataList.add( FLAG_SEQUENCE );
        
        // data and computing checksum
        short crc = 0xFF;
        
        for ( int dataId = 0; dataId < dataToTransform.length; dataId++ ) {
            crc = updateCRC(crc, dataToTransform[dataId]);
            convertAndAddByte(dataToTransform[dataId], convertedDataList);
        }
        
        // adding checksum
        convertAndAddByte(crc, convertedDataList);
        
        // ending byte
        convertedDataList.add( FLAG_SEQUENCE ); 
        
        Short[] transformedData = convertedDataList.toArray( new Short[] {});
        return ArrayUtils.toPrimitive(transformedData);
    }
    
    public static short[] getDataFromFrame(short[] uartDataFrame) 
            throws HDLC_FormatException {
        checkUartDataFrame(uartDataFrame);
        List<Short> dataList = new LinkedList<>();
        
        if ( uartDataFrame[0] != FLAG_SEQUENCE ) {
            throw new HDLC_FormatException("First byte must be 0x7e");
        }
        
        if ( uartDataFrame[ uartDataFrame.length-1 ] != FLAG_SEQUENCE ) {
            throw new HDLC_FormatException("Last byte must be 0x7e");
        }
        
        boolean escapedByte = false;
        short countedCRC = 0xFF;
        
        // getting starting position of a checksum
        int crcStartPos = uartDataFrame.length-2;
        if ( uartDataFrame[ uartDataFrame.length-3 ] == CONTROL_ESCAPE ) {
            crcStartPos = uartDataFrame.length-3;
        }
        
        for ( int dataId = 1; dataId < crcStartPos; dataId++ ) {
            short dataItem = uartDataFrame[dataId];
            
            if ( dataItem == CONTROL_ESCAPE ) {
                escapedByte = true;
                continue;
            }
            
            // previous byte was CONTROL ESCAPE
            if ( escapedByte ) {
                if ( (dataItem & ESCAPE_BIT ) == 0 ) {
                    dataItem |= ESCAPE_BIT;
                } else {
                    dataItem ^= ESCAPE_BIT;
                }
                escapedByte = false;
            }
            
            countedCRC = updateCRC(countedCRC, dataItem);
            
            dataList.add(dataItem);
        }
        
        short packetCRC = uartDataFrame[ uartDataFrame.length-2 ];
        if ( uartDataFrame[ uartDataFrame.length-3 ] == CONTROL_ESCAPE ) {
            packetCRC = uartDataFrame[ uartDataFrame.length-2 ];
            if ( (packetCRC & ESCAPE_BIT) == 0 ) {
                packetCRC |= ESCAPE_BIT;
            } else {
                packetCRC ^= ESCAPE_BIT;
            }
        }
        
        if ( countedCRC != packetCRC ) {
            throw new HDLC_FormatException(
                    "CRC mismatch. "
                    + "Counted: " + countedCRC + " Get: " + packetCRC
            );
        }
        
        Short[] data = dataList.toArray( new Short[] {} );
        return ArrayUtils.toPrimitive(data);
    }
}
