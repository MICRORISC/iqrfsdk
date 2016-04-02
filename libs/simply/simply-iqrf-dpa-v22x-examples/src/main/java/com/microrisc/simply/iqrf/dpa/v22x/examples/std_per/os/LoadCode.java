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
package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.os;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.di_services.StandardServices;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingResult;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This example shows how to upload custom DPA handler via DPA.
 *
 * @author Martin Strouhal
 */
public class LoadCode {

   /**
    * Reads hex files and provides getting data from them.
    */
   private final class IntelHEX {

      private ByteBuffer data;

      private final List<CodeBlock> codeBlocks = new ArrayList<>();

      private int offset = 0;

      public IntelHEX(int dataSize) {
         data = ByteBuffer.allocate(dataSize);
      }

      public void setSizeData(int dataSize) {
         data = ByteBuffer.allocate(dataSize);
      }

      public ByteBuffer getData() {
         return data;
      }

      public List<CodeBlock> getCodeBlocks() {
         return codeBlocks;
      }

      public void parseIntelHex(String file) throws FileSystemException,
              IOException {

         List<String> lines = Files.readAllLines(Paths.get(file), Charset.
                 forName("UTF-8"));

         for (String line : lines) {
            //   System.out.println(line);
            parseLine(line, lines.indexOf(line));
         }
      }

      private void parseLine(String line, Integer lineIndex) throws
              FileSystemException {
         line = line.trim();

         if (!line.startsWith(":")) {
            throw new FileSystemException(String.format(
                    "Invalid Intel HEX record: line[%s] '%s' does not start with colon",
                    lineIndex, line));
         }

         if (line.length() % 2 != 1 || line.length() < (1 + 5 * 2)) {
            throw new FileSystemException(String.format(
                    "Invalid Intel HEX record: line[%s] '%s' - invalid length",
                    lineIndex, line));
         }

         byte recordType = 0;
         byte byteCount = ParseSubStringHexByte(line, 1);
         byte lineAddressH = ParseSubStringHexByte(line, 3);
         byte lineAddressL = ParseSubStringHexByte(line, 5);
         recordType = ParseSubStringHexByte(line, 7);
         byte checksum = (byte) (byteCount + lineAddressH + lineAddressL + recordType + ParseSubStringHexByte(
                 line, byteCount * 2 + 9));

         int realAddress = -1;
         int lastAddress = 0;
         boolean useBytes = false;

         switch (recordType) {
            case 0x00:
               realAddress = (offset + ((lineAddressH & 0xFF) << 8) + (lineAddressL & 0xFF));
               // if (realAddress == lastAddress) {
               lastAddress = realAddress + byteCount;
               useBytes = true;
               //  } else 
               //      lastAddress = -1;
               break;

            case 0x01:
               break;

            case 0x02:
            case 0x04:
               offset = ((ParseSubStringHexByte(line, 9) & 0xFF) << 8) + (ParseSubStringHexByte(
                       line, 11) & 0xFF);
               offset *= recordType == 0x02 ? 16 : 65536;
               break;

            default:
               throw new FileSystemException(String.format(
                       "Invalid Intel HEX record: line[%s] '%s' - unknown record type %02x",
                       lineIndex, line, recordType));
         }

         if (useBytes) {
            data.position(realAddress);
            new CodeBlock(realAddress, realAddress + byteCount - 1).
                    joinAdjacentTo(codeBlocks);
         }

         for (int i = 0; i < byteCount; i++) {
            byte b = ParseSubStringHexByte(line, i * 2 + 9);
            checksum += (byte) b;
            if (useBytes) {
               data.put((b));
            }
         }

         if (checksum != 0) {
            throw new FileSystemException(String.format(
                    "Invalid Intel HEX record: line[%s] '%s' - invalid checksum",
                    lineIndex, line));
         }
      }

      private byte ParseSubStringHexByte(String text, int pos) {
         return (byte) Integer.parseInt(text.substring(pos, pos + 2), 16);
      }

   }

   /**
    * Encapsulates blocks of code in hex file.
    */
   private final class CodeBlock implements Comparable<CodeBlock> {

      private int addressStart;
      private int addressEnd;

      public CodeBlock(int start, int end) {
         this.addressStart = start;
         this.addressEnd = end;
      }

      public CodeBlock() {
         this(0, 0);
      }

      public long getLength() {
         return addressEnd - addressStart + 1;
      }

      public long getAddressEnd() {
         return addressEnd;
      }

      public void setAddressEnd(int addressEnd) {
         this.addressEnd = addressEnd;
      }

