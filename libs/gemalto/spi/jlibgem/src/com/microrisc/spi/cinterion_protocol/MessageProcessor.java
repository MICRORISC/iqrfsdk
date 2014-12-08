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

package com.microrisc.spi.cinterion_protocol;

/**
 * Message processor.
 * 
 * @author Michal Konopa
 */
public final class MessageProcessor {
    /** Special character sent to the SPI device to start sending. */
    private static final char START_TRANSFER_MESSAGE = '<';
    
    /** Special character sent to the SPI device to start sending. */
    private static final char STOP_TRANSFER_MESSAGE = '>';
    
    /** Special character sent to the SPI aplication to mark the beginning of a response message. */
    private static final char START_REPONSE_MESSAGE = '{';
    
    /** Special character sent to the SPI aplication to mark the end of a response message. */
    private static final char STOP_RESPONSE_MESSAGE = '}';
    
    /** 
     * Reports to the SPI aplication that the transfer frame does not comply 
     * with the protocol definition (syntax error). 
     */
    private static final char PROTOCOL_ERROR = '!';
    
    /** 
     * Notifies the SPI application:
     * data were successfully transmitted
     * the SPI slave address was recognized
     */
    private static final char TRANSMISSION_OK = '+';
    
    // positions in a message
    private static final int START_MESSAGE_POS = 0;
    private static final int MESSAGE_ID_POS = 1;
    private static final int OPER_RESULT_POS = 2;
    private static final int READ_DATA_POS = 3;
    private static final int FAULTY_BYTE_POS = 3;
    private static final int FAULTY_BYTE_LENGTH = 2;
    
    
    // returns string representation of specified data to transfer them into SPI device
    private static String getSpiDataString(byte[] data) {
        StringBuffer strBuffer = new StringBuffer();
        for ( int dataId = 0; dataId < data.length; dataId++ ) {
            String hexByteStr = Integer.toHexString(data[dataId] & 0xFF);
            if ( hexByteStr.length() == 1 ) {
                hexByteStr = "0" + hexByteStr;
            }
            strBuffer.append(hexByteStr);
        }
        return strBuffer.toString();
    }
    
    private static String getSpiDataString(byte data) {
        StringBuffer strBuffer = new StringBuffer();
        String hexByteStr = Integer.toHexString(data & 0xFF);
        if ( hexByteStr.length() == 1 ) {
                hexByteStr = "0" + hexByteStr;
        }
        strBuffer.append(hexByteStr);
        
        return strBuffer.toString();
    }
    
    // computes and returs integer value from specified hex character
    private static short computeValueFromHex(byte hex) {
        if ( (hex >= '0') && (hex <= '9') ) {
            return (short)(hex - '0');
        }
        
        if ( (hex >= 'A') && (hex <= 'F') ) {
            return (short)( 10 + (hex - 'A'));
        }
        
        if ( (hex >= 'a') && (hex <= 'f') ) {
            return (short)( 10 + (hex - 'a'));
        }
        
        throw new IllegalArgumentException("Invalid hex character: " + hex);
    }
    
    // returns user data from specified response data
    // input data are in hex string format - see documentation of com.cinterion.io.SpiConnection interface
    private static short[] getUserData(byte[] responseData) {
        // checking of user data length
        int userDataLen = responseData.length - READ_DATA_POS - 1;
        if ( userDataLen % 2 != 0 ) {
            throw new IllegalArgumentException(
                    "Number of characteres, which represent data must be even. "
                    + "Number of characters: " + userDataLen
            );
        }
        
        short[] userData = new short[ ( responseData.length - READ_DATA_POS - 1 ) / 2];
        for ( int dataId = 0, respPos = READ_DATA_POS; dataId < userData.length; dataId++, respPos +=2 ) 
        {
            userData[dataId] = (short) (16 * computeValueFromHex(responseData[respPos]) 
                    + computeValueFromHex(responseData[respPos+1]));
        }
        return userData;
    }
    
    // returns faulty byte
    private static int getFaultyByte(byte[] spiData) {
        return  ((spiData[FAULTY_BYTE_POS+1] & 0xFF) + ((spiData[FAULTY_BYTE_POS] & 0xFF) << 8)) ;
    }
    
    /**
     * Creates and returns transfer message.
     * @param msgId message ID
     * @param userData user data to transfer
     * @param readOffset read offset
     * @param readLength length of data to read
     * @return transfer message
     */
    public static byte[] createTransferMessage(
            char msgId, byte[] userData, int readOffset, int readLength
    ) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(MessageProcessor.START_TRANSFER_MESSAGE);
        strBuffer.append(msgId);
        
