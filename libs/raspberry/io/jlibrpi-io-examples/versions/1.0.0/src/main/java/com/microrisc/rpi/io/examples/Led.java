
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
      
            // power on TR module
            io.set(IO.Port.RESET, IO.Direction.OUTPUT);
            io.write(IO.Port.RESET, IO.Level.LOW);

            // activate LED
            io.set(IO.Port.LED, IO.Direction.OUTPUT);

            final int MAX_CYCLES = 10;
            int cycle = 0;
            IO.Level level = IO.Level.LOW;
            
            do {            
                level = ( level == IO.Level.LOW )? IO.Level.HIGH : IO.Level.LOW;
                io.write(IO.Port.LED, level);
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
