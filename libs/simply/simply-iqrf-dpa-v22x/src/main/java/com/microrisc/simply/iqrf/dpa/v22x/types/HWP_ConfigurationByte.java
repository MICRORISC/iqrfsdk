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
 * Encapsulates values mainly for DPA cmd WriteHWPConfigurationByte.
 * @author Martin Strouhal
 */
public class HWP_ConfigurationByte {

   private int address, value, mask;

   /**
    * Create a instance of object which encapsulate values mainly for DPA cmd WriteHWPConfigurationByte.
    * @param address of item which will be set
    * @param value of item which will be set
    * @param mask which specifies bits of the configuration to set
    */
   public HWP_ConfigurationByte(int address, int value, int mask) {
      this.address = address;
      this.value = value;
      this.mask = mask;
   }

   /**
    * @return address of the item at configuration memory block.
    */
   public int getAddress() {
      return address;
   }

   /**
    * Sets address of the item at configuration memory block
    *
    * @param address which will be set. The valid address range is 0x01-0x1F for
    * configuration values. Also address 0x20 is valid value for RFPGM settings.
    */
   public void setAddress(int address) {
      this.address = address;
   }

   /**
    * @return value of the configuration item to write.
    */
   public int getValue() {
      return value;
   }

   /**
    * Sets value of the item.
    *
    * @param value of the configuration item to write. See DPA Tech guide.
    */
   public void setValue(int value) {
      this.value = value;
   }

   /**
    * @return mask which specifies bits of the configuration byte to be modified
    * by the corresponding bits of the Value parameter. Only bits that are set
    * at the Mask will be written to the configuration byte i.e. when Mask
    * equals to 0xFF then the whole Value will be written to the configuration
    * byte. For example, when Mask equals to 0x12 then only bit.1 and bit.4 from
    * Value will be written to the configuration byte.
    */
   public int getMask() {
      return mask;
   }

   /**
    * Sets mask which specifies bits of the configuration byte to be modified by
    * the corresponding bits of the Value parameter. Only bits that are set at
    * the Mask will be written to the configuration byte i.e. when Mask equals
    * to 0xFF then the whole Value will be written to the configuration byte.
    * For example, when Mask equals to 0x12 then only bit.1 and bit.4 from Value
    * will be written to the configuration byte.
    *
    * @param mask which will be set
    */
   public void setMask(int mask) {
      this.mask = mask;
   }

   @Override
   public String toString() {
      return "HWP_ConfigurationByte{" + "address=" + address + ", value=" + value
              + ", mask=" + mask + '}';
   }

}
