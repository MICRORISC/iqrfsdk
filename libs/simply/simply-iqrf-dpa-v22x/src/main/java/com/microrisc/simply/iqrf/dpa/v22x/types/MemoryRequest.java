/*
 * Copyright 2015 MICRORISC s.r.o..
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
package com.microrisc.simply.iqrf.dpa.v22x.types;

/**
 * Encapsulates individual memory request within a FRC command.
 *
 * @author Martin Strouhal
 */
public class MemoryRequest {

    private final int memoryAddress, pnum, pcmd, length;
    private final short[] data;

    private int checkMemoryAddress(int memoryAddress) {
        if ((memoryAddress < 0) || (memoryAddress > 0xFFFF)) {
            throw new IllegalArgumentException(
                    "Memory address cannot be less then 0 or greather then 0xFFFF"
            );
        }
        return memoryAddress;
    }

    private int checkValue(String name, int value) {
        if ((value < 0) || (value > 0xFF)) {
            throw new IllegalArgumentException(
                    name + " cannot be less then 0 or greather then 0xFF"
            );
        }
        return value;
    }
    
    private short[] checkData(short [] data, int lengthByUser){
        if ( data == null ) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(data.length != lengthByUser){
            throw new IllegalArgumentException("Data may not have a different length then is user.");
        }
        return data;
    }

    public MemoryRequest(int memoryAddress, int pnum, int pcmd, int length, short[] data) {
        this.memoryAddress = checkMemoryAddress(memoryAddress);
        this.pnum = checkValue("Peripheral number", pnum);
        this.pcmd = checkValue("Peripheral command", pcmd);
        this.length = checkValue("Data length", length);
        this.data = checkData(data, length);
    }

    public int getMemoryAddress() {
        return memoryAddress;
    }

    public int getPnum() {
        return pnum;
    }

    public int getPcmd() {
        return pcmd;
    }

    public int getLength() {
        return length;
    }

    public short[] getData() {
        return data;
    }
}
