// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import su.catlean.api.data.UniformData;
import su.catlean.client.render.uniforms.UniformBlockH;
import su.catlean.utils.render.MeshBuilder;

public final class TexturedQuadRenderer {
   @NotNull
   private List<TexturedQuadRenderer.PlateRenderState> items = new ArrayList();

   public TexturedQuadRenderer() {
   }

   public final void render(Matrix3x2fStack stack, Color c1, Color c2, boolean flag) {
      float value = 0.0F;
      if (!this.items.isEmpty()) {
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, this.items.size(), false, 4, null);
         Iterable<TexturedQuadRenderer.PlateRenderState> items2 = this.items;

         for (Object object : items2) {
            TexturedQuadRenderer.PlateRenderState plateRenderState = (TexturedQuadRenderer.PlateRenderState)object;
            float coerceIn = RangesKt.coerceIn(plateRenderState.getAmount3() / 10000.0F, 0.0F, 1.0F);
            float coerceIn2 = RangesKt.coerceIn(plateRenderState.getAmount4() / 10000.0F, 0.0F, 1.0F);
            float coerceIn3 = RangesKt.coerceIn(plateRenderState.getAmount5(), 0.0F, 1.0F);
            meshBuilder.computeMeshBuilder(plateRenderState.getM(), plateRenderState.getAmount() - 1.0F, plateRenderState.getAmount2() + plateRenderState.getAmount4() + 2.0F, value)
               .computeMeshBuilder(coerceIn, coerceIn2)
               .fetchAlpha(new Color(0.0F, 0.0F, coerceIn3, coerceIn3))
               .computeMeshBuilder(plateRenderState.getM(), plateRenderState.getAmount() - 1.0F, plateRenderState.getAmount2() - 1.0F, value)
               .computeMeshBuilder(coerceIn, coerceIn2)
               .fetchAlpha(new Color(0.0F, 0.0F, coerceIn3, coerceIn3))
               .computeMeshBuilder(plateRenderState.getM(), plateRenderState.getAmount() + plateRenderState.getAmount3() + 2.0F, plateRenderState.getAmount2() - 1.0F, value)
               .computeMeshBuilder(coerceIn, coerceIn2)
               .fetchAlpha(new Color(0.0F, 0.0F, coerceIn3, coerceIn3))
               .computeMeshBuilder(plateRenderState.getM(), plateRenderState.getAmount() + plateRenderState.getAmount3() + 2.0F, plateRenderState.getAmount2() + plateRenderState.getAmount4() + 2.0F, value)
               .computeMeshBuilder(coerceIn, coerceIn2)
               .fetchAlpha(new Color(0.0F, 0.0F, coerceIn3, coerceIn3));
            value += 0.1F;
         }

         RenderPipeline renderPipeline12 = flag ? Pipelines.INSTANCE.getRenderPipeline12() : Pipelines.INSTANCE.getRenderPipeline11();
         Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)stack);
         GpuBufferSlice writeUniform = UniformBlockH.INSTANCE.getDynamicUniformStorage().writeUniform(new UniformData(3.0F, 3.0F, 3.0F, 3.0F, 0.75F, c1, c2, 0.0F));
         Map<String, GpuTextureView> mapOf = MapsKt.mapOf(TuplesKt.to("Sampler0", BlurService.INSTANCE.getGpuTextureView()));
         MeshBuilder.defaultImpl(meshBuilder, renderPipeline12, null, writeUniform, null, matrix3x2f, mapOf, null, 10, null);
         this.items.clear();
      }
   }

   public final void render(Matrix3x2fStack m, float amount, float amount2, float amount3, float amount4, float amount5) {
      Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)m);
      this.items.add(new TexturedQuadRenderer.PlateRenderState(matrix3x2f, amount, amount2, amount3, amount4, amount5));
   }

   public static void defaultImpl(
      TexturedQuadRenderer texturedQuadRenderer,
      Matrix3x2fStack matrix3x2fStack,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      int value,
      Object object
   ) {
      if ((value & 32) != 0) {
         amount5 = 1.0F;
      }

      texturedQuadRenderer.render(matrix3x2fStack, amount, amount2, amount3, amount4, amount5);
   }

   public static final class PlateRenderState {
      @NotNull
      private final Matrix3x2f m;
      private final float amount;
      private final float amount2;
      private final float amount3;
      private final float amount4;
      private final float amount5;

      public PlateRenderState(Matrix3x2f m, float amount, float amount2, float amount3, float amount4, float amount5) {
         this.m = m;
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.amount4 = amount4;
         this.amount5 = amount5;
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
      public final TexturedQuadRenderer.PlateRenderState computePlateRenderState(
         Matrix3x2f m, float amount, float amount2, float amount3, float amount4, float amount5
      ) {
         return new TexturedQuadRenderer.PlateRenderState(m, amount, amount2, amount3, amount4, amount5);
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
         return "PlateRenderState(m=" + matrix3x2f + ", x=" + amount6 + ", y=" + amount22 + ", w=" + amount32 + ", h=" + amount42 + ", a=" + amount52 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.m.hashCode();
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
         } else if (!(object instanceof TexturedQuadRenderer.PlateRenderState plateRenderState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.m, plateRenderState.m)) {
            return false;
         } else if (Float.compare(this.amount, plateRenderState.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, plateRenderState.amount2) != 0) {
            return false;
         } else if (Float.compare(this.amount3, plateRenderState.amount3) != 0) {
            return false;
         } else {
            return Float.compare(this.amount4, plateRenderState.amount4) != 0 ? false : Float.compare(this.amount5, plateRenderState.amount5) == 0;
         }
      }
   }
}