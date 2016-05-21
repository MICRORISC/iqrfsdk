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

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Prepares data in parts with effective size given to IQRF.
 *
 * @author Martin Strouhal
 */
public final class DataPreparer {

   private CodeBlock handlerBlock;
   private ByteBuffer data;
   private LoadCodeServiceParameters params;

   public DataPreparer(CodeBlock handlerBlock, IntelHex file,
           LoadCodeServiceParameters params) {
      this.handlerBlock = handlerBlock;
      this.data = file.getData();
      this.params = params;
   }


   /**
    * Returns array of short[] prepared to effective writing into memory
    *
    * @return array of short[]
    */
   public short[][] prepare() {
      // size of the smallest part of data which we are dividing - we are 
      // dividing data on 48's part and 16's part
      final int smallestPartSize = 16;

      List<Short[]> list = new LinkedList<>();

      for (long address = handlerBlock.getAddressStart() / smallestPartSize;
              address < handlerBlock.getAddressEnd() / smallestPartSize;
              address += smallestPartSize / 2) {
//         //first batch
//         System.out.print("48 request ");
         list.add(getDataPart(address, 0, 3, smallestPartSize));
//         for (long j = (address+0)*16; j < (address+3) * 16; j++) {
//            System.out.print(getCheckedValue(data, j, handlerBlock.getAddressEnd()) + ", ");
//         }
//         System.out.println("");
//         
//         System.out.print("16 request ");
         list.add(getDataPart(address, 3, 4, smallestPartSize));
//         for (long j = (address+3)*16; j < (address+4) * 16; j++) {
//            System.out.print(getCheckedValue(data, j, handlerBlock.getAddressEnd())+ ", ");
//         }
//         System.out.println("\n");
//         
//         
//         //second batch
//         System.out.print("16 request ");
         list.add(getDataPart(address, 4, 5, smallestPartSize));
//         for (long j = (address+4)*16; j < (address+5) * 16; j++) {
//            System.out.print(getCheckedValue(data, j, handlerBlock.getAddressEnd()) + ", ");
//         }
//         System.out.println("");
//         
//         System.out.print("48 request ");
         list.add(getDataPart(address, 5, 8, smallestPartSize));
//         for (long j = (address+5)*16; j < (address+8) * 16; j++) {
//            System.out.print(getCheckedValue(data, j, handlerBlock.getAddressEnd()) + ", ");
//         }
//         System.out.println("\n\n");
      }
      short[][] resultData = new short[list.size()][];
      for (int i = 0; i < resultData.length; i++) {
         Short[] dataPart = list.get(i);
         resultData[i] = new short[dataPart.length];
         for (int j = 0; j < dataPart.length; j++) {
            resultData[i][j] = dataPart[j];
         }
      }
      return resultData;
   }

   /**
    * Returns data part.
    *
    * @param address of data
    * @param startOffsetIndex start index of offset of data part
    * @param endOffsetIndex end index of offset of data part
    * @param smallestPartSize used while counting
    * @return short[]
    */
   private Short[] getDataPart(long address, int startOffsetIndex,
           int endOffsetIndex, int smallestPartSize) {
      int dataPartLength = (endOffsetIndex - startOffsetIndex) *  smallestPartSize;
      Short[] dataPart = new Short[dataPartLength];
      int dataIndex = 0;
      for (long j = (address + startOffsetIndex) * smallestPartSize;
              j < (address + endOffsetIndex) * smallestPartSize; j++) 
      {
         dataPart[dataIndex] = getCheckedValue(j);
         dataIndex++;
      }
      return dataPart;
   }

   /** Return value if it's in range, otherwise returns 0x34FF. */
   private short getCheckedValue(long address) {
      if (address > handlerBlock.getAddressEnd()) {
         return 0x34FF;
      } else {
         return (short) (data.get((int) address) & 0xFF);
      }
   }
}
