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

package com.microrisc.simply.network.udp.gweth;

/**
 * Transformation of Simply data to and from GW-ETH-01 protocol. 
 * <p>
 * CRC computation was adopted from 
 * <a href="http://introcs.cs.princeton.edu/java/51data/CRC16CCITT.java.html">
 * http://introcs.cs.princeton.edu/java/51data/CRC16CCITT.java.html</a>
 * site.
 * 
 * @author Michal Konopa
 */
public class GWETH_DataTransformer {
    /** Size of CRC part. */
    private static final int CRC_SIZE = 2;
    
    /** Size of header part. */
    private static final int HEADER_SIZE = 9;
    
    private static final int GW_ADR_POS = 0;
    private static final int GW_ADR_VALUE = 0x22;
    
    private static final int CMD_POS = 1;
    private static final int CMD_WRITE_VALUE = 0x03;
    private static final int CMD_ASYNC_VALUE = 0x04;
    private static final int CMD_ANSWER_INDICATION = 0x80;
    
    private static final int SUBCMD_POS = 2;
    private static final int SUBCMD_WRITE_VALUE = 0;
    private static final int SUBCMD_ASYNC_VALUE = 0;
    
    private static final int DLEN_H_POS = 7;
    private static final int DLEN_L_POS = 8;
    
    
    /** Maximal length of DATA part. */
    public static final int MAX_DATA_LENGTH = 497;
    
    
    /** Returns value of data length fields in specified message. */
    private static int getDataSizeField(short[] data) {
        return data[DLEN_L_POS] + (data[DLEN_H_POS] << 8);   
    }
    
    /** Returns value CRC of fields in specified message. */
    private static int getCRCField(short[] data) {
        return data[data.length - 1] + (data[data.length - 2] << 8);
    }
    
    /** Checks specified request to be sent to GW. */
    private static void checkRequest(short[] dataToTransform) {
        if (dataToTransform.length > MAX_DATA_LENGTH) {
            throw new IllegalArgumentException("Maximal size of data to transform "
                    + " cannot be greather then" + MAX_DATA_LENGTH);
        }
    }
    
    /** Checks data length of specified mesage. */
    private static void checkMessageLength(short[] messageFromGW) {
        if (messageFromGW.length < (HEADER_SIZE + CRC_SIZE)) {
            throw new IllegalArgumentException("Size of GW message must be at minimal"
                    + (HEADER_SIZE + CRC_SIZE) + " bytes long.");
        }
        
        if (messageFromGW.length > (HEADER_SIZE + MAX_DATA_LENGTH + CRC_SIZE)) {
            throw new IllegalArgumentException("Size of GW message cannot be greather"
                    + "then " + (HEADER_SIZE + MAX_DATA_LENGTH + CRC_SIZE) + " bytes long.");
        }
        
        int dataSize = getDataSizeField(messageFromGW);
        if (dataSize != (messageFromGW.length - (HEADER_SIZE + CRC_SIZE)) ) {
            throw new IllegalArgumentException("Message size mismatch: " + dataSize + ". "
                    + " It should be: " + (messageFromGW.length - (HEADER_SIZE + CRC_SIZE)));
        }
    }
    
    /** Checks CRC of specified message. */
    private static void checkMessageCRC(short[] messageFromGW) {
        int crc = getCRCField(messageFromGW);

        int crcCalculated = calculateCRC_microrisc(messageFromGW, 0, 
                HEADER_SIZE + getDataSizeField(messageFromGW));
        //only short part
        int crcCompare = crcCalculated & 0xFFFF;

        if ( crc != crcCompare ) {
            throw new IllegalArgumentException("CRC mismatch. CRC sent, received: " + crc + ", "
                    + "CRC calculated: " + crcCompare);
        }
    }
    
    /** Checks GW identification of specified message. */
    private static void checkMessageGWIdentification(short[] messageFromGW) {
        if (messageFromGW[GW_ADR_POS] != GW_ADR_VALUE) {
            throw new IllegalArgumentException("GW identification mismatch: " + 
                    messageFromGW[GW_ADR_POS]);
        }
    }
    
    /** Checks GW identification of specified message. */
    private static void checkMessageAsynchronity(short[] messageFromGW) {
        if ( messageFromGW[SUBCMD_POS] != SUBCMD_ASYNC_VALUE ) {
            throw new IllegalArgumentException("GW asynchronity identification mismatch: " + 
                    messageFromGW[SUBCMD_POS]);
        }
    }
    
    /** Checks message from GW. */
    private static void checkMessageFromGW(short[] messageFromGW) {
        checkMessageLength(messageFromGW);
        checkMessageCRC(messageFromGW);
        checkMessageGWIdentification(messageFromGW);
        
        // if the message has indication of asynchronity, check it
        if (messageFromGW[CMD_POS] == CMD_ASYNC_VALUE) {
            checkMessageAsynchronity(messageFromGW);
        }
    }
    
