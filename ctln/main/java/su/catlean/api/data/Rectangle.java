// deobf by pusia
package su.catlean.api.data;

import org.jetbrains.annotations.NotNull;

public final class Rectangle {
   private final float amount;
   private final float amount2;
   private final float amount3;
   private final float amount4;

   public Rectangle(float amount, float amount2, float amount3, float amount4) {
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final float getAmount3() {
      return this.amount3;
   }

   public final float getAmount4() {
      return this.amount4;
   }

   public final float getAmount5() {
      return this.amount;
   }

   public final float getAmount6() {
      return this.amount2;
   }

   public final float getAmount7() {
      return this.amount3;
   }

   public final float getAmount8() {
      return this.amount4;
   }

   @NotNull
   public final Rectangle computeRectangle(float amount, float amount2, float amount3, float amount4) {
      return new Rectangle(amount, amount2, amount3, amount4);
   }

   @NotNull
   @Override
   public String toString() {
      float amount42 = this.amount4;
      float amount32 = this.amount3;
      float amount22 = this.amount2;
      float amount5 = this.amount;
      return "Rectangle(x=" + amount5 + ", y=" + amount22 + ", x1=" + amount32 + ", y1=" + amount42 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Float.hashCode(this.amount);
      hashCode = hashCode * 31 + Float.hashCode(this.amount2);
      hashCode = hashCode * 31 + Float.hashCode(this.amount3);
      return hashCode * 31 + Float.hashCode(this.amount4);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Rectangle rectangle)) {
         return false;
      } else if (Float.compare(this.amount, rectangle.amount) != 0) {
         return false;
      } else if (Float.compare(this.amount2, rectangle.amount2) != 0) {
         return false;
      } else {
         return Float.compare(this.amount3, rectangle.amount3) != 0 ? false : Float.compare(this.amount4, rectangle.amount4) == 0;
      }
   }
}