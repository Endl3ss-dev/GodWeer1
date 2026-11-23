// deobf by pusia
package su.catlean.api.data;

import org.jetbrains.annotations.NotNull;

public final class Box {
   private final int value;
   private final int value2;
   private final int value3;
   private final int value4;

   public Box(int value, int value2, int value3, int value4) {
      this.value = value;
      this.value2 = value2;
      this.value3 = value3;
      this.value4 = value4;
   }

   public final int getValue() {
      return this.value;
   }

   public final int getValue2() {
      return this.value2;
   }

   public final int getValue3() {
      return this.value3;
   }

   public final int getValue4() {
      return this.value4;
   }

   public final int getValue5() {
      return this.value;
   }

   public final int getValue6() {
      return this.value2;
   }

   public final int getValue7() {
      return this.value3;
   }

   public final int getValue8() {
      return this.value4;
   }

   @NotNull
   public final Box computeBox(int value, int value2, int value3, int value4) {
      return new Box(value, value2, value3, value4);
   }

   @NotNull
   @Override
   public String toString() {
      int value42 = this.value4;
      int value32 = this.value3;
      int value22 = this.value2;
      int value5 = this.value;
      return "Box(x=" + value5 + ", y=" + value22 + ", width=" + value32 + ", height=" + value42 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Integer.hashCode(this.value);
      hashCode = hashCode * 31 + Integer.hashCode(this.value2);
      hashCode = hashCode * 31 + Integer.hashCode(this.value3);
      return hashCode * 31 + Integer.hashCode(this.value4);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Box box)) {
         return false;
      } else if (this.value != box.value) {
         return false;
      } else if (this.value2 != box.value2) {
         return false;
      } else {
         return this.value3 != box.value3 ? false : this.value4 == box.value4;
      }
   }
}