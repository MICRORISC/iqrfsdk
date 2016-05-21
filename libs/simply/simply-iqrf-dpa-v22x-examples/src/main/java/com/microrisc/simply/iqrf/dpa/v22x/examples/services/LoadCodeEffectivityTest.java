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
package com.microrisc.simply.iqrf.dpa.v22x.examples.services;

/**
 *
 * @author Martin Strouhal
 */
public class LoadCodeEffectivityTest {

   public static void main(String[] args) {
      int[] array = new int[32 * 11];
      for (int i = 0; i < array.length; i++) {
         array[i] = i;
      }
      
      /*int index16 = 0, index32 = 0;
      for (int i = 0; i < array.length; i++) {
         if (i % 2 == 0) {
            array[i] = 32;
            index32++;
            array[i] <<= 24;
            array[i] += index32;
         } else {
            array[i] = 16;
            index16++;
            array[i] <<= 24;
            array[i] += index16;
         }
      }*/
      
      vypisEffective(array);
   }
   
   private static void vypis(int[] array){
      for (int i = 0; i < array.length / 16; i+=6) {
         System.out.print("48 request ");
         for (int j = i*16; j < (i+1) * 16; j++) {
            System.out.print(array[j] + ", ");
         }//TODO more effective by merge
         for (int j = (i+1)*16; j < (i+2) * 16; j++) {
            System.out.print(array[j] + ", ");
         }
         System.out.println("");
         
         System.out.print("16 request ");
         for (int j = (i+2)*16; j < (i+3) * 16; j++) {
            System.out.print(array[j] + ", ");
         }
         System.out.println("\n");
         
         
         System.out.print("16 request ");
         for (int j = (i+3)*16; j < (i+4) * 16; j++) {
            System.out.print(array[j] + ", ");
         }
         System.out.println("");
         
         System.out.print("48 request ");
         for (int j = (i+4)*16; j < (i+5) * 16; j++) {
            System.out.print(array[j] + ", ");
         }//TODO more effective by merge
         for (int j = (i+5)*16; j < (i+6) * 16; j++) {
            System.out.print(array[j] + ", ");
         }
         System.out.println("\n\n");
      }
   }
   
   private static void vypisEffective(int[] array){
      for (int i = 0; i < array.length / 16; i+=8) {
         //first batch
         System.out.print("48 request ");
         for (int j = i*16; j < (i+3) * 16; j++) {
            System.out.print(array[j] + ", ");
         }
         System.out.println("");
         
         System.out.print("16 request ");
         for (int j = (i+3)*16; j < (i+4) * 16; j++) {
            System.out.print(array[j] + ", ");
         }
         System.out.println("\n");
         
         
         //second batch
         System.out.print("16 request ");
         for (int j = (i+4)*16; j < (i+5) * 16; j++) {
            System.out.print(array[j] + ", ");
         }
         System.out.println("");
         
         System.out.print("48 request ");
         for (int j = (i+5)*16; j < (i+8) * 16; j++) {
            System.out.print(array[j] + ", ");
         }
         System.out.println("\n\n");
      }
   }
}
