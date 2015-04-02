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
 * Button example.
 * 
 * @author Rostislav Spinar
 */
public class Button {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Button example");
        
        IO io = null;
        int ioResult = 0;
        
        try {
            io = new SimpleIO();

            // set pins state
            io.set(IO.Pin.BUTTON, IO.Direction.INPUT);
            io.set(IO.Pin.LED, IO.Direction.OUTPUT);
        
            //10s loop for button testing
            for (int i = 0; i < 1000; i++) {
                ioResult = io.read(IO.Pin.BUTTON);
                
                // button pressed
                if(ioResult == 0) {
                    io.write(IO.Pin.LED, IO.Level.HIGH);
                }
                // button released
                else {
                    io.write(IO.Pin.LED, IO.Level.LOW);
                }
                            
                Thread.sleep(10);
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
