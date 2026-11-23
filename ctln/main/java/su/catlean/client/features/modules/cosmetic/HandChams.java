// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.nio.ByteBuffer;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.render.FrameBufferEvent;
import su.catlean.api.event.events.render.PostRenderHandEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.render.RenderHandEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.esp.PlayerEsp;
import su.catlean.client.render.BlurService;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.uniforms.UniformBlockE;
import su.catlean.client.render.uniforms.UniformBlockF;
import su.catlean.client.render.uniforms.UniformBlockG;
import su.catlean.client.render.uniforms.UniformBlockJ;
import su.catlean.client.render.uniforms.UniformBlockK;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.GameRendererAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class HandChams extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(HandChams.class, "handsMode", "getHandsMode()Lsu/catlean/kl;", 0), new MutablePropertyReference1Impl(HandChams.class, "handsColor", "getHandsColor()Ljava/awt/Color;", 0), new MutablePropertyReference1Impl(HandChams.class, "glass", "getGlass()Z", 0), new MutablePropertyReference1Impl(HandChams.class, "alpha", "getAlpha()F", 0), new MutablePropertyReference1Impl(HandChams.class, "borders", "getBorders()Z", 0), new MutablePropertyReference1Impl(HandChams.class, "radiusValue", "getRadiusValue()F", 0), new MutablePropertyReference1Impl(HandChams.class, "distanceValue", "getDistanceValue()F", 0), new MutablePropertyReference1Impl(HandChams.class, "glowAlphaValue", "getGlowAlphaValue()F", 0), new MutablePropertyReference1Impl(HandChams.class, "lineWithValue", "getLineWithValue()F", 0), new MutablePropertyReference1Impl(HandChams.class, "colorModeValue", "getColorModeValue()Lsu/catlean/d5;", 0), new MutablePropertyReference1Impl(HandChams.class, "secondColor", "getSecondColor()Ljava/awt/Color;", 0), new MutablePropertyReference1Impl(HandChams.class, "glowQualityValue", "getGlowQualityValue()I", 0), new MutablePropertyReference1Impl(HandChams.class, "glowRadiusValue", "getGlowRadiusValue()F", 0), new MutablePropertyReference1Impl(HandChams.class, "glowAlphaMultValue", "getGlowAlphaMultValue()F", 0), new MutablePropertyReference1Impl(HandChams.class, "fillAlphaMultValue", "getFillAlphaMultValue()F", 0), new MutablePropertyReference1Impl(HandChams.class, "saturation", "getSaturation()F", 0), new MutablePropertyReference1Impl(HandChams.class, "brightness", "getBrightness()F", 0) };
   @NotNull
   public static final HandChams INSTANCE = new HandChams();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", PlayerEsp.ShaderMode.MIRROR, null, null, 12, null);
   @NotNull
   private static final ColorSetting color = SettingFactory.defaultImpl(INSTANCE, "color", new Color(200, 200, 255), null, null, 12, null);
   @NotNull
   private static final BooleanSetting glass = SettingFactory.defaultImpl(
      INSTANCE, "glass", false, null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.MIRROR, 4, null
   );
   @NotNull
   private static final FloatSetting alpha = SettingFactory.defaultImpl(
      INSTANCE, "alpha", 0.2F, RangesKt.rangeTo(0.0F, 2.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.MIRROR, 8, null
   );
   @NotNull
   private static final BooleanSetting border = SettingFactory.defaultImpl(
      INSTANCE, "border", false, null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.MIRROR, 4, null
   );
   @NotNull
   private static final FloatSetting radius = SettingFactory.defaultImpl(
      INSTANCE, "radius", 6.0F, RangesKt.rangeTo(0.0F, 15.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.BLOOM, 8, null
   );
   @NotNull
   private static final FloatSetting distance = SettingFactory.defaultImpl(
      INSTANCE, "distance", 1.3F, RangesKt.rangeTo(0.0F, 15.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.BLOOM, 8, null
   );
   @NotNull
   private static final FloatSetting glowAlpha = SettingFactory.defaultImpl(
      INSTANCE, "glow-alpha", 1.7F, RangesKt.rangeTo(1.0F, 5.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.BLOOM, 8, null
   );
   @NotNull
   private static final FloatSetting lineWidth = SettingFactory.defaultImpl(
      INSTANCE,
      "line-width",
      4.0F,
      RangesKt.rangeTo(1.0F, 4.0F),
      null,
      () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DEFAULT
         || INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.BLOOM
         || INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.CAMOUFLAGE,
      8,
      null
   );
   @NotNull
   private static final EnumSetting colorMode = SettingFactory.defaultImpl(
      INSTANCE, "color-mode", PlayerEsp.ColorMode.RGB, null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DOUBLE, 4, null
   );
   @NotNull
   private static final ColorSetting secondColor = SettingFactory.defaultImpl(
      INSTANCE,
      "second-color",
      new Color(200, 200, 255),
      null,
      () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DOUBLE || INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.CAMOUFLAGE,
      4,
      null
   );
   @NotNull
   private static final IntSetting glowQuality = SettingFactory.defaultImpl(
      INSTANCE, "glow-quality", 2, new IntRange(1, 4), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DOUBLE, 8, null
   );
   @NotNull
   private static final FloatSetting glowRadius = SettingFactory.defaultImpl(
      INSTANCE, "glow-radius", 4.0F, RangesKt.rangeTo(1.0F, 16.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DOUBLE, 8, null
   );
   @NotNull
   private static final FloatSetting glowAlphaMultiplier = SettingFactory.defaultImpl(
      INSTANCE, "glow-alpha-multiplier", 2.0F, RangesKt.rangeTo(0.0F, 4.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DOUBLE, 8, null
   );
   @NotNull
   private static final FloatSetting fillAlphaMultiplier = SettingFactory.defaultImpl(
      INSTANCE, "fill-alpha-multiplier", 0.6F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DOUBLE, 8, null
   );
   @NotNull
   private static final FloatSetting saturation = SettingFactory.defaultImpl(
      INSTANCE, "saturation", 0.7F, RangesKt.rangeTo(0.0F, 5.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DOUBLE, 8, null
   );
   @NotNull
   private static final FloatSetting brightness = SettingFactory.defaultImpl(
      INSTANCE, "brightness", 2.0F, RangesKt.rangeTo(0.0F, 45.0F), null, () -> INSTANCE.getHandsMode() == PlayerEsp.ShaderMode.DOUBLE, 8, null
   );
   @Nullable
   private static MainTarget mainTarget;
   @Nullable
   private static MainTarget mainTarget2;
   @Nullable
   private static MainTarget mainTarget3;
   private static boolean flag;
   private static boolean flag2;
   @NotNull
   private static final float[] amounts = new float[16];
   private static int value = -1;

   private HandChams() {
      super("hand-chams", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final PlayerEsp.ShaderMode getHandsMode() {
      return (PlayerEsp.ShaderMode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final Color getHandsColor() {
      return (Color)color.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getGlass() {
      return (Boolean)glass.computeObject(this, $$delegatedProperties[2]);
   }

   private final float getAlpha() {
      return ((Number)alpha.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   private final boolean getBorders() {
      return (Boolean)border.computeObject(this, $$delegatedProperties[4]);
   }

   private final float getRadiusValue() {
      return ((Number)radius.computeObject(this, $$delegatedProperties[5])).floatValue();
   }

   private final float getDistanceValue() {
      return ((Number)distance.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   private final float getGlowAlphaValue() {
      return ((Number)glowAlpha.computeObject(this, $$delegatedProperties[7])).floatValue();
   }

   private final float getLineWithValue() {
      return ((Number)lineWidth.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   private final PlayerEsp.ColorMode getColorModeValue() {
      return (PlayerEsp.ColorMode)colorMode.computeObject(this, $$delegatedProperties[9]);
   }

   private final Color getSecondColor() {
      return (Color)secondColor.computeObject(this, $$delegatedProperties[10]);
   }

   private final int getGlowQualityValue() {
      return ((Number)glowQuality.computeObject(this, $$delegatedProperties[11])).intValue();
   }

   private final float getGlowRadiusValue() {
      return ((Number)glowRadius.computeObject(this, $$delegatedProperties[12])).floatValue();
   }

   private final float getGlowAlphaMultValue() {
      return ((Number)glowAlphaMultiplier.computeObject(this, $$delegatedProperties[13])).floatValue();
   }

   private final float getFillAlphaMultValue() {
      return ((Number)fillAlphaMultiplier.computeObject(this, $$delegatedProperties[14])).floatValue();
   }

   private final float getSaturation() {
      return ((Number)saturation.computeObject(this, $$delegatedProperties[15])).floatValue();
   }

   private final float getBrightness() {
      return ((Number)brightness.computeObject(this, $$delegatedProperties[16])).floatValue();
   }

   @Flow
   private final void onFrameBuffer(FrameBufferEvent frameBufferEvent) {
      if (flag2) {
         frameBufferEvent.setFrameBuffer((RenderTarget)mainTarget);
         frameBufferEvent.cancel();
      }
   }

   @Flow(
      priority = -10
   )
   private final void onRenderHand(RenderHandEvent renderHandEvent) {
      if (flag2 || flag) {
         renderHandEvent.cancel();
      }
   }

   @Flow(
      priority = -10
   )
   private final void onPostRenderHand(PostRenderHandEvent postRenderHandEvent) {
      if (mainTarget != null && mainTarget2 != null && mainTarget3 != null) {
         GofraState.INSTANCE.setModifyBuffer(false);
         flag2 = false;
         RenderSystem.getDevice()
            .createCommandEncoder()
            .clearDepthTexture(MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().getDepthTexture(), 0.0);
         this.applyBackupProjectionMatrix(null);
         GofraState.INSTANCE.setModifyBuffer(true);
         flag2 = true;
         this.applyDarker(
            this.getHandsMode(),
            this.getHandsColor(),
            mainTarget,
            mainTarget2,
            this.getGlass(),
            this.getBorders(),
            this.getRadiusValue(),
            this.getDistanceValue(),
            this.getGlowAlphaValue(),
            this.getLineWithValue(),
            this.getColorModeValue().ordinal(),
            this.getHandsColor(),
            this.getSecondColor(),
            this.getGlowQualityValue(),
            this.getGlowRadiusValue(),
            this.getGlowAlphaMultValue(),
            this.getFillAlphaMultValue(),
            this.getSaturation(),
            this.getBrightness(),
            this.getAlpha()
         );
         flag2 = false;
         GofraState.INSTANCE.setModifyBuffer(false);
      }
   }

   @Flow(
      priority = -10
   )
   public final void onRender3D(Render3DEvent event) {
      GofraState.INSTANCE.setModifyBuffer(true);
      flag2 = true;
      this.applyMainRenderTarget();
      this.applyBackupProjectionMatrix(mainTarget);
      flag2 = false;
      GofraState.INSTANCE.setModifyBuffer(false);
   }

   public final void applyDarker(
      PlayerEsp.ShaderMode shaderMode,
      Color color,
      MainTarget frameBuffer,
      MainTarget secondary,
      boolean flag,
      boolean flag2,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      int value,
      Color color1v,
      Color color2v,
      int value2,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      float amount9,
      float amount10
   ) {
      switch (shaderMode == null ? -1 : HandChams.WhenMappings.values[shaderMode.ordinal()]) {
         case 1:
            this.applyGetColorTextureView(frameBuffer, color, flag2, flag, amount10);
            break;
         case 2:
            this.applyGetColorTextureView(color, amount4, frameBuffer);
            break;
         case 3:
            this.applyGetWindow(color, frameBuffer, secondary, amount, amount2, amount3, amount4);
            break;
         case 4:
            this.applyAbs(value, color1v, color2v, value2, amount5, amount6, amount7, amount8, amount9, frameBuffer);
            break;
         case 5:
            Color darker = color1v.darker().darker();
            Color darker2 = color2v.darker().darker();
            this.applyAbs(color1v, color2v, darker, darker2, amount4, frameBuffer);
            break;
         default:
            this.updateColor(color, frameBuffer);
      }
   }

   private final void updateColor(Color color, MainTarget mainTarget) {
   }

   private final void applyGetWindow(Color color, MainTarget mainTarget, MainTarget mainTarget2, float amount, float amount2, float amount3, float amount4) {
      this.applyGetColorTextureView(color, amount4, mainTarget);
      int amount5 = (int)amount;
      if (value != amount5) {
         for (int i = amount5; -1 < i; i--) {
            amounts[i] = this.computeAmount(i);
         }

         value = amount5;
      }

      Matrix4f set = new Matrix4f()
         .set(
            amounts[0],
            amounts[1],
            amounts[2],
            amounts[3],
            amounts[4],
            amounts[5],
            amounts[6],
            amounts[7],
            amounts[8],
            amounts[9],
            amounts[10],
            amounts[11],
            amounts[12],
            amounts[13],
            amounts[14],
            amounts[15]
         );
      MeshBuilder.defaultImpl(
         new MeshBuilder(null, 0, false, 7, null).getMeshBuilder(),
         Pipelines.INSTANCE.getRenderPipeline5(),
         (RenderTarget)mainTarget2,
         UniformBlockJ.INSTANCE
            .getDynamicUniformStorage()
            .writeUniform(
               new HandChams.UniformData(
                  new Vec2(
                     1.0F / MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth(),
                     1.0F / MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight()
                  ),
                  new Vec2(amount2, 0.0F),
                  amount5,
                  amount3,
                  set,
                  color
               )
            ),
         null,
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler1", mainTarget.getColorTextureView())),
         null,
         88,
         null
      );
      MeshBuilder meshBuilder = new MeshBuilder(null, 0, false, 7, null).getMeshBuilder();
      RenderPipeline renderPipeline5 = Pipelines.INSTANCE.getRenderPipeline5();
      RenderTarget mainRenderTarget = MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget();
      GpuBufferSlice writeUniform = UniformBlockJ.INSTANCE
         .getDynamicUniformStorage()
         .writeUniform(
            new HandChams.UniformData(
               new Vec2(
                  1.0F / MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth(), 1.0F / MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight()
               ),
               new Vec2(0.0F, amount2),
               amount5,
               amount3,
               set,
               color
            )
         );
      Pair[] pairs = new Pair[]{TuplesKt.to("Sampler0", mainTarget.getColorTextureView()), TuplesKt.to("Sampler1", mainTarget2.getColorTextureView())};
      MeshBuilder.defaultImpl(meshBuilder, renderPipeline5, mainRenderTarget, writeUniform, null, null, MapsKt.mapOf(pairs), null, 88, null);
   }

   private final void applyMainRenderTarget() {
      if (mainTarget == null) {
         mainTarget = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != mainTarget.width
         || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != mainTarget.height) {
         mainTarget.resize(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
      commandEncoder.clearColorAndDepthTextures(mainTarget.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), mainTarget.getDepthTexture(), 0.0);
      if (mainTarget2 == null) {
         mainTarget2 = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != mainTarget2.width
         || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != mainTarget2.height) {
         mainTarget2.resize(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      commandEncoder.clearColorAndDepthTextures(mainTarget2.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), mainTarget2.getDepthTexture(), 0.0);
      if (mainTarget3 == null) {
         mainTarget3 = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != mainTarget3.width
         || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != mainTarget3.height) {
         mainTarget3.resize(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      commandEncoder.clearColorAndDepthTextures(mainTarget3.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), mainTarget3.getDepthTexture(), 0.0);
   }

   private final void applyBackupProjectionMatrix(MainTarget mainTarget) {
      RenderSystem.backupProjectionMatrix();
      flag = false;
      this.applyGetCameraEntity(Render3D.INSTANCE.getAmount4(), mainTarget);
      flag = true;
      RenderSystem.restoreProjectionMatrix();
   }

   private final void applyAbs(
      int value, Color color, Color color2, int value2, float amount, float amount2, float amount3, float amount4, float amount5, MainTarget mainTarget
   ) {
      MeshBuilder.defaultImpl(
         new MeshBuilder(null, 0, false, 7, null).getMeshBuilder(),
         Pipelines.INSTANCE.getRenderPipeline3(),
         MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
         UniformBlockF.INSTANCE
            .getDynamicUniformStorage()
            .writeUniform(
               new HandChams.UniformData4(
                  Math.abs(
                     Render2D.INSTANCE
                        .computeAmount(
                           (MinecraftHolder.getLocalPlayer().tickCount - 1) / 6.0F,
                           MinecraftHolder.getLocalPlayer().tickCount / 6.0F,
                           Render2D.INSTANCE.getAmount2()
                        )
                  ),
                  color,
                  color2,
                  value,
                  value2,
                  amount,
                  amount2,
                  1,
                  amount3,
                  new Vec2(1.0F / mainTarget.width, 1.0F / mainTarget.height),
                  amount4,
                  amount5
               )
            ),
         null,
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", mainTarget.getColorTextureView())),
         null,
         88,
         null
      );
   }

   private final void applyGetColorTextureView(Color color, float amount, MainTarget mainTarget) {
      MeshBuilder.defaultImpl(
         new MeshBuilder(null, 0, false, 7, null).getMeshBuilder(),
         Pipelines.INSTANCE.getRenderPipeline(),
         MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
         UniformBlockE.INSTANCE
            .getDynamicUniformStorage()
            .writeUniform(new HandChams.UniformData3(1.0F / mainTarget.width, 1.0F / mainTarget.height, color, amount)),
         null,
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", mainTarget.getColorTextureView())),
         null,
         88,
         null
      );
   }

   private final void applyAbs(Color color, Color color2, Color color3, Color color4, float amount, MainTarget mainTarget) {
      MeshBuilder.defaultImpl(
         new MeshBuilder(null, 0, false, 7, null).getMeshBuilder(),
         Pipelines.INSTANCE.getRenderPipeline2(),
         MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
         UniformBlockK.INSTANCE
            .getDynamicUniformStorage()
            .writeUniform(
               new HandChams.UniformData2(
                  1.0F / mainTarget.width,
                  1.0F / mainTarget.height,
                  color,
                  color2,
                  color3,
                  color4,
                  amount,
                  Math.abs(
                     Render2D.INSTANCE
                        .computeAmount(
                           (MinecraftHolder.getLocalPlayer().tickCount - 1) / 1000.0F,
                           MinecraftHolder.getLocalPlayer().tickCount / 1000.0F,
                           Render2D.INSTANCE.getAmount2()
                        )
                  )
               )
            ),
         null,
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", mainTarget.getColorTextureView())),
         null,
         88,
         null
      );
   }

   private final void applyGetColorTextureView(MainTarget mainTarget, Color color, boolean flag, boolean flag2, float amount) {
      MeshBuilder meshBuilder = new MeshBuilder(null, 0, false, 7, null).getMeshBuilder();
      RenderPipeline renderPipeline4 = Pipelines.INSTANCE.getRenderPipeline4();
      RenderTarget mainRenderTarget = MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget();
      DynamicUniformStorage dynamicUniformStorage = UniformBlockG.INSTANCE.getDynamicUniformStorage();
      Vec2 vec2 = new Vec2(0.0F, flag2 ? 0.0F : 0.5F);
      Vec2 vec22 = new Vec2(1.0F / mainTarget.width, 1.0F / mainTarget.height);
      float value2 = flag ? 1.0F : 0.0F;
      Vec2 vec23 = vec22;
      Vec2 vec24 = vec2;
      GpuBufferSlice writeUniform = dynamicUniformStorage.writeUniform(new HandChams.UniformData5(vec24, vec23, color, value2, amount, amount));
      Pair[] pairs = new Pair[]{TuplesKt.to("Sampler0", mainTarget.getColorTextureView()), TuplesKt.to("Sampler1", BlurService.INSTANCE.getGpuTextureView())};
      MeshBuilder.defaultImpl(meshBuilder, renderPipeline4, mainRenderTarget, writeUniform, null, null, MapsKt.mapOf(pairs), null, 88, null);
   }

   private final void applyGetCameraEntity(float amount, MainTarget mainTarget) {
      GameRendererAccessor gameRenderer = (GameRendererAccessor)MinecraftHolder.getMinecraft().gameRenderer;
      GameRenderState catleangetGameRenderState = gameRenderer.catlean$getGameRenderState();
      CameraRenderState cameraRenderState = catleangetGameRenderState.levelRenderState.cameraRenderState;
      WindowRenderState windowRenderState = catleangetGameRenderState.windowRenderState;
      Projection catleangetHudProjection = gameRenderer.catlean$getHudProjection();
      catleangetHudProjection.setupPerspective(0.05F, 100.0F, cameraRenderState.hudFov, windowRenderState.width, windowRenderState.height);
      RenderSystem.setProjectionMatrix(gameRenderer.catlean$getHud3dProjectionMatrixBuffer().getBuffer(catleangetHudProjection), ProjectionType.PERSPECTIVE);
      Matrix4f viewRotationMatrix = cameraRenderState.viewRotationMatrix;
      PoseStack poseStack = new PoseStack();
      poseStack.pushPose();
      poseStack.mulPose((Matrix4fc)viewRotationMatrix.invert(new Matrix4f()));
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix().mul((Matrix4fc)viewRotationMatrix);
      gameRenderer.itiltViewWhenHurt(cameraRenderState, poseStack);
      if ((Boolean)MinecraftHolder.getMinecraft().options.bobView().get()) {
         gameRenderer.ibobView(cameraRenderState, poseStack);
      }

      boolean flag3 = MinecraftHolder.getMinecraft().getCameraEntity() instanceof LivingEntity
         && ((LivingEntity)MinecraftHolder.getMinecraft().getCameraEntity()).isSleeping();
      SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
      if (MinecraftHolder.getMinecraft().options.getCameraType().isFirstPerson()
         && !flag3
         && MinecraftHolder.getMinecraft().gameMode.getPlayerMode() != GameType.SPECTATOR) {
         MinecraftHolder.getMinecraft()
            .gameRenderer
            .itemInHandRenderer
            .submitHandsWithItems(
               amount,
               poseStack,
               (SubmitNodeCollector)submitNodeStorage,
               MinecraftHolder.getLocalPlayer(),
               MinecraftHolder.getMinecraft().getEntityRenderDispatcher().getPackedLightCoords((Entity)MinecraftHolder.getLocalPlayer(), amount)
            );
      }

      GpuTextureView outputColorTextureOverride = RenderSystem.outputColorTextureOverride;
      GpuTextureView outputDepthTextureOverride = RenderSystem.outputDepthTextureOverride;

      try {
         if (mainTarget != null) {
            RenderSystem.outputColorTextureOverride = mainTarget.getColorTextureView();
            RenderSystem.outputDepthTextureOverride = mainTarget.getDepthTextureView();
         }

         MinecraftHolder.getMinecraft().gameRenderer.featureRenderDispatcher().renderAllFeatures(submitNodeStorage);
      } finally {
         RenderSystem.outputColorTextureOverride = outputColorTextureOverride;
         RenderSystem.outputDepthTextureOverride = outputDepthTextureOverride;
      }

      modelViewStack.popMatrix();
      poseStack.popPose();
   }

   private final float computeAmount(int value) {
      return (float)(0.06649038007 * Math.exp(-(value * value) / 72.0));
   }

   static {
   }

   public static final class UniformData implements DynamicUniform {
      @NotNull
      private final Vec2 texelSize;
      @NotNull
      private final Vec2 direction;
      private final float amount;
      private final float amount2;
      @NotNull
      private final Matrix4f matrix;
      @NotNull
      private final Color color;

      public UniformData(Vec2 texelSize, Vec2 direction, float amount, float amount2, Matrix4f matrix, Color color) {
         this.texelSize = texelSize;
         this.direction = direction;
         this.amount = amount;
         this.amount2 = amount2;
         this.matrix = matrix;
         this.color = color;
      }

      @NotNull
      public final Vec2 getTexelSize() {
         return this.texelSize;
      }

      @NotNull
      public final Vec2 getDirection() {
         return this.direction;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      @NotNull
      public final Matrix4f getMatrix() {
         return this.matrix;
      }

      @NotNull
      public final Color getColor() {
         return this.color;
      }

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer)
            .putVec2(this.texelSize.x, this.texelSize.y)
            .putVec2(this.direction.x, this.direction.y)
            .putFloat(this.amount)
            .putFloat(this.amount2)
            .putMat4f((Matrix4fc)this.matrix)
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.color),
               Render2D.INSTANCE.fetchGreen(this.color),
               Render2D.INSTANCE.fetchBlue(this.color),
               Render2D.INSTANCE.fetchAlpha(this.color)
            );
      }

      @NotNull
      public final Vec2 getTexelSize2() {
         return this.texelSize;
      }

      @NotNull
      public final Vec2 getDirection2() {
         return this.direction;
      }

      public final float getAmount3() {
         return this.amount;
      }

      public final float getAmount4() {
         return this.amount2;
      }

      @NotNull
      public final Matrix4f getMatrix2() {
         return this.matrix;
      }

      @NotNull
      public final Color getColor2() {
         return this.color;
      }

      @NotNull
      @Override
      public String toString() {
         Color color2 = this.color;
         Matrix4f matrix2 = this.matrix;
         float amount22 = this.amount2;
         float amount3 = this.amount;
         Vec2 direction2 = this.direction;
         Vec2 texelSize2 = this.texelSize;
         return "UniformData(texelSize="
            + texelSize2
            + ", direction="
            + direction2
            + ", radius="
            + amount3
            + ", glowAlpha="
            + amount22
            + ", matrix="
            + matrix2
            + ", color="
            + color2
            + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.texelSize.hashCode();
         hashCode = hashCode * 31 + this.direction.hashCode();
         hashCode = hashCode * 31 + Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + this.matrix.hashCode();
         return hashCode * 31 + this.color.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof HandChams.UniformData uniformData)) {
            return false;
         } else if (!Intrinsics.areEqual(this.texelSize, uniformData.texelSize)) {
            return false;
         } else if (!Intrinsics.areEqual(this.direction, uniformData.direction)) {
            return false;
         } else if (Float.compare(this.amount, uniformData.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, uniformData.amount2) != 0) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.matrix, uniformData.matrix) ? false : Intrinsics.areEqual(this.color, uniformData.color);
         }
      }
   }

   public static final class UniformData2 implements DynamicUniform {
      private final float amount;
      private final float amount2;
      @NotNull
      private final Color color1;
      @NotNull
      private final Color color2;
      @NotNull
      private final Color color3;
      @NotNull
      private final Color color4;
      private final float amount3;
      private final float amount4;

      public UniformData2(float amount, float amount2, Color color1, Color color2, Color color3, Color color4, float amount3, float amount4) {
         this.amount = amount;
         this.amount2 = amount2;
         this.color1 = color1;
         this.color2 = color2;
         this.color3 = color3;
         this.color4 = color4;
         this.amount3 = amount3;
         this.amount4 = amount4;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      @NotNull
      public final Color getColor1() {
         return this.color1;
      }

      @NotNull
      public final Color getColor2() {
         return this.color2;
      }

      @NotNull
      public final Color getColor3() {
         return this.color3;
      }

      @NotNull
      public final Color getColor4() {
         return this.color4;
      }

      public final float getAmount3() {
         return this.amount3;
      }

      public final float getAmount4() {
         return this.amount4;
      }

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer)
            .putVec2(this.amount, this.amount2)
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.color1),
               Render2D.INSTANCE.fetchGreen(this.color1),
               Render2D.INSTANCE.fetchBlue(this.color1),
               Render2D.INSTANCE.fetchAlpha(this.color1)
            )
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.color2),
               Render2D.INSTANCE.fetchGreen(this.color2),
               Render2D.INSTANCE.fetchBlue(this.color2),
               Render2D.INSTANCE.fetchAlpha(this.color2)
            )
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.color3),
               Render2D.INSTANCE.fetchGreen(this.color3),
               Render2D.INSTANCE.fetchBlue(this.color3),
               Render2D.INSTANCE.fetchAlpha(this.color3)
            )
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.color4),
               Render2D.INSTANCE.fetchGreen(this.color4),
               Render2D.INSTANCE.fetchBlue(this.color4),
               Render2D.INSTANCE.fetchAlpha(this.color4)
            )
            .putFloat(this.amount3)
            .putFloat(this.amount4);
      }

      public final float getAmount5() {
         return this.amount;
      }

      public final float getAmount6() {
         return this.amount2;
      }

      @NotNull
      public final Color getColor5() {
         return this.color1;
      }

      @NotNull
      public final Color getColor6() {
         return this.color2;
      }

      @NotNull
      public final Color getColor7() {
         return this.color3;
      }

      @NotNull
      public final Color getColor8() {
         return this.color4;
      }

      public final float getAmount7() {
         return this.amount3;
      }

      public final float getAmount8() {
         return this.amount4;
      }

      @NotNull
      @Override
      public String toString() {
         float amount42 = this.amount4;
         float amount32 = this.amount3;
         Color color42 = this.color4;
         Color color32 = this.color3;
         Color color22 = this.color2;
         Color color12 = this.color1;
         float amount22 = this.amount2;
         float amount5 = this.amount;
         return "UniformData(halfTexelSizeX="
            + amount5
            + ", halfTexelSizeY="
            + amount22
            + ", color1="
            + color12
            + ", color2="
            + color22
            + ", color3="
            + color32
            + ", color4="
            + color42
            + ", lineWidth="
            + amount32
            + ", time="
            + amount42
            + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + this.color1.hashCode();
         hashCode = hashCode * 31 + this.color2.hashCode();
         hashCode = hashCode * 31 + this.color3.hashCode();
         hashCode = hashCode * 31 + this.color4.hashCode();
         hashCode = hashCode * 31 + Float.hashCode(this.amount3);
         return hashCode * 31 + Float.hashCode(this.amount4);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof HandChams.UniformData2 uniformData2)) {
            return false;
         } else if (Float.compare(this.amount, uniformData2.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, uniformData2.amount2) != 0) {
            return false;
         } else if (!Intrinsics.areEqual(this.color1, uniformData2.color1)) {
            return false;
         } else if (!Intrinsics.areEqual(this.color2, uniformData2.color2)) {
            return false;
         } else if (!Intrinsics.areEqual(this.color3, uniformData2.color3)) {
            return false;
         } else if (!Intrinsics.areEqual(this.color4, uniformData2.color4)) {
            return false;
         } else {
            return Float.compare(this.amount3, uniformData2.amount3) != 0 ? false : Float.compare(this.amount4, uniformData2.amount4) == 0;
         }
      }
   }

   public static final class UniformData3 implements DynamicUniform {
      private final float amount;
      private final float amount2;
      @NotNull
      private final Color color;
      private final float amount3;

      public UniformData3(float amount, float amount2, Color color, float amount3) {
         this.amount = amount;
         this.amount2 = amount2;
         this.color = color;
         this.amount3 = amount3;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      @NotNull
      public final Color getColor() {
         return this.color;
      }

      public final float getAmount3() {
         return this.amount3;
      }

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer)
            .putVec2(this.amount, this.amount2)
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.color),
               Render2D.INSTANCE.fetchGreen(this.color),
               Render2D.INSTANCE.fetchBlue(this.color),
               Render2D.INSTANCE.fetchAlpha(this.color)
            )
            .putFloat(this.amount3);
      }

      public final float getAmount4() {
         return this.amount;
      }

      public final float getAmount5() {
         return this.amount2;
      }

      @NotNull
      public final Color getColor2() {
         return this.color;
      }

      public final float getAmount6() {
         return this.amount3;
      }

      @NotNull
      @Override
      public String toString() {
         float amount32 = this.amount3;
         Color color2 = this.color;
         float amount22 = this.amount2;
         float amount4 = this.amount;
         return "UniformData(halfTexelSizeX=" + amount4 + ", halfTexelSizeY=" + amount22 + ", color=" + color2 + ", lineWidth=" + amount32 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + this.color.hashCode();
         return hashCode * 31 + Float.hashCode(this.amount3);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof HandChams.UniformData3 uniformData3)) {
            return false;
         } else if (Float.compare(this.amount, uniformData3.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, uniformData3.amount2) != 0) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.color, uniformData3.color) ? false : Float.compare(this.amount3, uniformData3.amount3) == 0;
         }
      }
   }

   public static final class UniformData4 implements DynamicUniform {
      private final float amount;
      @NotNull
      private final Color color;
      @NotNull
      private final Color color2;
      private final int value;
      private final int value2;
      private final float amount2;
      private final float amount3;
      private final int value3;
      private final float amount4;
      @NotNull
      private final Vec2 uHalfTexelSize;
      private final float amount5;
      private final float amount6;

      public UniformData4(
         float amount,
         Color color,
         Color color2,
         int value,
         int value2,
         float amount2,
         float amount3,
         int value3,
         float amount4,
         Vec2 uHalfTexelSize,
         float amount5,
         float amount6
      ) {
         this.amount = amount;
         this.color = color;
         this.color2 = color2;
         this.value = value;
         this.value2 = value2;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.value3 = value3;
         this.amount4 = amount4;
         this.uHalfTexelSize = uHalfTexelSize;
         this.amount5 = amount5;
         this.amount6 = amount6;
      }

      public final float getAmount() {
         return this.amount;
      }

      @NotNull
      public final Color getColor() {
         return this.color;
      }

      @NotNull
      public final Color getColor2() {
         return this.color2;
      }

      public final int getValue() {
         return this.value;
      }

      public final int getValue2() {
         return this.value2;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      public final float getAmount3() {
         return this.amount3;
      }

      public final int getValue3() {
         return this.value3;
      }

      public final float getAmount4() {
         return this.amount4;
      }

      @NotNull
      public final Vec2 getUHalfTexelSize() {
         return this.uHalfTexelSize;
      }

      public final float getAmount5() {
         return this.amount5;
      }

      public final float getAmount6() {
         return this.amount6;
      }

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer)
            .putFloat(this.amount)
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.color),
               Render2D.INSTANCE.fetchGreen(this.color),
               Render2D.INSTANCE.fetchBlue(this.color),
               Render2D.INSTANCE.fetchAlpha(this.color)
            )
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.color2),
               Render2D.INSTANCE.fetchGreen(this.color2),
               Render2D.INSTANCE.fetchBlue(this.color2),
               Render2D.INSTANCE.fetchAlpha(this.color2)
            )
            .putInt(this.value)
            .putInt(this.value2)
            .putFloat(this.amount2)
            .putFloat(this.amount3)
            .putInt(this.value3)
            .putFloat(this.amount4)
            .putVec2(this.uHalfTexelSize.x, this.uHalfTexelSize.y)
            .putFloat(this.amount5)
            .putFloat(this.amount6);
      }

      public final float getAmount7() {
         return this.amount;
      }

      @NotNull
      public final Color getColor3() {
         return this.color;
      }

      @NotNull
      public final Color getColor4() {
         return this.color2;
      }

      public final int getValue4() {
         return this.value;
      }

      public final int getValue5() {
         return this.value2;
      }

      public final float getAmount8() {
         return this.amount2;
      }

      public final float getAmount9() {
         return this.amount3;
      }

      public final int getValue6() {
         return this.value3;
      }

      public final float getAmount10() {
         return this.amount4;
      }

      @NotNull
      public final Vec2 getUHalfTexelSize2() {
         return this.uHalfTexelSize;
      }

      public final float getAmount11() {
         return this.amount5;
      }

      public final float getAmount12() {
         return this.amount6;
      }

      @NotNull
      @Override
      public String toString() {
         float amount62 = this.amount6;
         float amount52 = this.amount5;
         Vec2 uHalfTexelSize2 = this.uHalfTexelSize;
         float amount42 = this.amount4;
         int value32 = this.value3;
         float amount32 = this.amount3;
         float amount22 = this.amount2;
         int value22 = this.value2;
         int value4 = this.value;
         Color color22 = this.color2;
         Color color3 = this.color;
         float amount7 = this.amount;
         return "UniformData(time="
            + amount7
            + ", color="
            + color3
            + ", color2="
            + color22
            + ", colorMode="
            + value4
            + ", glowQuality="
            + value22
            + ", glowRadius="
            + amount22
            + ", glowAlphaMult="
            + amount32
            + ", glowThinOutline="
            + value32
            + ", fillAlphaMult="
            + amount42
            + ", uHalfTexelSize="
            + uHalfTexelSize2
            + ", uSaturation="
            + amount52
            + ", uBrightness="
            + amount62
            + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + this.color.hashCode();
         hashCode = hashCode * 31 + this.color2.hashCode();
         hashCode = hashCode * 31 + Integer.hashCode(this.value);
         hashCode = hashCode * 31 + Integer.hashCode(this.value2);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + Float.hashCode(this.amount3);
         hashCode = hashCode * 31 + Integer.hashCode(this.value3);
         hashCode = hashCode * 31 + Float.hashCode(this.amount4);
         hashCode = hashCode * 31 + this.uHalfTexelSize.hashCode();
         hashCode = hashCode * 31 + Float.hashCode(this.amount5);
         return hashCode * 31 + Float.hashCode(this.amount6);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof HandChams.UniformData4 uniformData4)) {
            return false;
         } else if (Float.compare(this.amount, uniformData4.amount) != 0) {
            return false;
         } else if (!Intrinsics.areEqual(this.color, uniformData4.color)) {
            return false;
         } else if (!Intrinsics.areEqual(this.color2, uniformData4.color2)) {
            return false;
         } else if (this.value != uniformData4.value) {
            return false;
         } else if (this.value2 != uniformData4.value2) {
            return false;
         } else if (Float.compare(this.amount2, uniformData4.amount2) != 0) {
            return false;
         } else if (Float.compare(this.amount3, uniformData4.amount3) != 0) {
            return false;
         } else if (this.value3 != uniformData4.value3) {
            return false;
         } else if (Float.compare(this.amount4, uniformData4.amount4) != 0) {
            return false;
         } else if (!Intrinsics.areEqual(this.uHalfTexelSize, uniformData4.uHalfTexelSize)) {
            return false;
         } else {
            return Float.compare(this.amount5, uniformData4.amount5) != 0 ? false : Float.compare(this.amount6, uniformData4.amount6) == 0;
         }
      }
   }

   public static final class UniformData5 implements DynamicUniform {
      @NotNull
      private final Vec2 uViewOffset;
      @NotNull
      private final Vec2 uHalfTexelSize;
      @NotNull
      private final Color uColor;
      private final float amount;
      private final float amount2;
      private final float amount3;

      public UniformData5(Vec2 uViewOffset, Vec2 uHalfTexelSize, Color uColor, float amount, float amount2, float amount3) {
         this.uViewOffset = uViewOffset;
         this.uHalfTexelSize = uHalfTexelSize;
         this.uColor = uColor;
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
      }

      @NotNull
      public final Vec2 getUViewOffset() {
         return this.uViewOffset;
      }

      @NotNull
      public final Vec2 getUHalfTexelSize() {
         return this.uHalfTexelSize;
      }

      @NotNull
      public final Color getUColor() {
         return this.uColor;
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

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer)
            .putVec2(this.uViewOffset.x, this.uViewOffset.y)
            .putVec4(
               Render2D.INSTANCE.fetchRed(this.uColor),
               Render2D.INSTANCE.fetchGreen(this.uColor),
               Render2D.INSTANCE.fetchBlue(this.uColor),
               Render2D.INSTANCE.fetchAlpha(this.uColor)
            )
            .putFloat(this.amount)
            .putVec2(this.uHalfTexelSize.x, this.uHalfTexelSize.y)
            .putFloat(this.amount2)
            .putFloat(this.amount3);
      }

      @NotNull
      public final Vec2 getUViewOffset2() {
         return this.uViewOffset;
      }

      @NotNull
      public final Vec2 getUHalfTexelSize2() {
         return this.uHalfTexelSize;
      }

      @NotNull
      public final Color getUColor2() {
         return this.uColor;
      }

      public final float getAmount4() {
         return this.amount;
      }

      public final float getAmount5() {
         return this.amount2;
      }

      public final float getAmount6() {
         return this.amount3;
      }

      @NotNull
      @Override
      public String toString() {
         float amount32 = this.amount3;
         float amount22 = this.amount2;
         float amount4 = this.amount;
         Color uColor2 = this.uColor;
         Vec2 uHalfTexelSize2 = this.uHalfTexelSize;
         Vec2 uViewOffset2 = this.uViewOffset;
         return "UniformData(uViewOffset="
            + uViewOffset2
            + ", uHalfTexelSize="
            + uHalfTexelSize2
            + ", uColor="
            + uColor2
            + ", uBorder="
            + amount4
            + ", alphaX="
            + amount22
            + ", alphaY="
            + amount32
            + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.uViewOffset.hashCode();
         hashCode = hashCode * 31 + this.uHalfTexelSize.hashCode();
         hashCode = hashCode * 31 + this.uColor.hashCode();
         hashCode = hashCode * 31 + Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         return hashCode * 31 + Float.hashCode(this.amount3);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof HandChams.UniformData5 uniformData5)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uViewOffset, uniformData5.uViewOffset)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uHalfTexelSize, uniformData5.uHalfTexelSize)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uColor, uniformData5.uColor)) {
            return false;
         } else if (Float.compare(this.amount, uniformData5.amount) != 0) {
            return false;
         } else {
            return Float.compare(this.amount2, uniformData5.amount2) != 0 ? false : Float.compare(this.amount3, uniformData5.amount3) == 0;
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[PlayerEsp.ShaderMode.values().length];

         try {
            values2[PlayerEsp.ShaderMode.MIRROR.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[PlayerEsp.ShaderMode.DEFAULT.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[PlayerEsp.ShaderMode.BLOOM.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[PlayerEsp.ShaderMode.DOUBLE.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[PlayerEsp.ShaderMode.CAMOUFLAGE.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         values = values2;
      }
   }
}