        // append read offset - 8 bits
        String readOffsetStr = Integer.toHexString(readOffset);
        readOffsetStr = (readOffsetStr.length() == 1)? ("0" + readOffsetStr) : readOffsetStr;  
        strBuffer.append(readOffsetStr);
        
        // append read length - 16 bits
        strBuffer.append("00");
        String readLengthStr = Integer.toHexString(readLength);
        readLengthStr = (readLengthStr.length() == 1)? ("0" + readLengthStr) : readLengthStr;  
        strBuffer.append(readLengthStr);
        
        // append user data
        String userDataStr = getSpiDataString(userData);
        strBuffer.append(userDataStr);
        
        strBuffer.append(MessageProcessor.STOP_TRANSFER_MESSAGE);
        return strBuffer.toString().getBytes();
    }
    
        public static byte[] createTransferMessage(
            char msgId, byte userData, int readOffset, int readLength
    ) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(MessageProcessor.START_TRANSFER_MESSAGE);
        strBuffer.append(msgId);
        
        // append read offset - 8 bits
        String readOffsetStr = Integer.toHexString(readOffset);
        readOffsetStr = (readOffsetStr.length() == 1)? ("0" + readOffsetStr) : readOffsetStr;  
        strBuffer.append(readOffsetStr);
        
        // append read length - 16 bits
        strBuffer.append("00");
        String readLengthStr = Integer.toHexString(readLength);
        readLengthStr = (readLengthStr.length() == 1)? ("0" + readLengthStr) : readLengthStr;  
        strBuffer.append(readLengthStr);
        
        // append user data
        String userDataStr = getSpiDataString(userData);
        strBuffer.append(userDataStr);
        
        strBuffer.append(MessageProcessor.STOP_TRANSFER_MESSAGE);
        return strBuffer.toString().getBytes();
    }
    
    /**
     * Parses specified SPI response data and returns a result of this parsing
     * process.
     * @param responseData response data to parse
     * @return result of parsing of {@code responseData}
     */
    public static ResponseDataParsingResult parseResponseData(byte[] responseData) {
        if ( responseData.length < WriteOkResponseMessage.LENGTH ) {
            
            return new ResponseDataParsingResult(
                    ResponseDataParsingResult.ParsingResultType.INCOMPLETE, null
            );
        }
        
        int[] dataInt = new int[ responseData.length ];
        for ( int dataId = 0; dataId < dataInt.length; dataId++) {
            dataInt[dataId] = responseData[dataId] & 0xFF;
        }
        
        if ( dataInt[START_MESSAGE_POS] != START_REPONSE_MESSAGE ) {
            
            return new ResponseDataParsingResult(
                    ResponseDataParsingResult.ParsingResultType.FORMAT_ERROR, null
            );
        }
        
        if ( dataInt[dataInt.length-1] != STOP_RESPONSE_MESSAGE ) {
            
            return new ResponseDataParsingResult(
                    ResponseDataParsingResult.ParsingResultType.INCOMPLETE, null
            );
        }
        
        if ( (dataInt[OPER_RESULT_POS] != TRANSMISSION_OK) && (dataInt[OPER_RESULT_POS] != PROTOCOL_ERROR) ) {
            
            return new ResponseDataParsingResult(
                    ResponseDataParsingResult.ParsingResultType.FORMAT_ERROR, null
            ); 
        }
        
        char msgId = (char)responseData[MESSAGE_ID_POS];
        
        // transfer OK
        if ( dataInt[OPER_RESULT_POS] == TRANSMISSION_OK ) {
            if ( dataInt.length == WriteOkResponseMessage.LENGTH ) {
                
                return new ResponseDataParsingResult(
                    ResponseDataParsingResult.ParsingResultType.OK,
                    new WriteOkResponseMessage(msgId)    
                );
            }
            
            
            short[] userData = null;
            try {
                userData = getUserData(responseData);
            } catch ( Exception e ) {
                
                return new ResponseDataParsingResult(
                    ResponseDataParsingResult.ParsingResultType.FORMAT_ERROR, null
                );
            }
            
            return new ResponseDataParsingResult(
                    ResponseDataParsingResult.ParsingResultType.OK,
                    new ReadingOkResponseMessage(msgId, userData)    
            );
        }
        
        // transfer error
        if ( dataInt.length != (WriteOkResponseMessage.LENGTH + FAULTY_BYTE_LENGTH) ) {
            
            // missing 16 bit code of error byte or some other bytes are present
            return new ResponseDataParsingResult(
                    ResponseDataParsingResult.ParsingResultType.FORMAT_ERROR, null
            );
        }
        
        // protocol error
        return new ResponseDataParsingResult(
            ResponseDataParsingResult.ParsingResultType.OK,
            new ProtocolErrorResponseMessage(msgId, getFaultyByte(responseData))    
        );
    }
}