    /** 
     * Calculates CRC on specified data.
     * Taken from other {@link http://introcs.cs.princeton.edu/java/51data/CRC16CCITT.java.html} site.
     */
    private static int calculateCRC(short[] data, int firstIndex, int byteCount) {
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 

        for (int index = firstIndex; index < (firstIndex + byteCount); index++) {
            short b = data[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
             }
        }

        crc &= 0xffff;
        return crc;
    }
    
    /** 
     * Calculates CRC on specified data.
     * Taken from the Microrisc implementation.
     */
    private static int calculateCRC_microrisc(short[] data, int firstIndex, int byteCount) {
        int crcDbyte = data[firstIndex];
        crcDbyte <<= 8;
        crcDbyte |= data[firstIndex + 1];
        
        int currentData = firstIndex + 2;
        short temp = 0;
        
        for (int byteCounter = firstIndex; byteCounter < (firstIndex + byteCount - 2); byteCounter++) {
            for (int bitCounter = 0; bitCounter < 8; bitCounter++) {
                if(( crcDbyte & 0x8000 ) == 0x0000) {
                    crcDbyte <<= 1;
                    temp = (short)(0x01 << (7 - bitCounter));
                    if( (data[currentData] & temp) == temp) {
                        crcDbyte |= 0x0001;
                    } else { 
                        crcDbyte &= 0xFFFE;
                    }
                    continue;
                }
                
                crcDbyte <<= 1;
                temp = (short)(0x01 << (7 - bitCounter));
                if((data[currentData] & temp) == temp) {
                    crcDbyte |= 0x0001;
                } else {
                    crcDbyte &= 0xFFFE;
                }
                crcDbyte ^= 0x1021;
            }
            currentData++;
        }
        
        for(int bitCounter = 0; bitCounter < 16; bitCounter++) {
           if((crcDbyte & 0x8000) == 0x0000) {
              crcDbyte <<= 1;
              continue;
           }
           crcDbyte <<= 1;
           crcDbyte ^= 0x1021;
        }
        
        return crcDbyte;
    }
    
    
    /**
     * Transform specified Simply request data into GW message format.
     * @param data request data to transform
     * @return transformed data - message for GW
     */
    public static short[] transformRequestData(short[] data) {
        checkRequest(data);
        short[] transformedData = new short[HEADER_SIZE + data.length + CRC_SIZE];
         
        transformedData[GW_ADR_POS] = GW_ADR_VALUE;
        transformedData[CMD_POS] = CMD_WRITE_VALUE;
        transformedData[SUBCMD_POS] = SUBCMD_WRITE_VALUE;
        
        System.arraycopy(data, 0, transformedData, HEADER_SIZE, data.length);
        
        transformedData[DLEN_H_POS] = (short)((data.length & 0xFF00) >> 8);
        transformedData[DLEN_L_POS] = (short)(data.length & 0xFF);
        
        int crc = calculateCRC_microrisc(transformedData, 0, HEADER_SIZE + data.length);
        
        transformedData[HEADER_SIZE + data.length] = (short)((crc & 0xFF00) >> 8);
        transformedData[HEADER_SIZE + data.length+1] = (short)(crc & 0xFF);
        
        return transformedData;
    }
    
    /**
     * Returns {@code true} if the specified message is asynchrounous message.
     * @param message message to check for asynchronity
     * @return {@code true} if the specified message is asynchrounous message <br>
     *         {@code false} otherwise
     */
    public static boolean isAsynchronousMessage(short[] message) {
        checkMessageFromGW(message);
        return (message[CMD_POS] == CMD_ASYNC_VALUE);
    } 
    
    /**
     * Returns {@code true} if the specified message has answer indication, i.e.
     * bit 7 of CMD is set.
     * @param message message to check
     * @return {@code true} if the specified message has answer indication <br>
     *         {@code false} otherwise
     */
    public static boolean hasAnswerIndication(short[] message) {
        checkMessageFromGW(message);
        return ((message[CMD_POS] & CMD_ANSWER_INDICATION) == CMD_ANSWER_INDICATION);  
    }
    
    /**
     * Returns DATA part of specified GW message. 
     * @param messageFromGW source message from GW
     * @return DATA part of specified GW message
     */
    public static short[] getDataFromMessage(short[] messageFromGW) {
        checkMessageFromGW(messageFromGW);
        
        int dataSize = getDataSizeField(messageFromGW);
        if (dataSize == 0) {
            return new short[0];
        }
        
        short[] data = new short[dataSize];
        System.arraycopy(messageFromGW, HEADER_SIZE, data, 0, dataSize);
        return data;
    }
}
