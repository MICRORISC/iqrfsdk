/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.services.node.load_code;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.DeviceObject;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.protocol.DPA_ProtocolProperties;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Request;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingResult;
import com.microrisc.simply.iqrf.types.VoidType;
import com.microrisc.simply.services.BaseServiceResult;
import com.microrisc.simply.services.ServiceParameters;
import com.microrisc.simply.services.ServiceResult;
import com.microrisc.simply.services.node.BaseService;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Load Code Service implementation.
 * 
 * @author Michal Konopa
 */
public final class SimpleLoadCodeService 
extends BaseService implements LoadCodeService {

   private static final Logger log = LoggerFactory.getLogger(SimpleLoadCodeService.class);
   
   private static final int CRC_INIT_VALUE_HEX = 0x01;
   private static final int CRC_INIT_VALUE_IQRF = 0x03;
   
   private CodeBlock findHandlerBlock(IntelHex file) {
      Collections.sort(file.getCodeBlocks());

      List<CodeBlock> blocks = file.getCodeBlocks();

      for (CodeBlock block : blocks) {
         if (block != null && (file.getData().getShort((int) block.
                 getAddressStart()) == 0x6400)) {
            return block;
         }
      }

      terminateWithFail("Selected .hex files does not include Custom DPA "
              + "handler section or the code does not start with clrwdt() marker.");
      return null;
   }

   private int calculateChecksum(IntelHex file, CodeBlock handlerBlock, int length) {
      return calculateChecksum(file.getData().asShortBuffer(), handlerBlock, 
              CRC_INIT_VALUE_HEX, length);
   }

   private int calculateChecksum(short[] data, int length){
      CodeBlock block = new CodeBlock(0, data.length);
      ShortBuffer buffer = ShortBuffer.wrap(data);
      return calculateChecksum(buffer, block, CRC_INIT_VALUE_IQRF, length);
   }
   
   private int calculateChecksum(ShortBuffer buffer, CodeBlock handlerBlock,
           int checkSumInitialValue, int length) {
      log.debug("calculateChecksum - start: buffer={}, handlerBlock={}, checkSumInitialValue={}, length={}",
              buffer, handlerBlock, checkSumInitialValue, length);
      int dataChecksum = checkSumInitialValue;
      // checksum for data
      for (long address = handlerBlock.getAddressStart();
              address < handlerBlock.getAddressStart() + length;
              address++) {
         int oneByte = buffer.get((int) address) & 0xFF;
         if (handlerBlock.getAddressEnd() - address < 0) {
            oneByte = 0x34FF;
         }
         
         // One’s Complement Fletcher Checksum
         int tempL = dataChecksum & 0xff;
         tempL += oneByte;
         if ((tempL & 0x100) != 0) {
            tempL++;
         }

         int tempH = dataChecksum >> 8;
         tempH += tempL & 0xff;
         if ((tempH & 0x100) != 0) {
            tempH++;
         }

         dataChecksum = (tempL & 0xff) | (tempH & 0xff) << 8;
      }
      
      log.debug("calculateChecksum - end: {}", dataChecksum);
      return dataChecksum;
   }

   private ServiceResult<LoadingResult, LoadCodeProcessingInfo> 
        terminateWithFail(String failMsg){
      return new BaseServiceResult<LoadingResult, LoadCodeProcessingInfo>(
              ServiceResult.Status.ERROR,
              new LoadingResult(false), new LoadCodeProcessingInfo(failMsg));
   }
   
   private ServiceResult<LoadingResult, LoadCodeProcessingInfo> 
        terminateSuccessfully(LoadingResult result){
      return new BaseServiceResult<LoadingResult, LoadCodeProcessingInfo>(
              ServiceResult.Status.SUCCESSFULLY_COMPLETED,
              result, new LoadCodeProcessingInfo(result.toString()));
   }

   private void writeDataToMemory(int startAddress, short[][] data) {
      if(log.isDebugEnabled()){
         String debugData = "{\n";
         for (int i = 0; i < data.length; i++) {
            debugData += "(length: " + data[i].length + ") > " + Arrays.toString(data[i]) + "\n";
         }
         debugData += "\n}";
         log.debug("writeDataToMemory - start: startAddress={}, data={}", 
                 startAddress, debugData);
      }
      
      EEEPROM eeeprom = getDeviceObject(EEEPROM.class);
      if (eeeprom == null) {
         throw new RuntimeException("EEEPROM doesn't exist or is not enabled");
      }
      OS os = getDeviceObject(OS.class);
      if (os == null) {
         throw new RuntimeException("OS doesn't exist or is not enabled");
      }
      
      int actualAddress = startAddress;
      int index = 0;
      while(index < data.length){
         if(data[index].length == 16 && data[index+1].length == 16){
          
            DPA_Request firstReq = new DPA_Request(EEEPROM.class,
                    EEEPROM.MethodID.EXTENDED_WRITE,
                    new Object[]{actualAddress, data[index]},
                    DPA_ProtocolProperties.HWPID_Properties.DO_NOT_CHECK);
            actualAddress += data[index].length;

            DPA_Request secondReq = new DPA_Request(EEEPROM.class,
                    EEEPROM.MethodID.EXTENDED_WRITE,
                    new Object[]{actualAddress, data[index+1]},
                    DPA_ProtocolProperties.HWPID_Properties.DO_NOT_CHECK);
            actualAddress += data[index+1].length;
            
            VoidType result = os.batch(new DPA_Request[]{firstReq, secondReq});
            checkResult(os, result);
            
            index+=2;
         }else{
            VoidType result = eeeprom.extendedWrite(actualAddress, data[index]);
            checkResult(eeeprom, result);
            actualAddress+=data[index].length;
            index++;
         }
      }
      
      log.debug("writeDataToMemory - end");
   }
   
   private void checkResult(DPA_StandardServices peripheral, VoidType result){
      if(result == null){
         CallRequestProcessingState procState = peripheral.getCallRequestProcessingStateOfLastCall();
        if ( procState == CallRequestProcessingState.ERROR ) {
            CallRequestProcessingError error = peripheral.getCallRequestProcessingErrorOfLastCall();
            throw new RuntimeException("Exception while writing data to memory " + error);
        } else {
            throw new RuntimeException("Exception while writing data to memory " + procState);
        }
      }
   }
   
    /**
     * Creates new Load Code Service object.
     * @param deviceObjects Device Objects to use
     */
    public SimpleLoadCodeService(Map<Class, DeviceObject> deviceObjects) {
        super(deviceObjects);
    }
    
    @Override
    public ServiceResult<LoadingResult, LoadCodeProcessingInfo> 
        loadCode(LoadCodeServiceParameters params) 
    {
       log.debug("loadCode - start: params={}", params);
       short[][] dataToWrite = null;
       final int length, dataChecksum;
       
       if (params.getLoadingContent() == LoadingCodeProperties.LoadingContent.Hex) {
          // prepare with allocated size and after parse data
          IntelHex file = new IntelHex(0xFFFFFF);
          try {
             file.parseIntelHex(params.getFileName());
          } catch (IOException ex) {
             return terminateWithFail(ex.getMessage());
          }

          // separating code block with custom DPA handler block
          CodeBlock handlerBlock = findHandlerBlock(file);
          log.debug(" Handler block starts at " + handlerBlock.
                  getAddressStart()
                  + " and ends at " + handlerBlock.getAddressEnd());

          // calcualting rounded length of handler in memory
          length = (int) ((handlerBlock.getLength() + (64 - 1)) & ~(64 - 1));

          // calculating checksum with initial value 1 (defined for DPA handler)
          dataChecksum = calculateChecksum(file, handlerBlock, length);
          log.debug(" Checksum of data is " + Integer.toHexString(dataChecksum));

          // prepare data to 48 and 16 long blocks for writing
          dataToWrite = new DataPreparer(handlerBlock, file).prepare();
       } else if (params.getLoadingContent() == LoadingCodeProperties.LoadingContent.IQRF_Plugin) {
          // parse iqrf file
          IQRFParser parser = new IQRFParser(params.getFileName());
          short[] parsedData = parser.parse();
          
          // set length
          length = parsedData.length;
          log.debug(" Length of data is " + Integer.toHexString(length));
          
          dataChecksum = calculateChecksum(parsedData, length);
          //dataChecksum = 0x2fd2;
          log.debug(" Checksum of data is " + Integer.toHexString(dataChecksum));
          
          // prepare data to 48 and 16 long blocks for writing
          dataToWrite = new DataPreparer(parsedData).prepare();
       }else{
          log.debug("loadCode - end");
          return terminateWithFail("Unsupported loading content");
       }
       
       // writing data to memory
       try{
         writeDataToMemory(params.getStartAddress(), dataToWrite);
       }catch(RuntimeException re){
          log.debug("loadCode - end");
          return terminateWithFail(re.getMessage());
       }
      
      // get access to OS peripheral
      OS os = getDeviceObject(OS.class);
      if (os == null) {
         log.debug("loadCode - end");
         return terminateWithFail("OS doesn't exist or is not enabled");
      }
      
      // set longer waiting timeout
      os.setDefaultWaitingTimeout(30000);            
      
      // loading code
      LoadingResult result = os.loadCode(new LoadingCodeProperties(
              params.getLoadingAction(), params.getLoadingContent(),
              params.getStartAddress(), length, dataChecksum));

      log.debug("loadCode - end");
      if (result == null) {
          return terminateWithFail("Loading of code failed: "
                  + "Loading of code hasn't been processed yet");
       } else if (result.getResult()) {
          return terminateSuccessfully(result);
       } else {
          return terminateWithFail(result.toString());
       }
    }

    @Override
    public void setServiceParameters(ServiceParameters params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}