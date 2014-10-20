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
