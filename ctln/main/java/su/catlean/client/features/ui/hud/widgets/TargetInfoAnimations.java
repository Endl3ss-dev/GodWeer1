// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import kotlin.jvm.internal.DefaultConstructorMarker;

public class TargetInfoAnimations {
   private float amount;
   private float amount2;
   private float amount3;
   private float amount4;
   private int value;
   private float amount5;
   private float amount6;
   private boolean flag;

   public TargetInfoAnimations(float amount, float amount2, float amount3, float amount4, int value, float amount5, float amount6, boolean flag) {
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.value = value;
      this.amount5 = amount5;
      this.amount6 = amount6;
      this.flag = flag;
   }

   public TargetInfoAnimations(
      float amount,
      float amount2,
      float amount3,
      float amount4,
      int value,
      float amount5,
      float amount6,
      boolean flag,
      int value2,
      DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value2 & 32) != 0) {
         amount5 = amount;
      }

      if ((value2 & 64) != 0) {
         amount6 = amount2;
      }

      if ((value2 & 128) != 0) {
         flag = false;
      }

      this(amount, amount2, amount3, amount4, value, amount5, amount6, flag);
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   public final float getAmount3() {
      return this.amount3;
   }

   public final void setAmount3(float amount) {
      this.amount3 = amount;
   }

   public final float getAmount4() {
      return this.amount4;
   }

   public final void setAmount4(float amount) {
      this.amount4 = amount;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   public final float getAmount5() {
      return this.amount5;
   }

   public final void setAmount5(float amount) {
      this.amount5 = amount;
   }

   public final float getAmount6() {
      return this.amount6;
   }

   public final void setAmount6(float amount) {
      this.amount6 = amount;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }
}