// deobf by pusia
package su.catlean.api.data;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.awt.Color;
import java.nio.ByteBuffer;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.render.Render2D;

public final class UniformData implements DynamicUniform {
   private final float amount;
   private final float amount2;
   private final float amount3;
   private final float amount4;
   private final float amount5;
   @NotNull
   private final Color uColor;
   @NotNull
   private final Color uBorderColor;
   private final float amount6;

   public UniformData(float amount, float amount2, float amount3, float amount4, float amount5, Color uColor, Color uBorderColor, float amount6) {
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.amount5 = amount5;
      this.uColor = uColor;
      this.uBorderColor = uBorderColor;
      this.amount6 = amount6;
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

   @NotNull
   public final Color getUColor() {
      return this.uColor;
   }

   @NotNull
   public final Color getUBorderColor() {
      return this.uBorderColor;
   }

   public final float getAmount6() {
      return this.amount6;
   }

   public void write(ByteBuffer buffer) {
      Std140Builder.intoBuffer(buffer)
         .putVec4(this.amount2, this.amount, this.amount4, this.amount3)
         .putFloat(this.amount5)
         .putVec4(
            Render2D.INSTANCE.fetchRed(this.uColor),
            Render2D.INSTANCE.fetchGreen(this.uColor),
            Render2D.INSTANCE.fetchBlue(this.uColor),
            Render2D.INSTANCE.fetchAlpha(this.uColor)
         )
         .putVec4(
            Render2D.INSTANCE.fetchRed(this.uBorderColor),
            Render2D.INSTANCE.fetchGreen(this.uBorderColor),
            Render2D.INSTANCE.fetchBlue(this.uBorderColor),
            Render2D.INSTANCE.fetchAlpha(this.uBorderColor)
         )
         .putFloat(this.amount6);
   }

   public final float getAmount7() {
      return this.amount;
   }

   public final float getAmount8() {
      return this.amount2;
   }

   public final float getAmount9() {
      return this.amount3;
   }

   public final float getAmount10() {
      return this.amount4;
   }

   public final float getAmount11() {
      return this.amount5;
   }

   @NotNull
   public final Color getUColor2() {
      return this.uColor;
   }

   @NotNull
   public final Color getUBorderColor2() {
      return this.uBorderColor;
   }

   public final float getAmount12() {
      return this.amount6;
   }

   @NotNull
   @Override
   public String toString() {
      float amount62 = this.amount6;
      Color uBorderColor2 = this.uBorderColor;
      Color uColor2 = this.uColor;
      float amount52 = this.amount5;
      float amount42 = this.amount4;
      float amount32 = this.amount3;
      float amount22 = this.amount2;
      float amount7 = this.amount;
      return "UniformData(uRadius1="
         + amount7
         + ", uRadius2="
         + amount22
         + ", uRadius3="
         + amount32
         + ", uRadius4="
         + amount42
         + ", uBorderWidth="
         + amount52
         + ", uColor="
         + uColor2
         + ", uBorderColor="
         + uBorderColor2
         + ", uMix="
         + amount62
         + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Float.hashCode(this.amount);
      hashCode = hashCode * 31 + Float.hashCode(this.amount2);
      hashCode = hashCode * 31 + Float.hashCode(this.amount3);
      hashCode = hashCode * 31 + Float.hashCode(this.amount4);
      hashCode = hashCode * 31 + Float.hashCode(this.amount5);
      hashCode = hashCode * 31 + this.uColor.hashCode();
      hashCode = hashCode * 31 + this.uBorderColor.hashCode();
      return hashCode * 31 + Float.hashCode(this.amount6);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof UniformData uniformData)) {
         return false;
      } else if (Float.compare(this.amount, uniformData.amount) != 0) {
         return false;
      } else if (Float.compare(this.amount2, uniformData.amount2) != 0) {
         return false;
      } else if (Float.compare(this.amount3, uniformData.amount3) != 0) {
         return false;
      } else if (Float.compare(this.amount4, uniformData.amount4) != 0) {
         return false;
      } else if (Float.compare(this.amount5, uniformData.amount5) != 0) {
         return false;
      } else if (!Intrinsics.areEqual(this.uColor, uniformData.uColor)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.uBorderColor, uniformData.uBorderColor) ? false : Float.compare(this.amount6, uniformData.amount6) == 0;
      }
   }
}