      public long getAddressStart() {
         return addressStart;
      }

      public void setAddressStart(int addressStart) {
         this.addressStart = addressStart;
      }

      void incLength(int value) {
         addressEnd += value;
      }

      public boolean isInRange(int address) {
         return address >= this.addressStart && address <= this.addressEnd;
      }

      public boolean isInside(CodeBlock o) {
         return isInRange(o.addressStart) && isInRange(o.addressEnd);
      }

      public boolean isAdjacent(CodeBlock o) {
         return !isInRange(o.addressStart)
                 && !isInRange(o.addressEnd)
                 && ((this.addressEnd + 1) == o.addressStart || o.addressEnd == (this.addressStart - 1));
      }

      public boolean isAdjacentOrOverlaps(CodeBlock o) {
         return isInRange(o.addressStart)
                 || isInRange(o.addressEnd)
                 || (this.addressEnd + 1) == o.addressStart
                 || o.addressEnd == (this.addressStart - 1);
      }

      public CodeBlock joinAdjacent(CodeBlock o) {
         Objects.requireNonNull(o);

         if (!isAdjacent(o)) {
            throw new InvalidParameterException();
         }

         return new CodeBlock(Math.min(this.addressStart, o.addressStart), Math.
                 max(this.addressEnd, o.addressEnd));
      }

      public CodeBlock mergeAdjacentOrOverlaping(CodeBlock o) {
         Objects.requireNonNull(o);

         if (!isAdjacentOrOverlaps(o)) {
            throw new InvalidParameterException();
         }

         return new CodeBlock(Math.min(this.addressStart, o.addressStart), Math.
                 max(this.addressEnd, o.addressEnd));
      }

      public void joinAdjacentTo(List<CodeBlock> list) {
         for (int i = 0; i < list.size(); i++) {
            CodeBlock o = list.get(i);
            if (isAdjacent(o)) {
               CodeBlock newInterval = joinAdjacent(o);
               list.remove(i);
               list.add(newInterval);
               return;
            }
         }

         list.add(this);
      }

      public void mergeAdjacentOrOverlapingTo(List<CodeBlock> list) {
         for (int i = 0; i < list.size(); i++) {
            CodeBlock o = list.get(i);
            if (isAdjacentOrOverlaps(o)) {
               CodeBlock newInterval = mergeAdjacentOrOverlaping(o);
               list.remove(i);
               list.add(newInterval);
               return;
            }
         }

         list.add(this);
      }

      @Override
      public String toString() {
         return String.format("0x%08x:0x%08x (%dB 0x%08X)", addressStart,
                 addressEnd, getLength(), getLength());
      }

      public String toStringRealAddress() {
         return String.format("0x%08x:0x%08x (%dB 0x%08X)", addressStart / 2,
                 addressEnd / 2, getLength(), getLength());
      }

      @Override
      public int compareTo(CodeBlock o) {
         if (this.addressStart == o.addressStart) {
            return Integer.compare(this.addressEnd, o.addressEnd);
         } else {
            return Integer.compare(this.addressStart, o.addressStart);
         }
      }
   }

   private static final int CUSTOM_DPA_HANDLER_ADDRESS = 0x3A20;
   private static DPA_Simply simply;

