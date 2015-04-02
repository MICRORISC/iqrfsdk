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
 * Reseting example.
 * 
 * @author Rostislav Spinar
 */
public class ResetTr {
    
    public static void main(String[] args) {
        System.out.println("Reseting TR module example");
        
        IO io = null;
        
        try {
            io = new SimpleIO();
            
            io.resetTr();   
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
