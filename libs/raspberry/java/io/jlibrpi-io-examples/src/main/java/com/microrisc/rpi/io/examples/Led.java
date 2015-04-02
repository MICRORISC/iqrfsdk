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

package com.microrisc.rpi.io.examples;

import com.microrisc.rpi.io.IO;
import com.microrisc.rpi.io.IOException;
import com.microrisc.rpi.io.SimpleIO;

/**
 * Led example
 *
 * @author Rostislav Spinar
 */
public class Led {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("LED example");
        
        IO io = null;
        
        try {
            io = new SimpleIO();

            // activate LED
            io.set(IO.Pin.LED, IO.Direction.OUTPUT);

            final int MAX_CYCLES = 10;
            int cycle = 0;
            IO.Level level = IO.Level.LOW;
            
            do {            
                level = ( level == IO.Level.LOW )? IO.Level.HIGH : IO.Level.LOW;
                io.write(IO.Pin.LED, level);
                Thread.sleep(500);
            } while ( ++cycle < MAX_CYCLES );
        } catch (IOException e) {
            System.err.println("Error while working with IO: " + e);
        } finally {
            // termination and resource free up
            if ( io != null ) {
                io.destroy();
            }
        }
    }
}
