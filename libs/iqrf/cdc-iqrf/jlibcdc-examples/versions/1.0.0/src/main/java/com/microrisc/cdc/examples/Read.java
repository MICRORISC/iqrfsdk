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

package com.microrisc.cdc.examples;

import com.microrisc.cdc.*;

/**
 * Example demonstrates reception of asynchronous messages from connected USB 
 * device. 
 */
public class Read implements J_AsyncMsgListener {
    @Override
    public void onGetMessage(short[] data) {
        if (data == null || data.length == 0) {
            System.out.println("No data received\n");
            return;
        }
                
        System.out.println("data length: " + data.length);
        System.out.print("data received: ");
        for (short sh : data) {
            char[] chars = Character.toChars(sh);
            for (char ch : chars) {
                System.out.print(ch);
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        J_CDCImpl myCDC = null;
        
        try {
            // creating CDC object, which will communicate via /dev/ttyACM0
            //myCDC = new J_CDCImpl("/dev/ttyACM0");
            myCDC = new J_CDCImpl("COM4");
            
            // communication testing
            if (myCDC.test()) {
                System.out.println("Test OK");
            } else {
                System.out.println("Test FAILED");
                myCDC.destroy();
                return;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            if (myCDC != null) {
                myCDC.destroy();
            }
            return;
        }
        
        // listener of asynchronous messages
        Read example = new Read();
        
        // register to receiving asynchronous messages
        myCDC.registerAsyncListener(example);
        
        System.out.println("Receiving: ");
        
        for (int recCounter = 0; recCounter < 50; recCounter++) {
            // if reception is stopped, is not further possible to send and 
            // to receive any next messages
            if (myCDC.isReceptionStopped()) {
                myCDC.destroy();
                return;
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.out.println("Thread interrupted\n");
                break;
            }
        }
        
        // terminate library and free up used resources
        myCDC.destroy();
    }
}
