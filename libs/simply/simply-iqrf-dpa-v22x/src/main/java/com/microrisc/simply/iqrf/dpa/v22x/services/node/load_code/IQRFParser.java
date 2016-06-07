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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Parser for IQRF plugin files.
 *
 * @author Martin Strouhal
 */
final class IQRFParser {

   /** Count of chars on one line */
   private static final int LINE_LENGTH = 40;
   private Scanner sc;

   public IQRFParser(String fileName){
      try {
         sc = new Scanner(new File(fileName));
      } catch (FileNotFoundException ex) {
         throw new IllegalArgumentException("File not found: " + ex.getMessage());
      }
   }

   /** Parses IQRF plugin file into short array.
    *
    * @return short[]
    */
   public short[] parse() {
      ArrayList<Short> resultList = new ArrayList<>(100);
      while (sc.hasNextLine()) {
         String line = sc.nextLine();
         if (line.startsWith("#")) {
            continue;
         }
         if (line.length() != LINE_LENGTH) {
            throw new IllegalArgumentException("Corrupted IQRF plugin file!");
         }
         for (int i = 0; i < LINE_LENGTH; i += 2) {
            resultList.add(Short.parseShort(line.substring(i, i + 2), 16));
         }
      }

      short[] data = new short[resultList.size()];
      for (int i = 0; i < data.length; i++) {
         data[i] = resultList.get(i);
      }
      return data;
   }
}
