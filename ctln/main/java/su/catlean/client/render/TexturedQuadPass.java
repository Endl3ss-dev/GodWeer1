// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.awt.Color;
import java.util.Map;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.ranges.RangesKt;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import su.catlean.api.data.UniformData;
import su.catlean.client.render.uniforms.UniformBlockH;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;

public final class TexturedQuadPass {
   public static final void render(
      Matrix3x2fStack stack,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      Color colorValue,
      Color outlineColorValue,
      float amount9,
      float amount10,
      float amount11
   ) {
      float coerceIn = RangesKt.coerceIn(amount3 / 10000.0F, 0.0F, 1.0F);
      float coerceIn2 = RangesKt.coerceIn(amount4 / 10000.0F, 0.0F, 1.0F);
      float coerceIn3 = RangesKt.coerceIn(amount8, 0.0F, 1.0F);
      MeshBuilder alpha = MeshBuilder.defaultImpl(
            MeshBuilder.defaultImpl(
                  MeshBuilder.defaultImpl(
                        MeshBuilder.defaultImpl(
                              new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 0, false, 6, null),
                              amount - 1.0F,
                              amount2 + amount4 + 2.0F,
                              0.0F,
                              4,
                              null
                           )
                           .computeMeshBuilder(coerceIn, coerceIn2)
                           .fetchAlpha(new Color(0.0F, 0.0F, coerceIn3, coerceIn3)),
                        amount - 1.0F,
                        amount2 - 1.0F,
                        0.0F,
                        4,
                        null
                     )
                     .computeMeshBuilder(coerceIn, coerceIn2)
                     .fetchAlpha(new Color(0.0F, 0.0F, coerceIn3, coerceIn3)),
                  amount + amount3 + 2.0F,
                  amount2 - 1.0F,
                  0.0F,
                  4,
                  null
               )
               .computeMeshBuilder(coerceIn, coerceIn2)
               .fetchAlpha(new Color(0.0F, 0.0F, coerceIn3, coerceIn3)),
            amount + amount3 + 2.0F,
            amount2 + amount4 + 2.0F,
            0.0F,
            4,
            null
         )
         .computeMeshBuilder(coerceIn, coerceIn2)
         .fetchAlpha(new Color(0.0F, 0.0F, coerceIn3, coerceIn3));
      RenderPipeline renderPipeline11 = Pipelines.INSTANCE.getRenderPipeline11();
      Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)stack);
      GpuBufferSlice writeUniform = UniformBlockH.INSTANCE
         .getDynamicUniformStorage()
         .writeUniform(
            new UniformData(
               amount5,
               amount9,
               amount10,
               amount11,
               amount6 / 2.0F,
               Render2D.INSTANCE.computeColor(colorValue, Render2D.INSTANCE.fetchAlpha(colorValue) * Render2D.INSTANCE.getAmount()),
               Render2D.INSTANCE.computeColor(outlineColorValue, Render2D.INSTANCE.fetchAlpha(outlineColorValue) * Render2D.INSTANCE.getAmount()),
               amount7
            )
         );
      Map<String, GpuTextureView> mapOf = MapsKt.mapOf(TuplesKt.to("Sampler0", BlurService.INSTANCE.getGpuTextureView()));
      MeshBuilder.defaultImpl(alpha, renderPipeline11, null, writeUniform, null, matrix3x2f, mapOf, null, 74, null);
   }

   public static void defaultImpl(
      Matrix3x2fStack matrix3x2fStack,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      Color color,
      Color color2,
      float amount9,
      float amount10,
      float amount11,
      int value,
      Object object
   ) {
      if ((value & 2048) != 0) {
         amount9 = amount5;
      }

      if ((value & 4096) != 0) {
         amount10 = amount5;
      }

      if ((value & 8192) != 0) {
         amount11 = amount5;
      }

      render(matrix3x2fStack, amount, amount2, amount3, amount4, amount5, amount6, amount7, amount8, color, color2, amount9, amount10, amount11);
   }
}