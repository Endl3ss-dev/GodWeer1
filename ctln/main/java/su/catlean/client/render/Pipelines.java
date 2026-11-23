// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.ArrayList;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.ShaderApplyEvent;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Identifiers;

public final class Pipelines {
   @NotNull
   public static final Pipelines INSTANCE = new Pipelines();
   @NotNull
   private static final List<RenderPipeline> items = new ArrayList();
   private static boolean flag;
   @NotNull
   private static final Snippet snippet = PipelineBuilders.computeBuilder2(RenderPipeline.builder(new Snippet[0]), "VertexUBO", UniformType.UNIFORM_BUFFER)
      .buildSnippet();
   @NotNull
   private static final Lazy<RenderPipeline> lazy = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/default")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/post"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/default/default"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy2 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/camouflage")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/post"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/camouflage/camouflage"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy3 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/doubleglow")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/post"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/doubleglow/doubleglow"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy4 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/mirror")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/post"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/mirror/mirror"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(
               PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "Sampler1"), "UBO", UniformType.UNIFORM_BUFFER
            )
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy5 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/bloom")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/post"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/bloom/bloom"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(
               PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "Sampler1"), "UBO", UniformType.UNIFORM_BUFFER
            )
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy6 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/multidefault")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/multidefault/multidefault"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/multidefault/multidefault"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy7 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/lines")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/lines/lines"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/lines/lines"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy8 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/kawase/downscale")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/post"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/kawase/downscale"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy9 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/post/kawase/upscale")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "post/post"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "post/kawase/upscale"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy10 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/rect/textured")),
               DefaultVertexFormat.POSITION,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/rect/textured_rect"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/rect/textured_rect"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy11 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/rect/blurred")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/rect/blurred_rect"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/rect/blurred_rect"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy12 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/rect/blurred")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/rect/blurred_rect"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/rect/blurred_rect"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0"), "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy13 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/rect/rect")),
               DefaultVertexFormat.POSITION_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/rect/rect"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/rect/rect"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(withFragmentShader, "UBO", UniformType.UNIFORM_BUFFER)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy14 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/colored_texture")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0")
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy15 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/colored_texture_depth")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0")
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy16 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/colored_texture_cull")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0")
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(true)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy17 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/colored_texture_cull_depth")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0")
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy18 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/colored_texture_alt_blend")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0")
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy19 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/colored_texture_alt_blend_depthtest")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0")
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy20 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         Builder withFragmentShader = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/colored_texture_alt_blend_2")),
               DefaultVertexFormat.POSITION_TEX_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/colored_texture"));
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(withFragmentShader, "Sampler0")
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(new BlendFunction(BlendFactor.ZERO, BlendFactor.ONE_MINUS_SRC_COLOR, BlendFactor.ONE, BlendFactor.ZERO)))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy21 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/pos_color_alt_blend_depth")),
               DefaultVertexFormat.POSITION_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy22 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/pos_color")),
               DefaultVertexFormat.POSITION_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy23 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/pos_color_depth")),
               DefaultVertexFormat.POSITION_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy24 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/pos_color_no_blend")),
               DefaultVertexFormat.POSITION_COLOR,
               PrimitiveTopology.TRIANGLES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy25 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/pos_color_lines")),
               DefaultVertexFormat.POSITION_COLOR,
               PrimitiveTopology.DEBUG_LINES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );
   @NotNull
   private static final Lazy<RenderPipeline> lazy26 = LazyKt.lazy(
      () -> {
         Pipelines instance = INSTANCE;
         Snippet[] snippets = new Snippet[]{snippet};
         RenderPipeline renderPipeline = PipelineBuilders.computeBuilder2(
               RenderPipeline.builder(snippets).withLocation(Identifiers.computeIdentifier("catlean", "program/default/pos_color_lines_alt")),
               DefaultVertexFormat.POSITION_COLOR,
               PrimitiveTopology.DEBUG_LINES
            )
            .withVertexShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withFragmentShader(Identifiers.computeIdentifier("catlean", "core/default/pos_color"))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .build();
         return instance.computeRenderPipeline(renderPipeline);
      }
   );

   private Pipelines() {
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      Pipelines.flag = flag;
   }

   @NotNull
   public final RenderPipeline getRenderPipeline() {
      Lazy<RenderPipeline> lazy27 = lazy;
      return (RenderPipeline)lazy27.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline2() {
      Lazy<RenderPipeline> lazy27 = lazy2;
      return (RenderPipeline)lazy27.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline3() {
      Lazy<RenderPipeline> lazy32 = lazy3;
      return (RenderPipeline)lazy32.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline4() {
      Lazy<RenderPipeline> lazy42 = lazy4;
      return (RenderPipeline)lazy42.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline5() {
      Lazy<RenderPipeline> lazy52 = lazy5;
      return (RenderPipeline)lazy52.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline6() {
      Lazy<RenderPipeline> lazy62 = lazy6;
      return (RenderPipeline)lazy62.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline7() {
      Lazy<RenderPipeline> lazy72 = lazy7;
      return (RenderPipeline)lazy72.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline8() {
      Lazy<RenderPipeline> lazy82 = lazy8;
      return (RenderPipeline)lazy82.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline9() {
      Lazy<RenderPipeline> lazy92 = lazy9;
      return (RenderPipeline)lazy92.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline10() {
      Lazy<RenderPipeline> lazy102 = lazy10;
      return (RenderPipeline)lazy102.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline11() {
      Lazy<RenderPipeline> lazy112 = lazy11;
      return (RenderPipeline)lazy112.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline12() {
      Lazy<RenderPipeline> lazy122 = lazy12;
      return (RenderPipeline)lazy122.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline13() {
      Lazy<RenderPipeline> lazy132 = lazy13;
      return (RenderPipeline)lazy132.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline14() {
      Lazy<RenderPipeline> lazy142 = lazy14;
      return (RenderPipeline)lazy142.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline15() {
      Lazy<RenderPipeline> lazy152 = lazy15;
      return (RenderPipeline)lazy152.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline16() {
      Lazy<RenderPipeline> lazy162 = lazy16;
      return (RenderPipeline)lazy162.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline17() {
      Lazy<RenderPipeline> lazy172 = lazy17;
      return (RenderPipeline)lazy172.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline18() {
      Lazy<RenderPipeline> lazy182 = lazy18;
      return (RenderPipeline)lazy182.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline19() {
      Lazy<RenderPipeline> lazy192 = lazy19;
      return (RenderPipeline)lazy192.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline20() {
      Lazy<RenderPipeline> lazy202 = lazy20;
      return (RenderPipeline)lazy202.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline21() {
      Lazy<RenderPipeline> lazy212 = lazy21;
      return (RenderPipeline)lazy212.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline22() {
      Lazy<RenderPipeline> lazy222 = lazy22;
      return (RenderPipeline)lazy222.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline23() {
      Lazy<RenderPipeline> lazy232 = lazy23;
      return (RenderPipeline)lazy232.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline24() {
      Lazy<RenderPipeline> lazy242 = lazy24;
      return (RenderPipeline)lazy242.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline25() {
      Lazy<RenderPipeline> lazy252 = lazy25;
      return (RenderPipeline)lazy252.getValue();
   }

   @NotNull
   public final RenderPipeline getRenderPipeline26() {
      Lazy<RenderPipeline> lazy262 = lazy26;
      return (RenderPipeline)lazy262.getValue();
   }

   @Flow
   private final void onShaderApply(ShaderApplyEvent shaderApplyEvent) {
      RenderSystem.getDevice().clearPipelineCache();

      for (Object item : items) {
         RenderPipeline renderPipeline = (RenderPipeline)item;
         RenderSystem.getDevice().precompilePipeline(renderPipeline, (arg, arg2) -> MinecraftHolder.getMinecraft().getShaderManager().getShader(arg, arg2));
      }

      flag = true;
   }

   private final RenderPipeline computeRenderPipeline(RenderPipeline renderPipeline) {
      items.add(renderPipeline);
      return renderPipeline;
   }
}