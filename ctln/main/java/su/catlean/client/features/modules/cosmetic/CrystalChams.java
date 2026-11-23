// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.awt.Color;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.CrystalRenderEvent;
import su.catlean.api.event.events.render.CrystalYOffsetEvent;
import su.catlean.api.event.events.render.FrameBufferEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.render.SetCrystalScaleEvent;
import su.catlean.api.event.events.world.CrystalCreateEvent;
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
import su.catlean.client.features.modules.esp.PlayerEsp;
import su.catlean.client.render.PlayerEspRenderer;
import su.catlean.client.render.PlayerFillEspRenderer;
import su.catlean.client.render.ShaderEspRenderer;
import su.catlean.client.render.TracerRenderer;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Flow;

public final class CrystalChams extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(CrystalChams.class, "types", "getTypes()Lsu/catlean/nl;", 0), new MutablePropertyReference1Impl(CrystalChams.class, "default", "getDefault()Z", 0), new MutablePropertyReference1Impl(CrystalChams.class, "box", "getBox()Z", 0), new MutablePropertyReference1Impl(CrystalChams.class, "frame", "getFrame()Z", 0), new MutablePropertyReference1Impl(CrystalChams.class, "shaderVal", "getShaderVal()Z", 0), new PropertyReference1Impl(CrystalChams.class, "renderMode", "getRenderMode()Lsu/catlean/c2;", 0), new PropertyReference1Impl(CrystalChams.class, "shaderMode", "getShaderMode()Lsu/catlean/kl;", 0), new PropertyReference1Impl(CrystalChams.class, "shaderColor", "getShaderColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(CrystalChams.class, "fillColor", "getFillColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(CrystalChams.class, "lineColor", "getLineColor()Ljava/awt/Color;", 0), new MutablePropertyReference1Impl(CrystalChams.class, "glass", "getGlass()Z", 0), new MutablePropertyReference1Impl(CrystalChams.class, "borders", "getBorders()Z", 0), new MutablePropertyReference1Impl(CrystalChams.class, "radiusValue", "getRadiusValue()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "distanceValue", "getDistanceValue()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "glowAlphaValue", "getGlowAlphaValue()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "lineWithValue", "getLineWithValue()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "alpha", "getAlpha()F", 0), new PropertyReference1Impl(CrystalChams.class, "secondColor", "getSecondColor()Ljava/awt/Color;", 0), new MutablePropertyReference1Impl(CrystalChams.class, "colorModeValue", "getColorModeValue()Lsu/catlean/d5;", 0), new MutablePropertyReference1Impl(CrystalChams.class, "glowQualityValue", "getGlowQualityValue()I", 0), new MutablePropertyReference1Impl(CrystalChams.class, "glowRadiusValue", "getGlowRadiusValue()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "glowAlphaMultValue", "getGlowAlphaMultValue()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "fillAlphaMultValue", "getFillAlphaMultValue()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "saturation", "getSaturation()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "brightness", "getBrightness()F", 0), new MutablePropertyReference1Impl(CrystalChams.class, "hDistance", "getHDistance()I", 0), new MutablePropertyReference1Impl(CrystalChams.class, "vDistance", "getVDistance()I", 0), new MutablePropertyReference1Impl(CrystalChams.class, "texture", "getTexture()Z", 0), new PropertyReference1Impl(CrystalChams.class, "scale", "getScale()F", 0), new PropertyReference1Impl(CrystalChams.class, "ageScale", "getAgeScale()Z", 0), new PropertyReference1Impl(CrystalChams.class, "speed", "getSpeed()F", 0), new PropertyReference1Impl(CrystalChams.class, "amplitude", "getAmplitude()F", 0) };
   @NotNull
   public static final CrystalChams INSTANCE = new CrystalChams();
   @NotNull
   private static final GroupSetting types = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "types", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting defaultValue = SettingFactory.defaultImpl(INSTANCE, "default", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final BooleanSetting box = SettingFactory.defaultImpl(INSTANCE, "box", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final BooleanSetting frame = SettingFactory.defaultImpl(INSTANCE, "frame", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final BooleanSetting shader = SettingFactory.defaultImpl(INSTANCE, "shader", false, INSTANCE.getTypes(), null, 8, null);
   @NotNull
   private static final EnumSetting renderMode = SettingFactory.defaultImpl(
      INSTANCE, "render-mode", PlayerEsp.RenderMode.BOTH, null, () -> INSTANCE.getBox() || INSTANCE.getDefault(), 4, null
   );
   @NotNull
   private static final EnumSetting shaderMode = SettingFactory.defaultImpl(
      INSTANCE, "shader-mode", PlayerEsp.ShaderMode.DEFAULT, null, () -> INSTANCE.getShaderVal(), 4, null
   );
   @NotNull
   private static final ColorSetting shaderColor = SettingFactory.defaultImpl(
      INSTANCE, "shader-color", new Color(255, 255, 255, 255), null, () -> INSTANCE.getShaderVal(), 4, null
   );
   @NotNull
   private static final ColorSetting fill = SettingFactory.defaultImpl(INSTANCE, "fill", new Color(255, 255, 255, 120), null, null, 12, null);
   @NotNull
   private static final ColorSetting line = SettingFactory.defaultImpl(INSTANCE, "line", new Color(255, 255, 255, 255), null, null, 12, null);
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
      RangesKt.rangeTo(0.0F, 15.0F),
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
      RangesKt.rangeTo(0.0F, 15.0F),
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
   private static final EnumSetting colorMode = SettingFactory.defaultImpl(
      INSTANCE, "color-mode", PlayerEsp.ColorMode.RGB, null, () -> INSTANCE.getShaderMode() == PlayerEsp.ShaderMode.DOUBLE && INSTANCE.getShaderVal(), 4, null
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
   private static final BooleanSetting texture = SettingFactory.defaultImpl(INSTANCE, "texture", true, null, null, 12, null);
   @NotNull
   private static final FloatSetting scale = SettingFactory.defaultImpl(INSTANCE, "scale", 1.0F, RangesKt.rangeTo(0.1F, 2.0F), null, null, 24, null);
   @NotNull
   private static final BooleanSetting ageScale = SettingFactory.defaultImpl(INSTANCE, "age-scale", false, null, () -> INSTANCE.getScale() != 1.0F, 4, null);
   @NotNull
   private static final FloatSetting speed = SettingFactory.defaultImpl(INSTANCE, "speed", 0.2F, RangesKt.rangeTo(0.0F, 1.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting amplitude = SettingFactory.defaultImpl(INSTANCE, "amplitude", 0.4F, RangesKt.rangeTo(0.0F, 1.0F), null, null, 24, null);
   @NotNull
   private static final ShaderEspRenderer shaderEspRenderer = new ShaderEspRenderer();

   private CrystalChams() {
      super("crystal-chams", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final GroupValue getTypes() {
      return (GroupValue)types.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getDefault() {
      return (Boolean)defaultValue.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getBox() {
      return (Boolean)box.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getFrame() {
      return (Boolean)frame.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getShaderVal() {
      return (Boolean)shader.computeObject(this, $$delegatedProperties[4]);
   }

   private final PlayerEsp.RenderMode getRenderMode() {
      return (PlayerEsp.RenderMode)renderMode.computeObject(this, $$delegatedProperties[5]);
   }

   private final PlayerEsp.ShaderMode getShaderMode() {
      return (PlayerEsp.ShaderMode)shaderMode.computeObject(this, $$delegatedProperties[6]);
   }

   private final Color getShaderColor() {
      return (Color)shaderColor.computeObject(this, $$delegatedProperties[7]);
   }

   private final Color getFillColor() {
      return (Color)fill.computeObject(this, $$delegatedProperties[8]);
   }

   private final Color getLineColor() {
      return (Color)line.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getGlass() {
      return (Boolean)glass.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getBorders() {
      return (Boolean)border.computeObject(this, $$delegatedProperties[11]);
   }

   private final float getRadiusValue() {
      return ((Number)radius.computeObject(this, $$delegatedProperties[12])).floatValue();
   }

   private final float getDistanceValue() {
      return ((Number)distance.computeObject(this, $$delegatedProperties[13])).floatValue();
   }

   private final float getGlowAlphaValue() {
      return ((Number)glowAlpha.computeObject(this, $$delegatedProperties[14])).floatValue();
   }

   private final float getLineWithValue() {
      return ((Number)lineWidth.computeObject(this, $$delegatedProperties[15])).floatValue();
   }

   private final float getAlpha() {
      return ((Number)alpha.computeObject(this, $$delegatedProperties[16])).floatValue();
   }

   private final Color getSecondColor() {
      return (Color)secondColor.computeObject(this, $$delegatedProperties[17]);
   }

   private final PlayerEsp.ColorMode getColorModeValue() {
      return (PlayerEsp.ColorMode)colorMode.computeObject(this, $$delegatedProperties[18]);
   }

   private final int getGlowQualityValue() {
      return ((Number)glowQuality.computeObject(this, $$delegatedProperties[19])).intValue();
   }

   private final float getGlowRadiusValue() {
      return ((Number)glowRadius.computeObject(this, $$delegatedProperties[20])).floatValue();
   }

   private final float getGlowAlphaMultValue() {
      return ((Number)glowAlphaMultiplier.computeObject(this, $$delegatedProperties[21])).floatValue();
   }

   private final float getFillAlphaMultValue() {
      return ((Number)fillAlphaMultiplier.computeObject(this, $$delegatedProperties[22])).floatValue();
   }

   private final float getSaturation() {
      return ((Number)saturation.computeObject(this, $$delegatedProperties[23])).floatValue();
   }

   private final float getBrightness() {
      return ((Number)brightness.computeObject(this, $$delegatedProperties[24])).floatValue();
   }

   private final int getHDistance() {
      return ((Number)maxHorizontalDistance.computeObject(this, $$delegatedProperties[25])).intValue();
   }

   private final int getVDistance() {
      return ((Number)maxVerticalDistance.computeObject(this, $$delegatedProperties[26])).intValue();
   }

   public final boolean getTexture() {
      return (Boolean)texture.computeObject(this, $$delegatedProperties[27]);
   }

   private final float getScale() {
      return ((Number)scale.computeObject(this, $$delegatedProperties[28])).floatValue();
   }

   private final boolean getAgeScale() {
      return (Boolean)ageScale.computeObject(this, $$delegatedProperties[29]);
   }

   private final float getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[30])).floatValue();
   }

   private final float getAmplitude() {
      return ((Number)amplitude.computeObject(this, $$delegatedProperties[31])).floatValue();
   }

   @NotNull
   public final ShaderEspRenderer getShaderEspRenderer() {
      return shaderEspRenderer;
   }

   @Flow
   public final void onRender3D(Render3DEvent e) {
      if (this.getShaderVal()) {
         shaderEspRenderer.render(e.getStack(), true, this.getVDistance(), this.getHDistance());
      }

      if (this.getBox()) {
         PlayerFillEspRenderer.INSTANCE
            .applyGetBoundingBox(this.getRenderMode(), this.getFillColor(), this.getLineColor(), this.getVDistance(), this.getHDistance());
      }

      if (this.getDefault()) {
         PlayerEspRenderer.INSTANCE
            .render(e.getStack(), this.getRenderMode(), this.getFillColor(), this.getLineColor(), this.getVDistance(), this.getHDistance());
      }
   }

   @Flow
   public final void onRender2D(Render2DEvent e) {
      if (this.getFrame()) {
         TracerRenderer.INSTANCE.render(e.getContext(), this.getLineColor());
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
            this.getLineWithValue(),
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
   public final void onCrystalCreate(CrystalCreateEvent e) {
      if (this.getAgeScale()) {
         e.cancel();
      }
   }

   @Flow
   public final void onFrameBuffer(FrameBufferEvent event) {
      if (this.getShaderVal() && shaderEspRenderer.getMainTarget() != null && shaderEspRenderer.isFlag()) {
         event.setFrameBuffer((RenderTarget)shaderEspRenderer.getMainTarget());
         event.cancel();
      }
   }

   @Flow
   private final void onCrystalRender(CrystalRenderEvent crystalRenderEvent) {
      if (!this.getTexture() && !shaderEspRenderer.isFlag()) {
         crystalRenderEvent.cancel();
      }
   }

   @Flow
   private final void onSetCrystalScale(SetCrystalScaleEvent setCrystalScaleEvent) {
      setCrystalScaleEvent.setScale(this.computeAmount(setCrystalScaleEvent.getAge()));
      setCrystalScaleEvent.cancel();
   }

   @Flow
   private final void onCrystalYOffset(CrystalYOffsetEvent crystalYOffsetEvent) {
      crystalYOffsetEvent.setOffset(this.computeAmount2(crystalYOffsetEvent.getAge()));
      crystalYOffsetEvent.cancel();
   }

   public final float computeAmount(float amount) {
      return !this.getAgeScale()
         ? this.getScale()
         : this.getScale()
            * (float)((Number)NotificationManager.Notification.Type.OUT_EXPO.getCallback().invoke(RangesKt.coerceIn(amount / 10.0, 0.0, 1.0))).doubleValue();
   }

   private final float computeAmount2(float amount) {
      float amount2 = (float)Math.sin(amount * this.getSpeed()) / 2.0F + 0.5F;
      amount2 = (amount2 * amount2 + amount2) * this.getAmplitude();
      return amount2 - 1.4F;
   }

   static {
   }
}