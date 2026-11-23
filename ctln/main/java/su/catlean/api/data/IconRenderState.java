// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import su.catlean.client.render.SvgTexture;

public final class IconRenderState {
   @NotNull
   private final SvgTexture image;
   @NotNull
   private final Matrix3x2f m;
   private final float amount;
   private final float amount2;
   private final float amount3;
   private final float amount4;
   private final float amount5;

   public IconRenderState(SvgTexture image, Matrix3x2f m, float amount, float amount2, float amount3, float amount4, float amount5) {
      this.image = image;
      this.m = m;
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.amount5 = amount5;
   }

   @NotNull
   public final SvgTexture getImage() {
      return this.image;
   }

   @NotNull
   public final Matrix3x2f getM() {
      return this.m;
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
   public final SvgTexture getImage2() {
      return this.image;
   }

   @NotNull
   public final Matrix3x2f getM2() {
      return this.m;
   }

   public final float getAmount6() {
      return this.amount;
   }

   public final float getAmount7() {
      return this.amount2;
   }

   public final float getAmount8() {
      return this.amount3;
   }

   public final float getAmount9() {
      return this.amount4;
   }

   public final float getAmount10() {
      return this.amount5;
   }

   @NotNull
   @Override
   public String toString() {
      float amount52 = this.amount5;
      float amount42 = this.amount4;
      float amount32 = this.amount3;
      float amount22 = this.amount2;
      float amount6 = this.amount;
      Matrix3x2f matrix3x2f = this.m;
      SvgTexture image2 = this.image;
      return "IconRenderState(image=" + image2 + ", m=" + matrix3x2f + ", x=" + amount6 + ", y=" + amount22 + ", w=" + amount32 + ", h=" + amount42 + ", a=" + amount52 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.image.hashCode();
      hashCode = hashCode * 31 + this.m.hashCode();
      hashCode = hashCode * 31 + Float.hashCode(this.amount);
      hashCode = hashCode * 31 + Float.hashCode(this.amount2);
      hashCode = hashCode * 31 + Float.hashCode(this.amount3);
      hashCode = hashCode * 31 + Float.hashCode(this.amount4);
      return hashCode * 31 + Float.hashCode(this.amount5);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof IconRenderState iconRenderState)) {
         return false;
      } else if (!Intrinsics.areEqual(this.image, iconRenderState.image)) {
         return false;
      } else if (!Intrinsics.areEqual(this.m, iconRenderState.m)) {
         return false;
      } else if (Float.compare(this.amount, iconRenderState.amount) != 0) {
         return false;
      } else if (Float.compare(this.amount2, iconRenderState.amount2) != 0) {
         return false;
      } else if (Float.compare(this.amount3, iconRenderState.amount3) != 0) {
         return false;
      } else {
         return Float.compare(this.amount4, iconRenderState.amount4) != 0 ? false : Float.compare(this.amount5, iconRenderState.amount5) == 0;
      }
   }
}