// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.VertexFormat;

public final class PipelineBuilders {
   private static final Builder computeBuilder(Builder builder, String text, UniformType uniformType) {
      return builder.withBindGroupLayout(BindGroupLayout.builder().withUniform(text, uniformType).build());
   }

   private static final Builder computeBuilder(Builder builder, String text) {
      return builder.withBindGroupLayout(BindGroupLayout.builder().withSampler(text).build());
   }

   private static final Builder computeBuilder(Builder builder, VertexFormat vertexFormat, PrimitiveTopology primitiveTopology) {
      return builder.withVertexBinding(0, vertexFormat).withPrimitiveTopology(primitiveTopology);
   }

   public static final Builder computeBuilder2(Builder builder, String text, UniformType uniformType) {
      return computeBuilder(builder, text, uniformType);
   }

   public static final Builder computeBuilder2(Builder builder, String text) {
      return computeBuilder(builder, text);
   }

   public static final Builder computeBuilder2(Builder builder, VertexFormat vertexFormat, PrimitiveTopology primitiveTopology) {
      return computeBuilder(builder, vertexFormat, primitiveTopology);
   }
}