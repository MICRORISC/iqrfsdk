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
package com.microrisc.simply.iqrf.dpa.v22x.types;

/**
 * Properties of currently loading code image (eg. custom DPA handler, iqrf
 * plugin and etc.).
 *
 * @author Martin Strouhal
 */
public class LoadingCodeProperties {

   /** Identify action while is code loading. */
   public enum LoadingAction {
      /** Computes and matches the checksum only without loading code image. */
      ComputeAndMatchChecksumWithoutCodeLoading,
      /** Same
       * as above plus loads the code into Flash if the checksum matches. */
      ComputeAndMatchChecksumWithCodeLoading;
   }

   /** Identify code image which is loading. */
   public enum LoadingContent {
      Hex,
      IQRF_Plugin;
   }

   private LoadingAction action;
   private LoadingContent content;
   private int address, length, checksum;

   /** Create a new instance of {@link LoadingCodeProperties}.
    *
    * @param action which is executed while code image is loading
    * @param content of code image
    * @param address of memory to load the code image from
    * @param length of the code image
    * @param checksum of the code image
    */
   public LoadingCodeProperties(LoadingAction action, LoadingContent content,
           int address, int length, int checksum) {
      this.action = action;
      this.content = content;
      this.address = address;
      this.length = length;
      this.checksum = checksum;
   }

   /**
    * @return {@link LoadingAction} which is executed while code image is
    * loading
    */
   public LoadingAction getAction() {
      return action;
   }

   /**
    * @return {@link LoadingContent} of code image
    */
   public LoadingContent getContent() {
      return content;
   }

   /**
    * @return address of memory to load the code image from
    */
   public int getAddress() {
      return address;
   }

   /**
    * @return length of the code image
    */
   public int getLength() {
      return length;
   }

   /**
    * @return checksum of the code image
    */
   public int getChecksum() {
      return checksum;
   }
}
