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
 * Encapsulates information about IO delay settings.
 * 
 * @author Michal Konopa
 */
public final class IO_DelaySettings implements IO_Command {
    /** Delay value [in ms]. */
    private final int delay;
    
   
    /**
     * Creates new objects encapsulating IO delay value settings.
     * @param delay delay value [in ms]
     */
    public IO_DelaySettings(int delay) {
        this.delay = delay;
    }

    /**
     * @return delay [in ms]
     */
    public int getDelay() {
        return delay;
    }

    @Override
    public int getFirstField() {
        return 0xFF;
    }

    @Override
    public int getSecondField() {
        return (delay & 0xFF);
    }

    @Override
    public int getThirdField() {
        return ((delay & 0xFF00) >> 8);
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Delay: " + delay + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
