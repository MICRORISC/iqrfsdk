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

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates blocks of code in hex file.
 * 
 * @author Martin Strouhal
 */
final class CodeBlock implements Comparable<CodeBlock> {

   private int addressStart;
   private int addressEnd;

   /**
    * Creates instance of {@link CodeBlock}.
    * @param start address
    * @param end address
    */
   public CodeBlock(int start, int end) {
      this.addressStart = start;
      this.addressEnd = end;
   }

   /** Creates instance of {@link CodeBlock} with address from 0 to 0. */
   public CodeBlock() {
      this(0, 0);
   }

   /**
    * Returns codeblock's length.
    * @return length as long
    */
   public long getLength() {
      return addressEnd - addressStart + 1;
   }

   /**
    * Returns codeblock's address end.
    * @return end address as long
    */
   public long getAddressEnd() {
      return addressEnd;
   }

   /**
    * Sets end address.
    * @param addressEnd address to set 
    */
   public void setAddressEnd(int addressEnd) {
      this.addressEnd = addressEnd;
   }

   /**
    * Returns codeblock's address start.
    * @return start address as long
    */
   public long getAddressStart() {
      return addressStart;
   }

   /**
    * Sets start address.
    * @param addressStart address to set 
    */
   public void setAddressStart(int addressStart) {
      this.addressStart = addressStart;
   }

   /**
    * Include specified long part to codeblock.
    * @param value long to prolonging
    */
   void incLength(int value) {
      addressEnd += value;
   }

   /**
    * Returns, if it's specified address in code block range.
    * @param address to comparing
    * @return true if it is in range
    */
   public boolean isInRange(int address) {
      return address >= this.addressStart && address <= this.addressEnd;
   }

   /**
    * Returns if it's start and end address of specified codeblock in range.
    */
   public boolean isInside(CodeBlock o) {
      return isInRange(o.addressStart) && isInRange(o.addressEnd);
   }

   /**
    * Returns if it is specified code block adjacent.
    * @param o is specified code block
    * @return true if it is adjacent
    */
   public boolean isAdjacent(CodeBlock o) {
      return !isInRange(o.addressStart)
              && !isInRange(o.addressEnd)
              && ((this.addressEnd + 1) == o.addressStart || o.addressEnd == (this.addressStart - 1));
   }

   /**
    * Returns if it is specified code block adjacent or overlaping.
    * @param o is specified code block
    * @return true if it is adjacent or overlaping
    */
   public boolean isAdjacentOrOverlaps(CodeBlock o) {
      return isInRange(o.addressStart)
              || isInRange(o.addressEnd)
              || (this.addressEnd + 1) == o.addressStart
              || o.addressEnd == (this.addressStart - 1);
   }

   /**
    * Joins specified code block.
    * @param o code block to join
    * @return joined code block
    */
   public CodeBlock joinAdjacent(CodeBlock o) {
      Objects.requireNonNull(o);

      if (!isAdjacent(o)) {
         throw new InvalidParameterException();
      }

      return new CodeBlock(Math.min(this.addressStart, o.addressStart), 
              Math.max(this.addressEnd, o.addressEnd)
      );
   }

   
   /**
    * Merge specified code block.
    * @param o code block to join
    * @return merged code block
    */
   public CodeBlock mergeAdjacentOrOverlaping(CodeBlock o) {
      Objects.requireNonNull(o);

      if (!isAdjacentOrOverlaps(o)) {
         throw new InvalidParameterException();
      }

      return new CodeBlock(Math.min(this.addressStart, o.addressStart), 
              Math.max(this.addressEnd, o.addressEnd)
      );
   }

   /**
    * Joins specified code blocks.
    * @param list list of code blocks to join
    * @return joined code blocks
    */
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

   
   /**
    * Merge specified code blocks.
    * @param o list of code blocks to join
    * @return merged code blocks
    */
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

   /**
    * Convert to string with real address.
    * @return string
    */
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