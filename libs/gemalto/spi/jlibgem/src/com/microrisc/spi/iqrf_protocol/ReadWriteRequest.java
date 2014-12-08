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
 * Request packet, which is sent by SPI master to READ/WRITE a packet 
 * from/to the module.
 * Details can be found in document: SPI. Implementation in IQRF TR modules. User Guide.
 * <a href="http://www.iqrf.cz/weben/downloads.php?id=85">SPI in IQRF TR modules</a>  
 * on page 5.
 * 
 * @author Michal Konopa
 */
public final class ReadWriteRequest extends AbstractRequestPacket {
    
    /** SPI_CMD. */
    public static final class SPI_Command {
        private final int commandType;
    
        private SPI_Command(int commandType) {
            this.commandType = commandType;
        }

        public int getValue() {
            return commandType;
        }

        public static final SPI_Command DATA_READ_WRITE = new SPI_Command(0xF0);
        public static final SPI_Command GET_TR_MODULE_INFO = new SPI_Command(0xF5);
        public static final SPI_Command DATA_WRITE = new SPI_Command(0xFA);
    }
    
    /** PTYPE. */
    public static final class PacketType {

        /** SPIDLEN. */
        private final int dataLen;
        
        /** CTYPE. */
        public static final class CommunicationType {
            private final int commType;
            
            private CommunicationType(int commType) {
                this.commType = commType;
            }

            public int getValue() {
                return commType;
            }
            
            public static final CommunicationType BUFFER_COM_CHANGED = new CommunicationType(0x01);
            public static final CommunicationType BUFFER_COM_UNCHANGED = new CommunicationType(0x00);
        }
        
        private final CommunicationType commType;
        
        
        private int checkDataLength(int dataLen) {
            if ( dataLen <= 0 ) {
                throw new IllegalArgumentException("Length of data must be positive integer.");
            }
            return dataLen;
        }
        
        private CommunicationType checkCommunicationType(CommunicationType commType) {
            if ( commType == null ) {
                throw new IllegalArgumentException("Communication type cannot be null.");
            }
            return commType;
        }
        
        /**
         * Creates new PTYPE object.
         * @param dataLen length of data
         * @param commType communication type - CTYPE
         */
        public PacketType(int dataLen, CommunicationType commType ) {
            this.dataLen = checkDataLength(dataLen);
            this.commType = checkCommunicationType(commType);
        }

        /**
         * @return the data length
         */
        public int getDataLength() {
            return dataLen;
        }

        /**
         * @return the communication type
         */
        public CommunicationType getCommType() {
            return commType;
        }
    }
    
    private static final int SPI_COMMAND_POS = 0;
    private static final int PACKET_TYPE_POS = 1;
    private static final int DATA_POS = 2;
    
    private final SPI_Command commandType;
    private final PacketType packetType;
    private final short[] dataToWrite;
    
    private static final int CRCM_NOT_CALCULATED = -1;
    private short crcm = CRCM_NOT_CALCULATED;
    
    
    private static SPI_Command checkCommandType(SPI_Command commandType) {
        if ( commandType == null ) {
            throw new IllegalArgumentException("SPI command type cannot be null");
        }
        return commandType;
    }
    
    private static PacketType checkPacketType(PacketType packetType) {
        if ( packetType == null ) {
            throw new IllegalArgumentException("Packet type cannot be null");
        }
        return packetType;
    }
    
    private static short[] checkDataToWrite(short[] dataToWrite) {
        if ( dataToWrite == null ) {
            throw new IllegalArgumentException("Data to write cannot be null");
        }
        return dataToWrite;
    }
    
    // computes and returns CRCM
    private short computeCRCM() {
        crcm = (short)(commandType.getValue() ^ ((packetType.commType.getValue() << 7) + packetType.dataLen));
        for ( int byteId = 0; byteId < dataToWrite.length; byteId++ ) {
            crcm ^= dataToWrite[byteId];
        }
        crcm ^= 0x5F;
        return crcm;
    }    
    
    /**
     * Creates new read/write request.
     * @param commandType command type - CTYPE
     * @param packetType packet type - PTYPE
     * @throws IllegalArgumentException if {@code commandType} or {@code packetType} is {@code null}
     */
    public ReadWriteRequest(SPI_Command commandType, PacketType packetType) {
        this.commandType = checkCommandType(commandType);
        this.packetType = checkPacketType(packetType);
        this.dataToWrite = new short[packetType.dataLen];
    }
    
    /**
     * Creates new read/write request.
     * PTYPE.SPIDLEN must be equal to greather then {@code dataToWrite} length.
     * If PTYPE.SPIDLEN is greather then {@code dataToWrite} length, the remainder
     * of the packet is filled by zeroes.
     * @param commandType command type - CTYPE
     * @param packetType packet type - PTYPE
     * @param dataToWrite data to write
     * @throws IllegalArgumentException if {@code commandType} or {@code packetType} or 
     *         {@code dataToWrite} is {@code null} <br>
     *         if PTYPE.SPIDLEN is less then {@code dataToWrite} length
     */
    public ReadWriteRequest(
            SPI_Command commandType, PacketType packetType, short[] dataToWrite
    ) {
        this.commandType = checkCommandType(commandType);
        this.packetType = checkPacketType(packetType);
        checkDataToWrite(dataToWrite);
        if ( packetType.dataLen < dataToWrite.length ) {
            throw new IllegalArgumentException(
                    "PTYPE.SPIDLEN cannot be less then data to write length."
            );
        }
        
        int dummyDataLen = packetType.dataLen - dataToWrite.length;
        if ( dummyDataLen > 0 ) {
            this.dataToWrite = new short[packetType.dataLen];
        } else {
            this.dataToWrite = new short[dataToWrite.length];
        }
        System.arraycopy(dataToWrite, 0, this.dataToWrite, 0, dataToWrite.length);
    }
    
    public byte[] serialize() {
        byte[] serData = new byte[DATA_POS + 1 + dataToWrite.length];
        
        serData[SPI_COMMAND_POS] = (byte) commandType.getValue();
        serData[PACKET_TYPE_POS] = (byte)((packetType.commType.getValue() << 7) + packetType.dataLen);
        
        byte[] dataToWriteConverted = new byte[dataToWrite.length];
        for ( int index = 0; index < dataToWrite.length; index++ ) {
            dataToWriteConverted[index] = (byte) dataToWrite[index];
        }
        
        System.arraycopy(dataToWriteConverted, 0, serData, DATA_POS, dataToWriteConverted.length);
        serData[DATA_POS + dataToWriteConverted.length] = (byte)computeCRCM();
        
        return serData;
    }

    /**
     * @return the command type
     */
    public SPI_Command getCommandType() {
        return commandType;
    }

    /**
     * @return the packet type
     */
    public PacketType getPacketType() {
        return packetType;
    }

    /**
     * @return the data to write
     */
    public short[] getDataToWrite() {
        return dataToWrite;
    }
    
    /**
     * @return CRCM
     */
    public short getCRCM() {
        if ( crcm == CRCM_NOT_CALCULATED ) {
            crcm = computeCRCM();
        }
        return crcm;
    }
}
