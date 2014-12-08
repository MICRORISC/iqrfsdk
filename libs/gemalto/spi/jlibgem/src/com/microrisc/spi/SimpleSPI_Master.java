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

package com.microrisc.spi;

import com.cinterion.io.SpiConnection;
import com.microrisc.spi.cinterion_protocol.AbstractResponseMessage;
import com.microrisc.spi.cinterion_protocol.MessageProcessor;
import com.microrisc.spi.cinterion_protocol.ProtocolErrorResponseMessage;
import com.microrisc.spi.cinterion_protocol.ReadingOkResponseMessage;
import com.microrisc.spi.cinterion_protocol.ResponseDataParsingResult;
import com.microrisc.spi.cinterion_protocol.WriteOkResponseMessage;
import com.microrisc.spi.iqrf_protocol.AbstractResponsePacket;
import com.microrisc.spi.iqrf_protocol.CheckSPI_StatusRequest;
import com.microrisc.spi.iqrf_protocol.CheckSPI_StatusResponse;
import com.microrisc.spi.iqrf_protocol.ReadWriteRequest;
import com.microrisc.spi.iqrf_protocol.ReadWriteResponse;
import com.microrisc.spi.iqrf_protocol.ResponsePacketParseException;
import com.microrisc.spi.iqrf_protocol.ResponseParser;
import com.microrisc.spi.iqrf_protocol.SPI_Status;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;

/**
 * Simple SPI Master implementation.
 * 
 * @author Michal Konopa
 */
final class SimpleSPI_Master implements SPI_Master {
    private static final String SPI_PREFIX = "spi";
    private static final char SPI_PREFIX_SEPARATOR = ':';
    private static final char PARAM_SEPARATOR = ';';
    
    private final String devId;
    
    // connection parameters
    private BaudRate baudRate;
    
    // used SPI connection
    private final SpiConnection spiConnection;
    
    // streams used to I/O operations
    private final InputStream inStream;
    private final OutputStream outStream;
    
    
    // starting value of message ID
    private static final char MESSAGE_ID_START_VALUE = 0x00; 
    
    // ending value of message ID
    private static final char MESSAGE_ID_END_VALUE = 0x7F;
    
    // current value of message ID
    private char messageId = MESSAGE_ID_START_VALUE;
    
    
    /** Default response timeout. */
    public static final long DEFAULT_RESPONSE_TIMEOUT = 5000;
    
    // actual value of the response timeout
    private long responseTimeout = DEFAULT_RESPONSE_TIMEOUT;
    
    private static long checkResponseTimeout(long responseTimeout) {
        if ( responseTimeout < 0 ) {
            throw new IllegalArgumentException("Response timeout cannot be less then 0.");
        }
        return responseTimeout;
    }
    
    
    private static String checkDeviceId(String devId) {
        if ( devId == null ) {
            throw new IllegalArgumentException("Device ID cannot be null.");
        }
        
        if ( devId.length() == 0 ) {
            throw new IllegalArgumentException("Device ID cannot be empty.");
        }
        return devId;
    }
    
    private static BaudRate checkBaudRate(BaudRate baudRate) {
        if ( baudRate == null ) {
            throw new IllegalArgumentException("Baud rate cannot be null.");
        }
        return baudRate;
    }
    
    private static short[] checkDataToSend(short[] dataToSend) {
        if ( dataToSend == null ) {
            throw new IllegalArgumentException("Data to send cannot be null.");
        }
        if ( dataToSend.length == 0 ) {
            throw new IllegalArgumentException("Data to send cannot be empty.");
        }
        return dataToSend;
    }
    
    private static int checkLengthOfDataToRead(int dataLen) {
        if ( dataLen <= 0 ) {
            throw new IllegalArgumentException("Length of data to read must be positive integer.");
        }
        return dataLen;
    }
    
    
    // error of lastly called SPI request 
    private SPI_Error lastError = null;
    
    
    // combines read data with a result of a reading 
    private class ReadResult {
        ResponseDataParsingResult result;
        SPI_Error error;
        
