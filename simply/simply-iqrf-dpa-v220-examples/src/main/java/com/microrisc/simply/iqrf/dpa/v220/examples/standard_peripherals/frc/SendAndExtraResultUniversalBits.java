/* 
 * Copyright 2015 MICRORISC s.r.o.
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

package  com.microrisc.simply.iqrf.dpa.v220.examples.standard_peripherals.frc;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v220.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v220.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v220.types.FRC_Data;
import com.microrisc.simply.iqrf.dpa.v220.types.FRC_UniversalWithBits;
import java.io.File;
import java.util.Map;

/**
 * Example of using FRC peripheral - send command and getting extra result with
 * FRC Universal command.
 *
 * @author Michal Konopa
 * @author Rostislav Spinar
 * @author Martin Strouhal
 */
public class SendBitsUniversal {

   // reference to Simply
   private static Simply simply = null;

   // prints out specified error description, destroys the Simply and exits
   private static void printMessageAndExit(String errorDescr) {
      System.out.println(errorDescr);
      if (simply != null) {
         simply.destroy();
      }
      System.exit(1);
   }

   // processes NULL result
   private static void processNullResult(FRC frc, String errorMsg,
           String notProcMsg) {
      CallRequestProcessingState procState = frc.getCallRequestProcessingStateOfLastCall();
      if (procState == CallRequestProcessingState.ERROR) {
         CallRequestProcessingError error = frc.getCallRequestProcessingErrorOfLastCall();
         printMessageAndExit(errorMsg + ": " + error);
      } else {
         printMessageAndExit(notProcMsg + ": " + procState);
      }
   }

   public static void main(String[] args) {
      // creating Simply instance
      try {
         simply = DPA_SimplyFactory.getSimply(
                 "config" + File.separator + "Simply.properties");
      } catch (SimplyException ex) {
         printMessageAndExit("Error while creating Simply: " + ex.getMessage());
      }

      // getting network 1
      Network network1 = simply.getNetwork("1", Network.class);
      if (network1 == null) {
         printMessageAndExit("Network 1 doesn't exist");
      }

      // getting a master node
      Node master = network1.getNode("0");
      if (master == null) {
         printMessageAndExit("Master doesn't exist");
      }

      // getting FRC interface
      FRC frc = master.getDeviceObject(FRC.class);
      if (frc == null) {
         printMessageAndExit("FRC doesn't exist or is not enabled");
      }

      // creating a new universal FRC command with specified ID
      FRC_UniversalWithBits frcCmd = new FRC_UniversalWithBits(0x41);

      // sending FRC command and getting data
      FRC_Data data = frc.send(frcCmd);
      if (data == null) {
         processNullResult(frc, "Sending FRC command failed",
                 "Sending FRC command hasn't been processed yet"
         );
      }

      // getting extra FRC result
      short[] extra = frc.extraResult();
      if (extra == null) {
         processNullResult(frc, "Setting FRC extra result failed",
                 "Setting FRC extra result hasn't been processed yet"
         );
      }

      // merging data and extra result
      short[] allData = new short[64];
      System.arraycopy(data.getData(), 0, allData, 0, data.getData().length);
      System.arraycopy(extra, 0, allData, data.getData().length, extra.length);

      // parsing of all data
      Map<String, FRC_UniversalWithBits.Result> map = null;
      try {
         map = frcCmd.parse(allData);
      } catch (Exception ex) {
         printMessageAndExit("Parsing of FRC result failed: " + ex);
      }

      System.out.println("FRC results:");      
      
      System.out.println(map.get("0").getBit1() + " " + map.get("0").getBit0());
      System.out.println(map.get("1").getBit1() + " " + map.get("1").getBit0());
      System.out.println(map.get("2").getBit1() + " " + map.get("2").getBit0());

      simply.destroy();
   }

}
