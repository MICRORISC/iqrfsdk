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
 * Response packet, which is sent by IQRF module as a response to READ/WRITE a 
 * request packet from SPI master.
 * Details can be found in document: SPI. Implementation in IQRF TR modules. User Guide.
 * <a href="http://www.iqrf.cz/weben/downloads.php?id=85">SPI in IQRF TR modules</a>  
 * on page 5.
 * 
 * @author Michal Konopa
 */
public final class ReadWriteResponse extends AbstractResponsePacket {
    private final SPI_Status firstSpiStatus;
    private final SPI_Status secondSpiStatus;
    private final short[] data;
    private final short crcs;
    
    
    private SPI_Status checkSpiStatus(SPI_Status spiStatus) {
        if ( spiStatus == null ) {
            throw new IllegalArgumentException("SPI status cannot be null");
        }
        return spiStatus;
    }
    
    private short[] checkData(short[] data) {
        if ( data == null ) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return data;
    }
   
    
    /**
     * Counts and returns total response length.
     * @param dataLen length of data part 
     * @return total response length
     */
    public static int countLength(int dataLen) {
        return dataLen + 3;
    }
    
    /**
     * Creates new Read Write response packet.
     * @param firstSpiStatus first item of SPI status
     * @param secondSpiStatus second item of SPI status
     * @param data data
     * @param crcs CRCS
     * @throws IllegalArgumentException if {@code firstSpiStatus}, 
     *             {@code secondSpiStatus} or {@code data }is {@code null}
     */
    public ReadWriteResponse(
            SPI_Status firstSpiStatus, SPI_Status secondSpiStatus, short[] data, 
            short crcs
    ) throws CRC_VerificationException {
        this.firstSpiStatus = checkSpiStatus(firstSpiStatus);
        this.secondSpiStatus = checkSpiStatus(secondSpiStatus);
        checkData(data);
        this.data = new short[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
        this.crcs = crcs;
    }

    /**
     * @return the first SPI status
     */
    public SPI_Status getFirstSpiStatus() {
        return firstSpiStatus;
    }

    /**
     * @return the second SPI status
     */
    public SPI_Status getSecondSpiStatus() {
        return secondSpiStatus;
    }

    /**
     * @return the data
     */
    public short[] getData() {
        return data;
    }

    /**
     * @return the CRCS
     */
    public short getCrcs() {
        return crcs;
    }
    
    public short computeCRCS(ReadWriteRequest.PacketType ptype) {
        short compCrcs = (short)((ptype.getCommType().getValue() << 7) + ptype.getDataLength());
        for ( int byteId = 0; byteId < data.length; byteId++ ) {
            compCrcs ^= data[byteId];
        }
        compCrcs ^= 0x5F;
        return compCrcs;
    }
}
