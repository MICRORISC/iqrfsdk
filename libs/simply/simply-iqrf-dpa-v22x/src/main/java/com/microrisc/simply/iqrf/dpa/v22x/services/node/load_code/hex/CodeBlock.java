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
package com.microrisc.simply.iqrf.dpa.v22x.services.node.load_code.hex;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates blocks of code in hex file.
 * 
 * @author Martin Strouhal
 */
public final class CodeBlock implements Comparable<CodeBlock> {

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

      return new CodeBlock(Math.min(this.addressStart, o.addressStart), 
              Math.max(this.addressEnd, o.addressEnd)
      );
   }

   public CodeBlock mergeAdjacentOrOverlaping(CodeBlock o) {
      Objects.requireNonNull(o);

      if (!isAdjacentOrOverlaps(o)) {
         throw new InvalidParameterException();
      }

      return new CodeBlock(Math.min(this.addressStart, o.addressStart), 
              Math.max(this.addressEnd, o.addressEnd)
      );
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