        public ReadResult(ResponseDataParsingResult result, SPI_Error error) {
            this.result = result;
            this.error = error;
        }
    }
    
    
    // creates and returns GCF String for usage of specified SPI connection parameters
    // inside Connector factory open() method
    private String getGCF_String(String devId, BaudRate baudRate) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(SPI_PREFIX);
        strBuffer.append(SPI_PREFIX_SEPARATOR);
        strBuffer.append(devId);
        strBuffer.append(PARAM_SEPARATOR);
        strBuffer.append("baudrate=");
        strBuffer.append(baudRate.getValue());
        strBuffer.append(PARAM_SEPARATOR);
        strBuffer.append("clockMode=0");
        System.out.println("CFG srting = " + strBuffer.toString());
        return strBuffer.toString();
    }
    
    private void updateBaudRate() {
        int baudRateInt = spiConnection.getBaudRate();
        if ( baudRateInt == BaudRate.BaudRate_100.getValue() ) {
            this.baudRate = BaudRate.BaudRate_100;
        } else if ( baudRateInt == BaudRate.BaudRate_250.getValue() ) {
            this.baudRate = BaudRate.BaudRate_250;
        } else {
            throw new IllegalStateException("Unknown baud rate.");
        }
    }
    
    // updates SPI connection parameters according to SpiConnection object
    private void updateConnectionParameters() {
        updateBaudRate();
    }
    
    // increments current value of message ID
    private void incrementMessageId() {
        if ( messageId == MESSAGE_ID_END_VALUE ) {
            messageId = MESSAGE_ID_START_VALUE;
        } else {
            messageId++;
        }
    }
    
    // transfers specified byte to the connected SPI device
    private void transferByte(byte data, int readOffset, int readLength) throws IOException {
        incrementMessageId();
        lastError = null;
        
        byte[] transferMessage = MessageProcessor.createTransferMessage(
                messageId, data, readOffset, readLength
        );
        
        outStream.write(transferMessage, 0, transferMessage.length);
        outStream.flush();
    }
    
    // transfers specified data to the connected SPI device
    private void transferData(byte[] data, int readOffset, int readLength) 
            throws IOException 
    {
        incrementMessageId();
        lastError = null;
        
        byte[] transferMessage = MessageProcessor.createTransferMessage(
                messageId, data, readOffset, readLength
        );
                
        outStream.write(transferMessage, 0, transferMessage.length);
        outStream.flush();
    }
    
    // reads a response data
    private ReadResult readResponseData(long responseTimeout, int bufferLength) {
        byte[] responseBuffer = new byte[bufferLength];
        int buffPos = 0;
        
        long startWaitingTime = System.currentTimeMillis();
        long elapsedTime = 0;
        ResponseDataParsingResult result = null;
        SPI_Error readError = null;
        
        while ( elapsedTime < responseTimeout ) {
            try {
                if ( inStream.available() <= 0 ) {
                    elapsedTime = System.currentTimeMillis() - startWaitingTime;
                    continue;
                }
            } catch ( IOException ex ) {
                readError = new SPI_Error(
                        SPI_ErrorType.IO_ERROR, 
                        "Could not discover available data. Response data: " + responseBuffer,
                        ex
                );
                break;
            }
            
            if ( buffPos >= responseBuffer.length ) {
                readError = new SPI_Error(
                        SPI_ErrorType.IO_ERROR, 
                        "Response buffer overflowed. Response data: " + responseBuffer
                );
                break;
            }
            
            int readBytes = 0;
            try {
                readBytes = inStream.read(responseBuffer, buffPos, responseBuffer.length - buffPos);
            } catch ( IOException ex ) {
                readError = new SPI_Error(
                        SPI_ErrorType.IO_ERROR, 
                        "Reading response data failed. Response data: " + responseBuffer,
                        ex
                );
                break;
            }
            
            result = MessageProcessor.parseResponseData(responseBuffer);
            if ( result.getResult() != ResponseDataParsingResult.ParsingResultType.INCOMPLETE ) {
                break;
            }
            
            buffPos += readBytes;
            System.out.print("buffer position: " + buffPos);
            
            elapsedTime = System.currentTimeMillis() - startWaitingTime; 
        }
        
        return new ReadResult(result, readError);
    }
    
    /**
     * Creates new object of SPI Master with specified device identifier and
     * baud rate. Clock mode will be set to 0.
     * @param devId device identifier to use
     * @param baudRate baud rate to use
     * @throws IllegalArgumentException if {@code devId} is {@code null} or an empty String
     * @throws IllegalArgumentException if {@code baudRate} is {@code null}
     * @throws IOException if an I/O error occurs
     */
    public SimpleSPI_Master(String devId, BaudRate baudRate) throws IOException {
        this.devId = checkDeviceId(devId);
        this.baudRate = checkBaudRate(baudRate);
        this.spiConnection = (SpiConnection) Connector.open( getGCF_String(devId, baudRate) );
        updateConnectionParameters();
        this.inStream  = spiConnection.openInputStream();
        this.outStream = spiConnection.openOutputStream();
        this.messageId = MESSAGE_ID_START_VALUE;
    }
    
    public BaudRate getBaudRate() {
        return baudRate;
    }
    
    /**
     * @throws IllegalArgumentException if {@code responseTimeout} is less then 0 
     */
    public void setDefaultResponseTimeout(long responseTimeout) {
        this.responseTimeout = checkResponseTimeout(responseTimeout);
    }
    
    public long getDefaultResponseTimeout() {
        return responseTimeout;
    }
    
    public SPI_Status getSlaveStatus() {
        return getSlaveStatus(responseTimeout);
    }
    
    /**
     * @throws IllegalArgumentException if {@code responseTimeout} is less then 0 
     */
    public SPI_Status getSlaveStatus(long responseTimeout) {
        checkResponseTimeout(responseTimeout);
        
        try {
            transferData( 
                    new CheckSPI_StatusRequest().serialize(), 0, 
                    CheckSPI_StatusResponse.LENGTH 
            );
        } catch ( IOException ex ) {
            lastError = new SPI_Error(SPI_ErrorType.IO_ERROR, "Getting slave status failed", ex);
            return null;
        }
        
        // get a response
        ReadResult readResult = readResponseData(
                responseTimeout, WriteOkResponseMessage.LENGTH + CheckSPI_StatusResponse.LENGTH * 2
        );
 
        if ( readResult.error != null ) {
            lastError = readResult.error;
            return null;
        }
        
        ResponseDataParsingResult parsingResult = readResult.result;
        if ( parsingResult.getResult() == ResponseDataParsingResult.ParsingResultType.FORMAT_ERROR 
             || parsingResult.getResult() == ResponseDataParsingResult.ParsingResultType.INCOMPLETE   
        ) {
            lastError = new SPI_Error(SPI_ErrorType.FORMAT_ERROR);
            return null;
        }
        
        // parsing of read data was OK
        AbstractResponseMessage response = parsingResult.getResponse();
        
        // checking of message ID
        if ( response.getId() != messageId ) {
            lastError = new SPI_Error(
                    SPI_ErrorType.INCORRECT_MESSAGE_ID, 
                    "Incomming message ID: " + response.getId() + ", expected: " + messageId  
            );
            return null;
        }
        
        if ( response instanceof ProtocolErrorResponseMessage ) {
            lastError = new SPI_Error(
                    SPI_ErrorType.PROTOCOL_ERROR, 
                    "Faulty byte: " + ((ProtocolErrorResponseMessage)response).getFaultyByte() 
            );
            return null;
        }
        
        if ( response instanceof WriteOkResponseMessage ) {
            lastError = new SPI_Error(
                    SPI_ErrorType.BAD_DATA, "No data received." 
            );
            return null;
        }
        
        // retrieving incomming IQRF data
        short[] userData = ((ReadingOkResponseMessage)response).getReadData();
        AbstractResponsePacket iqrfResponse = null;
        try {
            iqrfResponse = ResponseParser.getInstance().parse(userData);
        } catch ( ResponsePacketParseException ex ) {
            lastError = new SPI_Error(
                     SPI_ErrorType.BAD_DATA, 
                     "Parsing IQRF packet exception: " + ex
             );
            return null; 
        }
        
        return ((CheckSPI_StatusResponse)iqrfResponse).getSpiStatus();
    }
    
    public VoidType sendData(short[] data) {
        return sendData(data, responseTimeout);
    }
    
    /**
     * @throws IllegalArgumentException if {@code data} is {@code null}, or its length is 0 <br> 
     *                                  if {@code responseTimeout} is less then 0 
     */
    public VoidType sendData(short[] data, long responseTimeout) {
        checkDataToSend(data);
        checkResponseTimeout(responseTimeout);  
        
        byte[] dataToWrite = new ReadWriteRequest( 
                ReadWriteRequest.SPI_Command.DATA_READ_WRITE,
                new ReadWriteRequest.PacketType(
                    data.length, 
                    ReadWriteRequest.PacketType.CommunicationType.BUFFER_COM_CHANGED
                ),
                data
        ).serialize(); 
        
        // because of Gemalto x Microrisc SPI limitation, bytes to write must be transfered one by one
        for ( int i = 0; i < dataToWrite.length; i++ ) {
            try {
                transferByte( dataToWrite[i], 0, 0 );
            } 
            catch ( IOException ex ) {
                lastError = new SPI_Error(
                        SPI_ErrorType.IO_ERROR, "Writing of data: " + dataToWrite + " failed", ex
                );
                return null;
            }

            // get a response
            ReadResult readResult = readResponseData(responseTimeout, WriteOkResponseMessage.LENGTH);
            if ( readResult.error != null ) {
                lastError = readResult.error;
                return null;
            }

            ResponseDataParsingResult parsingResult = readResult.result;
            if ( parsingResult.getResult() == ResponseDataParsingResult.ParsingResultType.FORMAT_ERROR
                 || parsingResult.getResult() == ResponseDataParsingResult.ParsingResultType.INCOMPLETE
            ) {
                lastError = new SPI_Error(SPI_ErrorType.FORMAT_ERROR);
                return null;
            }

            // parsing of read data was OK
            AbstractResponseMessage response = parsingResult.getResponse();

            // checking of message ID
            if ( response.getId() != messageId ) {
                lastError = new SPI_Error(
                        SPI_ErrorType.INCORRECT_MESSAGE_ID,
                        "Incomming message ID: " + response.getId() + ", expected: " + messageId
                );
                return null;
            }

            if ( response instanceof ProtocolErrorResponseMessage ) {
                lastError = new SPI_Error(
                        SPI_ErrorType.PROTOCOL_ERROR,
                        "Faulty byte: " + ((ProtocolErrorResponseMessage) response).getFaultyByte()
                );
                return null;
            }

            if ( response instanceof ReadingOkResponseMessage ) {
                lastError = new SPI_Error(
                        SPI_ErrorType.BAD_DATA,
                        "Unexpected bytes read: " + ((ReadingOkResponseMessage) response).getReadData()
                );
                return null;
            }
        }
        
        // all has gone allright
        return new VoidType();
    }
    
    public short[] readData(int dataLen) {
        return readData(dataLen, responseTimeout);
    }
    
    /**
     * @throws IllegalArgumentException if {@code dataLen} is not positive <br>
     *                                  if {@code responseTimeout} is less then 0 
     */
    public short[] readData(int dataLen, long responseTimeout) {
        checkLengthOfDataToRead(dataLen);
        checkResponseTimeout(responseTimeout); 
        
        ReadWriteRequest.PacketType ptype = new ReadWriteRequest.PacketType(
                dataLen, ReadWriteRequest.PacketType.CommunicationType.BUFFER_COM_UNCHANGED
        );
        
        byte[] dataToRead = new ReadWriteRequest(
                ReadWriteRequest.SPI_Command.DATA_READ_WRITE, ptype
        ).serialize();
        
        short[] userDataComplete = new short[dataToRead.length];
        
        // because of Gemalto x Microrisc SPI limitation, bytes to read must be transfered one by one
        for ( int i = 0; i < dataToRead.length; i++ ) {
            try {
                transferByte(dataToRead[i], 0, 1);
            } catch ( IOException ex ) {
                lastError = new SPI_Error(SPI_ErrorType.IO_ERROR, "Reading of data failed", ex);
                return null;
            }

            // get a response
            // reading 6B {0+two_ascii_bytes_as_one_hex_number}
            ReadResult readResult = readResponseData(responseTimeout, 6);
            if ( readResult.error != null ) {
                lastError = readResult.error;
                return null;
            }

            ResponseDataParsingResult parsingResult = readResult.result;
            if ( parsingResult.getResult() == ResponseDataParsingResult.ParsingResultType.FORMAT_ERROR
                 || parsingResult.getResult() == ResponseDataParsingResult.ParsingResultType.INCOMPLETE
            ) {
                lastError = new SPI_Error(SPI_ErrorType.FORMAT_ERROR);
                return null;
            }

            // parsing of read data was OK
            AbstractResponseMessage response = parsingResult.getResponse();

            // checking of message ID
            if ( response.getId() != messageId ) {
                lastError = new SPI_Error(
                        SPI_ErrorType.INCORRECT_MESSAGE_ID,
                        "Incomming message ID: " + response.getId() + ", expected: " + messageId
                );
                return null;
            }

            if ( response instanceof ProtocolErrorResponseMessage ) {
                lastError = new SPI_Error(
                        SPI_ErrorType.PROTOCOL_ERROR,
                        "Faulty byte: " + ((ProtocolErrorResponseMessage) response).getFaultyByte()
                );
                return null;
            }

            if ( response instanceof WriteOkResponseMessage ) {
                lastError = new SPI_Error(
                        SPI_ErrorType.BAD_DATA, "No data read"
                );
                return null;
            }

            short[] userData = ((ReadingOkResponseMessage) response).getReadData();        
            userDataComplete[i] = userData[0];
        }
            
        AbstractResponsePacket iqrfResponse = null;
        try {
            iqrfResponse = ResponseParser.getInstance().parse(userDataComplete);
        } catch (ResponsePacketParseException ex) {
            lastError = new SPI_Error(
                    SPI_ErrorType.BAD_DATA,
                    "Parsing IQRF packet data exception: " + ex.toString()
            );
            return null;
        }

        if ( !(iqrfResponse instanceof ReadWriteResponse) ) {
            lastError = new SPI_Error(
                    SPI_ErrorType.BAD_DATA,
                    "Bad incomming IQRF packet. Read/write response packet expected."
            );
            return null;
        }

        ReadWriteResponse rwResponse = (ReadWriteResponse) iqrfResponse;

        // checking CRCS
        short computedCrcs = rwResponse.computeCRCS( 
                new ReadWriteRequest.PacketType(
                    dataLen, ReadWriteRequest.PacketType.CommunicationType.BUFFER_COM_UNCHANGED
                )
        );
        if ( computedCrcs != rwResponse.getCrcs() ) {
            lastError = new SPI_Error(
                    SPI_ErrorType.BAD_DATA,
                    "CRCS mismatch. Computed = " + computedCrcs + ", get: " + rwResponse.getCrcs()
            );
            return null;
        }

        return rwResponse.getData();
    }
    
    public void destroy() throws IOException {
        inStream.close();
        outStream.close();
        spiConnection.close();
        lastError = null;
    }   

    public SPI_Error getLastError() {
        return lastError;
    }

}
