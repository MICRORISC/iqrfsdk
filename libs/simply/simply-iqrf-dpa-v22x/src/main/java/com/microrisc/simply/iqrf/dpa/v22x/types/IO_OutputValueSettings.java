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
 * Encapsulates information about IO output value settings.
 * 
 * @author Michal Konopa
 */
public final class IO_OutputValueSettings implements IO_Command {
    /** Port to setup an output state. 0=PORTA, 1=PORTB, … */
    private final int port;
    
    /** Masks pins of the port to setup. */
    private final int mask;
    
    /** Actual output bit value for the masked pins. */
    private final int value;
    
    
    /**
     * Creates new objects encapsulating IO output value settings.
     * @param port port to setup an output state. 0=PORTA, 1=PORTB, ...
     * @param mask masks pins of the port to setup.
     * @param value actual output bit value for the masked pins.
     */
    public IO_OutputValueSettings(int port, int mask, int value) {
        this.port = port;
        this.mask = mask;
        this.value = value;
    }

    /**
     * @return Port to setup an output state. 0=PORTA, 1=PORTB, …
     */
    public int getPort() {
        return port;
    }

    /**
     * @return Masks pins of the port to setup.
     */
    public int getMask() {
        return mask;
    }

    /**
     * @return Actual output bit value for the masked pins.
     */
    public int getValue() {
        return value;
    }

    @Override
    public int getFirstField() {
        return port;
    }

    @Override
    public int getSecondField() {
        return mask;
    }

    @Override
    public int getThirdField() {
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Port: " + port + NEW_LINE);
        strBuilder.append(" Mask: " + mask + NEW_LINE);
        strBuilder.append(" Value: " + value + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
