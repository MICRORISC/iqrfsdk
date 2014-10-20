
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
        
        try {
            io = new SimpleIO();
        
            // power on TR module
            io.set(IO.Port.RESET, IO.Direction.OUTPUT);
            io.write(IO.Port.RESET, IO.Level.LOW);

            // SW button trigger test
            io.set(IO.Port.CE0, IO.Direction.OUTPUT);
            io.write(IO.Port.CE0, IO.Level.HIGH);
        
            Thread.sleep(500);
        
            // falling edge - trigger
            io.write(IO.Port.CE0, IO.Level.LOW);

            System.out.println("CE0 as INPUT to test HW button");
            io.set(IO.Port.CE0, IO.Direction.INPUT);
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
