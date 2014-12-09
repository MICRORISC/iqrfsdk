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
 * SPI_CMD.
 * 
 * @author Michal Konopa
 */
public class SPI_Command {
    private final int commandType;
    
    private SPI_Command(int commandType) {
        this.commandType = commandType;
    }

    public int getValue() {
        return commandType;
    }
    
    public static final SPI_Command SPI_CHECK = new SPI_Command(0x00);
    public static final SPI_Command DATA_READ_WRITE = new SPI_Command(0xF0);
    public static final SPI_Command GET_TR_MODULE_INFO = new SPI_Command(0xF5);
    public static final SPI_Command DATA_WRITE = new SPI_Command(0xFA);
}