   public static void main(String[] args) {
      LoadCode.IntelHEX file = new LoadCode().new IntelHEX(0xFFFFFF);
      //  file.parseIntelHex("D:\\Plocha\\DPA\\CustomDpaHandlerExamples\\hex\\CustomDpaHandler-LED-Red-On-7xD-V226-160303.hex");
      try {
         file.parseIntelHex(
                 "D:\\Plocha\\IQRF_OS307_7xD\\Examples\\DPA\\CustomDpaHandlerExamples\\hex\\CustomDpaHandler-FRC-Minimalistic-7xD-V224-151201.hex");
      } catch (IOException ex) {
         printMessageAndExit(ex.getMessage());
      }

      CodeBlock customDpaHandlerBlock = findHandlerBlock(file);

      // calcualting length of handler in memory
      final int length = (int) ((customDpaHandlerBlock.getLength() + (64 - 1)) & ~(64 - 1));

      // calculating checksum with initial value 1 (defined for DPA handler)
      int dataChecksum = calculateChecksum(file, customDpaHandlerBlock, 1,
              length);
      System.out.printf("Checksum = %3x\n", dataChecksum);

      // creating Simply instance
      try {
         simply = DPA_SimplyFactory.getSimply(
                 "config" + File.separator + "Simply.properties");
      } catch (SimplyException ex) {
         printMessageAndExit("Error while creating Simply: " + ex.
                 getMessage());
      }

      // getting network 1
      Network network1 = simply.getNetwork("1", Network.class);
      if (network1 == null) {
         printMessageAndExit("Network 1 doesn't not exist");
      }

      // getting node 1
      Node node1 = network1.getNode("1");
      if (node1 == null) {
         printMessageAndExit("Node 1 doesn't exist");
      }

      // get access to EEEPROM peripheral
      EEEPROM eeeprom = node1.getDeviceObject(EEEPROM.class);
      if (eeeprom == null) {
         printMessageAndExit("OS doesn't exist or is not enabled");
      }

      // specify on which address start part with handler code in EEEPROM
      int startAddress = 0;
      // specify length of block, which will be written to EEEPROM
      int blockSize = 32;

      for (long address = customDpaHandlerBlock.getAddressStart();
              address < customDpaHandlerBlock.getAddressEnd();
              address += blockSize) 
      {

         // preparing code to write into EEEPROM
         short[] block = new short[blockSize];
         for (int i = 0; i < blockSize; i++) {
            block[i] = (short) ((file.getData().get((int) address + i)) & 0xFF);
         }

         // writing code to EEEPROM
         VoidType voidResult = eeeprom.extendedWrite(startAddress, block);
         if (voidResult == null) {
            processNullResult(eeeprom, "Writing to EEEPROM failed.",
                    "Writing data hasn't been processed yet");
         }
         startAddress += blockSize;
      }


      // get access to OS peripheral
      OS os = node1.getDeviceObject(OS.class);
      if (os == null) {
         printMessageAndExit("OS doesn't exist or is not enabled");
      }
      
      // set longer waiting timeout
      os.setDefaultWaitingTimeout(30000);

      
      //os.reset();
            
      
      try {
         Thread.sleep(5000);
      } catch (InterruptedException ex) {
         Logger.getLogger(LoadCode.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      // loading code
      LoadingResult result = os.loadCode(new LoadingCodeProperties(
              LoadingCodeProperties.LoadingAction.ComputeAndMatchChecksumWithCodeLoading,
              LoadingCodeProperties.LoadingContent.CustomDPAHandler,
              startAddress, length, dataChecksum));

      if (result == null) {
         processNullResult(os, "Getting OS info failed",
                 "Getting OS info hasn't been processed yet");
      } else {
         System.out.println("Result loading of Custom DPA handler: " + result);
      }

      simply.destroy();
   }

   private static CodeBlock findHandlerBlock(IntelHEX file) {
      Collections.sort(file.getCodeBlocks());

      Optional<LoadCode.CodeBlock> handlerBlock = file.getCodeBlocks().
              stream().filter(new Predicate<LoadCode.CodeBlock>() {
                 @Override
                 public boolean test(LoadCode.CodeBlock t) {
                    return ((t.getAddressStart() <= CUSTOM_DPA_HANDLER_ADDRESS * 2)
                            && (t.getAddressEnd() >= CUSTOM_DPA_HANDLER_ADDRESS * 2));
                 }
              }).findFirst();

      // checks if is Custom DPA handler available
      if (handlerBlock.isPresent()
              && (file.getData().getShort((int) handlerBlock.get().
                      getAddressStart()) == 0x6400)) {
         return handlerBlock.get();
      } else {
         printMessageAndExit(
                 "Selected .hex files does not include Custom DPA handler section or the code does not start with clrwdt() marker.");
         return null;
      }
   }

   private static int calculateChecksum(IntelHEX file, CodeBlock handlerBlock,
           int checkSumInitialValue, int length) {
      int dataChecksum = checkSumInitialValue;
      // checksum for data
      for (long address = handlerBlock.getAddressStart();
              address < handlerBlock.getAddressStart() + length;
              address++) {
         int oneByte = file.getData().get((int) address) & 0xFF;
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
      return dataChecksum;
   }

   // prints out specified message, destroys the Simply and exits
   private static void printMessageAndExit(String message) {
      System.out.println(message);
      if (simply != null) {
         simply.destroy();
      }
      System.exit(1);
   }

   // processes NULL result
   private static void processNullResult(StandardServices services,
           String errorMsg, String notProcMsg) {
      CallRequestProcessingState procState = services.
              getCallRequestProcessingStateOfLastCall();
      if (procState == CallRequestProcessingState.ERROR) {
         CallRequestProcessingError error = services.
                 getCallRequestProcessingErrorOfLastCall();
         printMessageAndExit(errorMsg + ": " + error);
      } else {
         printMessageAndExit(notProcMsg + ": " + procState);
      }
   }
}
