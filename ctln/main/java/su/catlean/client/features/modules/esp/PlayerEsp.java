// deobf by pusia
package su.catlean.client.features.modules.esp;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.awt.Color;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.EntityAlphaEvent;
import su.catlean.api.event.events.render.FrameBufferEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.render.RenderArmorEvent;
import su.catlean.api.event.events.render.RenderNameTagEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.ModelEspRenderer;
import su.catlean.client.render.PlayerEspRenderer;
import su.catlean.client.render.PlayerFillEspRenderer;
import su.catlean.client.render.ShaderEspRenderer;
import su.catlean.client.render.TracerRenderer;
import su.catlean.gofra.Flow;

public final class PlayerEsp extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(PlayerEsp.class, "types", "getTypes()Lsu/catlean/nl;", 0), new PropertyReference1Impl(PlayerEsp.class, "renderMode", "getRenderMode()Lsu/catlean/c2;", 0), new PropertyReference1Impl(PlayerEsp.class, "shaderMode", "getShaderMode()Lsu/catlean/kl;", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "colorModeValue", "getColorModeValue()Lsu/catlean/d5;", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "default", "getDefault()Z", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "box", "getBox()Z", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "frame", "getFrame()Z", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "shaderVal", "getShaderVal()Z", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "skeleton", "getSkeleton()Z", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "extraPart", "getExtraPart()Z", 0), new PropertyReference1Impl(PlayerEsp.class, "shaderColor", "getShaderColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "skeletonColor", "getSkeletonColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "skeletonFriendColor", "getSkeletonFriendColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "fillColor", "getFillColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "lineColor", "getLineColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "friendFillColor", "getFriendFillColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "friendLineColor", "getFriendLineColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "sFillColor", "getSFillColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "sLineColor", "getSLineColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "sFriendFillColor", "getSFriendFillColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlayerEsp.class, "sFriendLineColor", "getSFriendLineColor()Ljava/awt/Color;", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "glass", "getGlass()Z", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "borders", "getBorders()Z", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "radiusValue", "getRadiusValue()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "distanceValue", "getDistanceValue()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "glowAlphaValue", "getGlowAlphaValue()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "lineWidthValue", "getLineWidthValue()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "alpha", "getAlpha()F", 0), new PropertyReference1Impl(PlayerEsp.class, "secondColor", "getSecondColor()Ljava/awt/Color;", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "glowQualityValue", "getGlowQualityValue()I", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "glowRadiusValue", "getGlowRadiusValue()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "glowAlphaMultValue", "getGlowAlphaMultValue()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "fillAlphaMultValue", "getFillAlphaMultValue()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "saturation", "getSaturation()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "brightness", "getBrightness()F", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "hDistance", "getHDistance()I", 0), new MutablePropertyReference1Impl(PlayerEsp.class, "vDistance", "getVDistance()I", 0) };
   @NotNull
   public static final PlayerEsp INSTANCE = new PlayerEsp();
   @NotNull
   private static final GroupSetting types = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "types", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final EnumSetting renderMode = SettingFactory.defaultImpl(
      INSTANCE, "render-mode", PlayerEsp.RenderMode.BOTH, null, () -> INSTANCE.getBox() || INSTANCE.getDefault(), 4, null
   );
   @NotNull
   private static final EnumSetting shaderMode = SettingFactory.defaultImpl(
      INSTANCE, "shader-mode", PlayerEsp.ShaderMode.DEFAULT, null, () -> INSTANCE.getShaderVal(), 4, null
   );
   @NotNull
   private static final EnumSetting colorMode = SettingFactory.defaultImpl(
      INSTANCE, "color-mode", PlayerEsp.ColorMode.RGB, null, () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(), 4, null
   );
   @NotNull
   private static final BooleanSetting defaultValue = SettingFactory.defaultImpl(INSTANCE, "default", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final BooleanSetting box = SettingFactory.defaultImpl(INSTANCE, "box", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final BooleanSetting frame = SettingFactory.defaultImpl(INSTANCE, "frame", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final BooleanSetting shader = SettingFactory.defaultImpl(INSTANCE, "shader", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final BooleanSetting skeleton = SettingFactory.defaultImpl(INSTANCE, "skeleton", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final BooleanSetting extraPart = SettingFactory.defaultImpl(INSTANCE, "extra-part", false, null, () -> INSTANCE.getSkeleton(), 4, null);
   @NotNull
   private static final ColorSetting shaderColor = SettingFactory.defaultImpl(
      INSTANCE, "shader-color", new Color(255, 255, 255, 255), null, () -> INSTANCE.getShaderVal(), 4, null
   );
   @NotNull
   private static final ColorSetting skeletonColor = SettingFactory.defaultImpl(
      INSTANCE, "skeleton-color", new Color(255, 255, 255, 255), null, () -> INSTANCE.getSkeleton(), 4, null
   );
   @NotNull
   private static final ColorSetting skeletonFriendColor = SettingFactory.defaultImpl(
      INSTANCE, "skeleton-friend-color", new Color(255, 255, 255, 255), null, () -> INSTANCE.getSkeleton(), 4, null
   );
   @NotNull
   private static final ColorSetting fill = SettingFactory.defaultImpl(INSTANCE, "fill", new Color(255, 255, 255, 120), null, null, 12, null);
   @NotNull
   private static final ColorSetting line = SettingFactory.defaultImpl(INSTANCE, "line", new Color(255, 255, 255, 255), null, null, 12, null);
   @NotNull
   private static final ColorSetting friendFill = SettingFactory.defaultImpl(INSTANCE, "friend-fill", new Color(0, 255, 0, 120), null, null, 12, null);
   @NotNull
   private static final ColorSetting friendLine = SettingFactory.defaultImpl(INSTANCE, "friend-line", new Color(0, 255, 0, 255), null, null, 12, null);
   @NotNull
   private static final ColorSetting sFill = SettingFactory.defaultImpl(
      INSTANCE, "s-fill", new Color(255, 255, 255, 120), null, () -> INSTANCE.getBox(), 4, null
   );
   @NotNull
   private static final ColorSetting sLine = SettingFactory.defaultImpl(
      INSTANCE, "s-line", new Color(255, 255, 255, 255), null, () -> INSTANCE.getBox(), 4, null
   );
   @NotNull
   private static final ColorSetting sFriendFill = SettingFactory.defaultImpl(
      INSTANCE, "s-friend-fill", new Color(0, 255, 0, 120), null, () -> INSTANCE.getBox(), 4, null
   );
   @NotNull
   private static final ColorSetting sFriendLine = SettingFactory.defaultImpl(
      INSTANCE, "s-friend-line", new Color(0, 255, 0, 255), null, () -> INSTANCE.getBox(), 4, null
   );
   @NotNull
   private static final BooleanSetting glass = SettingFactory.defaultImpl(
      INSTANCE, "glass", false, null, () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.MIRROR && INSTANCE.getShaderVal(), 4, null
   );
   @NotNull
   private static final BooleanSetting border = SettingFactory.defaultImpl(
      INSTANCE, "border", false, null, () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.MIRROR && INSTANCE.getShaderVal(), 4, null
   );
   @NotNull
   private static final FloatSetting radius = SettingFactory.defaultImpl(
      INSTANCE,
      "radius",
      6.0F,
      RangesKt.rangeTo(0.1F, 15.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.BLOOM && INSTANCE.getShaderVal(),
      8,
      null
   );
   @NotNull
   private static final FloatSetting distance = SettingFactory.defaultImpl(
      INSTANCE,
      "distance",
      1.3F,
      RangesKt.rangeTo(0.1F, 15.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.BLOOM && INSTANCE.getShaderVal(),
      8,
      null
   );
   @NotNull
   private static final FloatSetting glowAlpha = SettingFactory.defaultImpl(
      INSTANCE,
      "glow-alpha",
      1.7F,
      RangesKt.rangeTo(1.0F, 5.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.BLOOM && INSTANCE.getShaderVal(),
      8,
      null
   );
   @NotNull
   private static final FloatSetting lineWidth = SettingFactory.defaultImpl(
      INSTANCE,
      "line-width",
      4.0F,
      RangesKt.rangeTo(1.0F, 4.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DEFAULT || INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.BLOOM,
      8,
      null
   );
   @NotNull
   private static final FloatSetting alpha = SettingFactory.defaultImpl(
      INSTANCE, "alpha", 0.2F, RangesKt.rangeTo(0.0F, 2.0F), null, () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.MIRROR, 8, null
   );
   @NotNull
   private static final ColorSetting secondColor = SettingFactory.defaultImpl(
      INSTANCE,
      "second-color",
      new Color(255, 255, 255, 255),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(),
      4,
      null
   );
   @NotNull
   private static final IntSetting glowQuality = SettingFactory.defaultImpl(
      INSTANCE, "glow-quality", 2, new IntRange(1, 4), null, () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(), 8, null
   );
   @NotNull
   private static final FloatSetting glowRadius = SettingFactory.defaultImpl(
      INSTANCE,
      "glow-radius",
      4.0F,
      RangesKt.rangeTo(1.0F, 16.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(),
      8,
      null
   );
   @NotNull
   private static final FloatSetting glowAlphaMultiplier = SettingFactory.defaultImpl(
      INSTANCE,
      "glow-alpha-multiplier",
      2.0F,
      RangesKt.rangeTo(0.0F, 4.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(),
      8,
      null
   );
   @NotNull
   private static final FloatSetting fillAlphaMultiplier = SettingFactory.defaultImpl(
      INSTANCE,
      "fill-alpha-multiplier",
      0.6F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(),
      8,
      null
   );
   @NotNull
   private static final FloatSetting saturation = SettingFactory.defaultImpl(
      INSTANCE,
      "saturation",
      0.7F,
      RangesKt.rangeTo(0.0F, 5.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(),
      8,
      null
   );
   @NotNull
   private static final FloatSetting brightness = SettingFactory.defaultImpl(
      INSTANCE,
      "brightness",
      2.0F,
      RangesKt.rangeTo(0.0F, 45.0F),
      null,
      () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(),
      8,
      null
   );
   @NotNull
   private static final IntSetting maxHorizontalDistance = SettingFactory.defaultImpl(
      INSTANCE, "max-horizontal-distance", 64, new IntRange(1, 256), null, null, 24, null
   );
   @NotNull
   private static final IntSetting maxVerticalDistance = SettingFactory.defaultImpl(
      INSTANCE, "max-vertical-distance", 32, new IntRange(1, 256), null, null, 24, null
   );
   @NotNull
   private static final ShaderEspRenderer shaderEspRenderer = new ShaderEspRenderer();

   private PlayerEsp() {
      super("player-esp", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final GroupValue getTypes() {
      return (GroupValue)types.computeObject(this, $$delegatedProperties[0]);
   }

   private final PlayerEsp.RenderMode getRenderMode() {
      return (PlayerEsp.RenderMode)renderMode.computeObject(this, $$delegatedProperties[1]);
   }

   private final PlayerEsp.ShaderMode getShaderMode() {
      return (PlayerEsp.ShaderMode)shaderMode.computeObject(this, $$delegatedProperties[2]);
   }

   private final PlayerEsp.ColorMode getColorModeValue() {
      return (PlayerEsp.ColorMode)colorMode.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getDefault() {
      return (Boolean)defaultValue.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getBox() {
      return (Boolean)box.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getFrame() {
      return (Boolean)frame.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getShaderVal() {
      return (Boolean)shader.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getSkeleton() {
      return (Boolean)skeleton.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getExtraPart() {
      return (Boolean)extraPart.computeObject(this, $$delegatedProperties[9]);
   }

   private final Color getShaderColor() {
      return (Color)shaderColor.computeObject(this, $$delegatedProperties[10]);
   }

   private final Color getSkeletonColor() {
      return (Color)skeletonColor.computeObject(this, $$delegatedProperties[11]);
   }

   private final Color getSkeletonFriendColor() {
      return (Color)skeletonFriendColor.computeObject(this, $$delegatedProperties[12]);
   }

   private final Color getFillColor() {
      return (Color)fill.computeObject(this, $$delegatedProperties[13]);
   }

   private final Color getLineColor() {
      return (Color)line.computeObject(this, $$delegatedProperties[14]);
   }

   private final Color getFriendFillColor() {
      return (Color)friendFill.computeObject(this, $$delegatedProperties[15]);
   }

   private final Color getFriendLineColor() {
      return (Color)friendLine.computeObject(this, $$delegatedProperties[16]);
   }

   private final Color getSFillColor() {
      return (Color)sFill.computeObject(this, $$delegatedProperties[17]);
   }

   private final Color getSLineColor() {
      return (Color)sLine.computeObject(this, $$delegatedProperties[18]);
   }

   private final Color getSFriendFillColor() {
      return (Color)sFriendFill.computeObject(this, $$delegatedProperties[19]);
   }

   private final Color getSFriendLineColor() {
      return (Color)sFriendLine.computeObject(this, $$delegatedProperties[20]);
   }

   private final boolean getGlass() {
      return (Boolean)glass.computeObject(this, $$delegatedProperties[21]);
   }

   private final boolean getBorders() {
      return (Boolean)border.computeObject(this, $$delegatedProperties[22]);
   }

   private final float getRadiusValue() {
      return ((Number)radius.computeObject(this, $$delegatedProperties[23])).floatValue();
   }

   private final float getDistanceValue() {
      return ((Number)distance.computeObject(this, $$delegatedProperties[24])).floatValue();
   }

   private final float getGlowAlphaValue() {
      return ((Number)glowAlpha.computeObject(this, $$delegatedProperties[25])).floatValue();
   }

   private final float getLineWidthValue() {
      return ((Number)lineWidth.computeObject(this, $$delegatedProperties[26])).floatValue();
   }

   private final float getAlpha() {
      return ((Number)alpha.computeObject(this, $$delegatedProperties[27])).floatValue();
   }

   private final Color getSecondColor() {
      return (Color)secondColor.computeObject(this, $$delegatedProperties[28]);
   }

   private final int getGlowQualityValue() {
      return ((Number)glowQuality.computeObject(this, $$delegatedProperties[29])).intValue();
   }

   private final float getGlowRadiusValue() {
      return ((Number)glowRadius.computeObject(this, $$delegatedProperties[30])).floatValue();
   }

   private final float getGlowAlphaMultValue() {
      return ((Number)glowAlphaMultiplier.computeObject(this, $$delegatedProperties[31])).floatValue();
   }

   private final float getFillAlphaMultValue() {
      return ((Number)fillAlphaMultiplier.computeObject(this, $$delegatedProperties[32])).floatValue();
   }

   private final float getSaturation() {
      return ((Number)saturation.computeObject(this, $$delegatedProperties[33])).floatValue();
   }

   private final float getBrightness() {
      return ((Number)brightness.computeObject(this, $$delegatedProperties[34])).floatValue();
   }

   private final int getHDistance() {
      return ((Number)maxHorizontalDistance.computeObject(this, $$delegatedProperties[35])).intValue();
   }

   private final int getVDistance() {
      return ((Number)maxVerticalDistance.computeObject(this, $$delegatedProperties[36])).intValue();
   }

   @NotNull
   public final ShaderEspRenderer getShaderEspRenderer() {
      return shaderEspRenderer;
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.getShaderVal()) {
         shaderEspRenderer.render(render3DEvent.getStack(), false, this.getVDistance(), this.getHDistance());
      }

      if (this.getBox()) {
         PlayerFillEspRenderer.INSTANCE
            .applyGetXsize(
               this.getRenderMode(),
               this.getFillColor(),
               this.getLineColor(),
               this.getFriendFillColor(),
               this.getFriendLineColor(),
               this.getVDistance(),
               this.getHDistance(),
               this.getSFillColor(),
               this.getSLineColor(),
               this.getSFriendFillColor(),
               this.getSFriendLineColor()
            );
      }

      if (this.getDefault()) {
         PlayerEspRenderer.INSTANCE
            .render(
               render3DEvent.getStack(),
               this.getRenderMode(),
               this.getFillColor(),
               this.getLineColor(),
               this.getFriendFillColor(),
               this.getFriendLineColor(),
               this.getVDistance(),
               this.getHDistance()
            );
      }

      if (this.getSkeleton()) {
         ModelEspRenderer.INSTANCE.render(render3DEvent.getStack(), this.getSkeletonColor(), this.getSkeletonFriendColor(), this.getExtraPart());
      }
   }

   @Flow
   private final void onRenderNameTag(RenderNameTagEvent renderNameTagEvent) {
      if (renderNameTagEvent.getState() instanceof AvatarRenderState && this.getShaderVal() && shaderEspRenderer.isFlag()) {
         renderNameTagEvent.cancel();
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (this.getFrame()) {
         TracerRenderer.INSTANCE.render(render2DEvent.getContext(), this.getLineColor(), this.getFriendLineColor());
      }

      if (this.getShaderVal()) {
         shaderEspRenderer.updateShaderMode(
            this.getShaderMode(),
            this.getShaderColor(),
            this.getGlass(),
            this.getBorders(),
            this.getRadiusValue(),
            this.getDistanceValue(),
            this.getGlowAlphaValue(),
            this.getLineWidthValue(),
            this.getColorModeValue().ordinal(),
            this.getShaderColor(),
            this.getSecondColor(),
            this.getGlowQualityValue(),
            this.getGlowRadiusValue(),
            this.getGlowAlphaMultValue(),
            this.getFillAlphaMultValue(),
            this.getSaturation(),
            this.getBrightness(),
            this.getAlpha()
         );
      }
   }

   @Flow
   private final void onRenderArmor(RenderArmorEvent renderArmorEvent) {
      if (this.getShaderVal() && this.getShaderMode() == PlayerEsp.ShaderMode.MIRROR && !shaderEspRenderer.isFlag()) {
         renderArmorEvent.cancel();
      }
   }

   @Flow
   private final void onEntityAlpha(EntityAlphaEvent entityAlphaEvent) {
      if (this.getShaderVal() && this.getShaderMode() == PlayerEsp.ShaderMode.MIRROR && !shaderEspRenderer.isFlag()) {
         entityAlphaEvent.setAlpha(0.0F);
         entityAlphaEvent.cancel();
      }
   }

   @Flow
   private final void onFrameBuffer(FrameBufferEvent frameBufferEvent) {
      if (this.getShaderVal() && shaderEspRenderer.getMainTarget() != null && shaderEspRenderer.isFlag()) {
         frameBufferEvent.setFrameBuffer((RenderTarget)shaderEspRenderer.getMainTarget());
         frameBufferEvent.cancel();
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
   }

   static {
   }

   public static enum ColorMode {
      SINGLE,
      RGB,
      DOUBLE,
      DOUBLE_UP,
      TV;

      private static final PlayerEsp.ColorMode[] colorModes = $values();
      private static final EnumEntries<PlayerEsp.ColorMode> enumEntries = EnumEntriesKt.enumEntries(colorModes);

      private ColorMode() {
      }

      @NotNull
      public static EnumEntries<PlayerEsp.ColorMode> getEnumEntries() {
         return enumEntries;
      }

      private static final PlayerEsp.ColorMode[] $values() {
         return new PlayerEsp.ColorMode[]{SINGLE, RGB, DOUBLE, DOUBLE_UP, TV};
      }
   }

   public static enum RenderMode {
      FILL,
      OUTLINE,
      BOTH;

      private static final PlayerEsp.RenderMode[] renderModes = $values();
      private static final EnumEntries<PlayerEsp.RenderMode> enumEntries = EnumEntriesKt.enumEntries(renderModes);

      private RenderMode() {
      }

      @NotNull
      public static EnumEntries<PlayerEsp.RenderMode> getEnumEntries() {
         return enumEntries;
      }

      private static final PlayerEsp.RenderMode[] $values() {
         return new PlayerEsp.RenderMode[]{FILL, OUTLINE, BOTH};
      }
   }

   public static enum ShaderMode {
      MIRROR,
      DEFAULT,
      BLOOM,
      DOUBLE,
      CAMOUFLAGE;

      private static final PlayerEsp.ShaderMode[] shaderModes = $values();
      private static final EnumEntries<PlayerEsp.ShaderMode> enumEntries = EnumEntriesKt.enumEntries(shaderModes);

      private ShaderMode() {
      }

      @NotNull
      public static EnumEntries<PlayerEsp.ShaderMode> getEnumEntries() {
         return enumEntries;
      }

      private static final PlayerEsp.ShaderMode[] $values() {
         return new PlayerEsp.ShaderMode[]{MIRROR, DEFAULT, BLOOM, DOUBLE, CAMOUFLAGE};
      }
   }
}