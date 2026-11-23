// deobf by pusia
package su.catlean.client.render.font;

import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import su.catlean.api.data.Box;
import su.catlean.utils.render.ScissorStack;

public final class GlyphMatrix {
   private final float amount;
   private final float amount2;
   private final float amount3;
   private final float amount4;
   private final float amount5;
   private final float amount6;
   private final int value;
   private final int value2;
   private final int value3;
   private final int value4;
   private final int value5;
   private final int value6;
   private final float amount7;
   @NotNull
   private final Matrix3x2f matrix;
   @Nullable
   private final Box box;

   public GlyphMatrix(
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      float amount6,
      int value,
      int value2,
      int value3,
      int value4,
      int value5,
      int value6,
      float amount7,
      Matrix3x2f matrix,
      Box box
   ) {
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.amount5 = amount5;
      this.amount6 = amount6;
      this.value = value;
      this.value2 = value2;
      this.value3 = value3;
      this.value4 = value4;
      this.value5 = value5;
      this.value6 = value6;
      this.amount7 = amount7;
      this.matrix = matrix;
      this.box = box;
   }

   public GlyphMatrix(
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      float amount6,
      int value,
      int value2,
      int value3,
      int value4,
      int value5,
      int value6,
      float amount7,
      Matrix3x2f matrix3x2f,
      Box box,
      int value7,
      DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value7 & 16384) != 0) {
         box = ScissorStack.INSTANCE.getBox2();
      }

      this(amount, amount2, amount3, amount4, amount5, amount6, value, value2, value3, value4, value5, value6, amount7, matrix3x2f, box);
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
      return this.amount5;
   }

   public final float getAmount6() {
      return this.amount6;
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
      return this.value5;
   }

   public final int getValue6() {
      return this.value6;
   }

   public final float getAmount7() {
      return this.amount7;
   }

   @NotNull
   public final Matrix3x2f getMatrix() {
      return this.matrix;
   }

   @Nullable
   public final Box getBox() {
      return this.box;
   }
}