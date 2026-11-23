// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.awt.Color;
import java.nio.ByteBuffer;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import su.catlean.client.render.uniforms.UniformBlockA;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;

public final class BoxPass {
   public static final void render(
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      Color colorValue1,
      Color colorValue2,
      Color colorValue3,
      Color colorValue4,
      Matrix3x2fStack matrix,
      float amount6,
      Color outlineColorValue
   ) {
      MeshBuilder alpha = MeshBuilder.defaultImpl(
            MeshBuilder.defaultImpl(
                  MeshBuilder.defaultImpl(
                        MeshBuilder.defaultImpl(
                              new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, 0, false, 6, null), amount - 1.0F, amount2 - 1.0F, 0.0F, 4, null
                           )
                           .fetchAlpha(colorValue2),
                        amount - 1.0F,
                        amount2 + amount4 + 2.0F,
                        0.0F,
                        4,
                        null
                     )
                     .fetchAlpha(colorValue1),
                  amount + amount3 + 2.0F,
                  amount2 + amount4 + 2.0F,
                  0.0F,
                  4,
                  null
               )
               .fetchAlpha(colorValue3),
            amount + amount3 + 2.0F,
            amount2 - 1.0F,
            0.0F,
            4,
            null
         )
         .fetchAlpha(colorValue4);
      RenderPipeline renderPipeline13 = Pipelines.INSTANCE.getRenderPipeline13();
      RenderTarget mainRenderTarget = MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget();
      Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)matrix);
      GpuBufferSlice writeUniform = UniformBlockA.INSTANCE
         .getDynamicUniformStorage()
         .writeUniform(new BoxPass.UniformData(amount3, amount4, amount5, amount6 / 2.0F, outlineColorValue));
      MeshBuilder.defaultImpl(alpha, renderPipeline13, mainRenderTarget, writeUniform, null, matrix3x2f, null, null, 104, null);
   }

   public static void defaultImpl(
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      Color color,
      Color color2,
      Color color3,
      Color color4,
      Matrix3x2fStack matrix3x2fStack,
      float amount6,
      Color color5,
      int value,
      Object object
   ) {
      if ((value & 1024) != 0) {
         amount6 = 0.0F;
      }

      if ((value & 2048) != 0) {
         color5 = Color.WHITE;
      }

      render(amount, amount2, amount3, amount4, amount5, color, color2, color3, color4, matrix3x2fStack, amount6, color5);
   }

   public static final class UniformData implements DynamicUniform {
      private final float amount;
      private final float amount2;
      private final float amount3;
      private final float amount4;
      @NotNull
      private final Color uBorderColor;

      public UniformData(float amount, float amount2, float amount3, float amount4, Color uBorderColor) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.amount4 = amount4;
         this.uBorderColor = uBorderColor;
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

      @NotNull
      public final Color getUBorderColor() {
         return this.uBorderColor;
      }

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer)
            .putVec2(this.amount, this.amount2)
            .putFloat(this.amount3)
            .putFloat(this.amount4)
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.uBorderColor),
               Render2D.INSTANCE.fetchGreen(this.uBorderColor),
               Render2D.INSTANCE.fetchBlue(this.uBorderColor),
               Render2D.INSTANCE.fetchAlpha(this.uBorderColor)
            );
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
      public final Color getUBorderColor2() {
         return this.uBorderColor;
      }

      @NotNull
      @Override
      public String toString() {
         Color uBorderColor2 = this.uBorderColor;
         float amount42 = this.amount4;
         float amount32 = this.amount3;
         float amount22 = this.amount2;
         float amount5 = this.amount;
         return "UniformData(uSizeX=" + amount5 + ", uSizeY=" + amount22 + ", radius=" + amount32 + ", uBorderWidth=" + amount42 + ", uBorderColor=" + uBorderColor2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + Float.hashCode(this.amount3);
         hashCode = hashCode * 31 + Float.hashCode(this.amount4);
         return hashCode * 31 + this.uBorderColor.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof BoxPass.UniformData uniformData)) {
            return false;
         } else if (Float.compare(this.amount, uniformData.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, uniformData.amount2) != 0) {
            return false;
         } else if (Float.compare(this.amount3, uniformData.amount3) != 0) {
            return false;
         } else {
            return Float.compare(this.amount4, uniformData.amount4) != 0 ? false : Intrinsics.areEqual(this.uBorderColor, uniformData.uBorderColor);
         }
      }
   }
}