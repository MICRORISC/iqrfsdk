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

package com.microrisc.simply.iqrf.dpa.v22x.types;

/**
 * Information about Peripheral.
 * 
 * @author Michal Konopa
 */
public final class PeripheralInfo {
    // peripheral type
    private final PeripheralType peripheralType;
    
    // extended peripheral characteristic
    private final ExtPerCharacteristic extPerCharacteristic;
    
    // optional peripheral specific information
    private final short par1;
    
    // optional peripheral specific information
    private final short par2;
    
    
    /**
     * Creates new {@code PeripheralInfo} object.
     * @param perType peripheral type
     * @param extPerCharacteristic extended peripheral characteristic
     * @param par1 optional peripheral specific information
     * @param par2 optional peripheral specific information
     */
    public PeripheralInfo(PeripheralType perType, 
            ExtPerCharacteristic extPerCharacteristic, short par1, short par2
    ) {
        this.peripheralType = perType;
        this.extPerCharacteristic = extPerCharacteristic;
        this.par1 = par1;
        this.par2 = par2;
    }

    /**
     * @return peripheral type
     */
    public PeripheralType getPeripheralType() {
        return peripheralType;
    }

    /**
     * @return extended peripheral characteristic
     */
    public ExtPerCharacteristic getExtPerCharacteristic() {
        return extPerCharacteristic;
    }

    /**
     * @return Par1 - optional peripheral specific information
     */
    public short getPar1() {
        return par1;
    }

    /**
     * @return Par2 - optional peripheral specific information
     */
    public short getPar2() {
        return par2;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Peripheral type: " + peripheralType + NEW_LINE);
        strBuilder.append(" Extended per. characteristic: " + extPerCharacteristic + NEW_LINE);
        strBuilder.append(" Parameter 1: " + par1 + NEW_LINE);
        strBuilder.append(" Parameter 2: " + par2 + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
