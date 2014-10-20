

package com.microrisc.rpi.io.examples;

import com.microrisc.rpi.io.IO;
import com.microrisc.rpi.io.IOException;
import com.microrisc.rpi.io.SimpleIO;

/**
 * Reseting example.
 * 
 * @author Rostislav Spinar
 */
public class ResetTR {
    
    public static void main(String[] args) {
        System.out.println("Reseting TR module example");
        
        IO io = null;
        
        try {
            io = new SimpleIO();
            
            io.resetTR();   
            System.out.println("TR module has been restarted.");
        } catch (IOException e) {
            System.err.println("Error while reseting module: " + e);
        } finally {
            // termination and resource free up
            if ( io != null ) {
                io.destroy();
            }
        }
    }
}
