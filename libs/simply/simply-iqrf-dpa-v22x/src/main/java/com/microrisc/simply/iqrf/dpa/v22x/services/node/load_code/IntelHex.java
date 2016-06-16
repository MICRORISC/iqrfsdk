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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads hex files and provides getting data from them.
 *
 * @author Martin Strouhal
 */
final class IntelHex {

   private ByteBuffer data;
   private final List<CodeBlock> codeBlocks = new ArrayList<>();
   private int offset = 0;

   /**
    * Creates instance of {@link IntelHex} with predefined buffer size.
    * @param dataSize size of buffer
    */
   public IntelHex(int dataSize) {
      data = ByteBuffer.allocate(dataSize);
   }

   /**
    * Sets size of buffer via allocate method.
    * 
    * @param dataSize size of buffer
    */
   public void setSizeData(int dataSize) {
      data = ByteBuffer.allocate(dataSize);
   }

   /**
    * Returns buffer with parsed data accroding to parsed {@link CodeBlocks codeblocks}.
    * @return buffer as {@link ByteBuffer}
    */
   public ByteBuffer getData() {
      return data;
   }

   /**
    * Returns all parsed {@link CodeBlock codeblocks}.
    * @return List of {@link CodeBlock}
    */
   public List<CodeBlock> getCodeBlocks() {
      return codeBlocks;
   }

   /**
    * Parse file into the buffer and {@link CodeBlock}
    *
    * @param file which will be parsed
    * @throws FileSystemException if some problem with reading and parsing is
    * occurred
    * @throws IOException if some error with file is occurred
    */
   public void parseIntelHex(String file) throws FileSystemException,
           IOException {

      List<String> lines = Files.readAllLines(Paths.get(file), Charset.forName("UTF-8"));

      for (String line : lines) {
         parseLine(line, lines.indexOf(line));
      }
   }

   /**
    * Parse line in hex file as record according his type.
    * .
    * @param line from hex file
    * @param lineIndex index of line
    * @throws FileSystemException throws if record is corrupted
    */
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
      byte byteCount = parseSubStringHexByte(line, 1);
      byte lineAddressH = parseSubStringHexByte(line, 3);
      byte lineAddressL = parseSubStringHexByte(line, 5);
      recordType = parseSubStringHexByte(line, 7);
      byte checksum = (byte) (byteCount + lineAddressH + lineAddressL + recordType + parseSubStringHexByte(
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
            offset = ((parseSubStringHexByte(line, 9) & 0xFF) << 8) + (parseSubStringHexByte(
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
         byte b = parseSubStringHexByte(line, i * 2 + 9);
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

   /**
    * Parse hex number on the specified position.
    * 
    * @param text from it will be parsed number
    * @param pos is position in text
    * @return parsed number
    */
   private byte parseSubStringHexByte(String text, int pos) {
      return (byte) Integer.parseInt(text.substring(pos, pos + 2), 16);
   }

}
