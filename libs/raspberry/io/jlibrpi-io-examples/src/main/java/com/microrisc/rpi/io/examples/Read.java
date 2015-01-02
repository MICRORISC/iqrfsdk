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
 * Read example.
 * 
 * @author Rostislav Spinar
 */
public class Read {
 
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Reading IO example");
        
        IO io = null;
        
        try {
            io = new SimpleIO();
      
            // disable power on TR module
            io.set(IO.Port.RESET, IO.Direction.OUTPUT);
            io.write(IO.Port.RESET, IO.Level.HIGH);

            // read IO
            int value = io.read(IO.Port.RESET);
        
            if ( value != 0 ) {
                io.set(IO.Port.LED, IO.Direction.OUTPUT);
                io.write(IO.Port.LED, IO.Level.HIGH);
            }

            Thread.sleep(100);
        
            // enable power on TR module
            io.write(IO.Port.RESET, IO.Level.LOW);

            // read IO again
            value = io.read(IO.Port.RESET);

            if ( value == 0 ) {
                io.set(IO.Port.LED, IO.Direction.OUTPUT);
                io.write(IO.Port.LED, IO.Level.LOW);
            }
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
