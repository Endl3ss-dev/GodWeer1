// deobf by pusia
package su.catlean.api.data;

import java.awt.Color;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.serialization.ColorSerializer;

@Serializable
public final class ThemeConfig {
   @NotNull
   public static final ThemeConfig.Companion companion = new ThemeConfig.Companion(null);
   @NotNull
   private final String id;
   @NotNull
   private Color primaryColor;
   @NotNull
   private Color secondaryColor;
   @NotNull
   private Color blurColor;
   @NotNull
   private Color widgetBlurColor;
   @NotNull
   private Color outlineColor;
   @NotNull
   private Color textColor;
   @NotNull
   private Color nonactiveTextColor;
   @NotNull
   private Color slidersColor;
   @NotNull
   private Color nonActiveModuleColor;
   @NotNull
   private Color moduleColor;
   @NotNull
   private Color moduleOutlineColor;
   @NotNull
   private Color nonActiveOutlineModuleColor;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public ThemeConfig(
      String id,
      Color primaryColor,
      Color secondaryColor,
      Color blurColor,
      Color widgetBlurColor,
      Color outlineColor,
      Color textColor,
      Color nonactiveTextColor,
      Color slidersColor,
      Color nonActiveModuleColor,
      Color moduleColor,
      Color moduleOutlineColor,
      Color nonActiveOutlineModuleColor
   ) {
      this.id = id;
      this.primaryColor = primaryColor;
      this.secondaryColor = secondaryColor;
      this.blurColor = blurColor;
      this.widgetBlurColor = widgetBlurColor;
      this.outlineColor = outlineColor;
      this.textColor = textColor;
      this.nonactiveTextColor = nonactiveTextColor;
      this.slidersColor = slidersColor;
      this.nonActiveModuleColor = nonActiveModuleColor;
      this.moduleColor = moduleColor;
      this.moduleOutlineColor = moduleOutlineColor;
      this.nonActiveOutlineModuleColor = nonActiveOutlineModuleColor;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final Color getPrimaryColor() {
      return this.primaryColor;
   }

   public final void updateColor(Color color) {
      this.primaryColor = color;
   }

   @NotNull
   public final Color getSecondaryColor() {
      return this.secondaryColor;
   }

   public final void updateColor2(Color color) {
      this.secondaryColor = color;
   }

   @NotNull
   public final Color getBlurColor() {
      return this.blurColor;
   }

   public final void updateColor3(Color color) {
      this.blurColor = color;
   }

   @NotNull
   public final Color getWidgetBlurColor() {
      return this.widgetBlurColor;
   }

   public final void updateColor4(Color color) {
      this.widgetBlurColor = color;
   }

   @NotNull
   public final Color getOutlineColor() {
      return this.outlineColor;
   }

   public final void updateColor5(Color color) {
      this.outlineColor = color;
   }

   @NotNull
   public final Color getTextColor() {
      return this.textColor;
   }

   public final void updateColor6(Color color) {
      this.textColor = color;
   }

   @NotNull
   public final Color getNonactiveTextColor() {
      return this.nonactiveTextColor;
   }

   public final void updateColor7(Color color) {
      this.nonactiveTextColor = color;
   }

   @NotNull
   public final Color getSlidersColor() {
      return this.slidersColor;
   }

   public final void updateColor8(Color color) {
      this.slidersColor = color;
   }

   @NotNull
   public final Color getNonActiveModuleColor() {
      return this.nonActiveModuleColor;
   }

   public final void updateColor9(Color color) {
      this.nonActiveModuleColor = color;
   }

   @NotNull
   public final Color getModuleColor() {
      return this.moduleColor;
   }

   public final void updateColor10(Color color) {
      this.moduleColor = color;
   }

   @NotNull
   public final Color getModuleOutlineColor() {
      return this.moduleOutlineColor;
   }

   public final void updateColor11(Color color) {
      this.moduleOutlineColor = color;
   }

   @NotNull
   public final Color getNonActiveOutlineModuleColor() {
      return this.nonActiveOutlineModuleColor;
   }

   public final void updateColor12(Color color) {
      this.nonActiveOutlineModuleColor = color;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final Color getPrimaryColor2() {
      return this.primaryColor;
   }

   @NotNull
   public final Color getSecondaryColor2() {
      return this.secondaryColor;
   }

   @NotNull
   public final Color getBlurColor2() {
      return this.blurColor;
   }

   @NotNull
   public final Color getWidgetBlurColor2() {
      return this.widgetBlurColor;
   }

   @NotNull
   public final Color getOutlineColor2() {
      return this.outlineColor;
   }

   @NotNull
   public final Color getTextColor2() {
      return this.textColor;
   }

   @NotNull
   public final Color getNonactiveTextColor2() {
      return this.nonactiveTextColor;
   }

   @NotNull
   public final Color getSlidersColor2() {
      return this.slidersColor;
   }

   @NotNull
   public final Color getNonActiveModuleColor2() {
      return this.nonActiveModuleColor;
   }

   @NotNull
   public final Color getModuleColor2() {
      return this.moduleColor;
   }

   @NotNull
   public final Color getModuleOutlineColor2() {
      return this.moduleOutlineColor;
   }

   @NotNull
   public final Color getNonActiveOutlineModuleColor2() {
      return this.nonActiveOutlineModuleColor;
   }

   @NotNull
   @Override
   public String toString() {
      Color nonActiveOutlineModuleColor2 = this.nonActiveOutlineModuleColor;
      Color moduleOutlineColor2 = this.moduleOutlineColor;
      Color moduleColor2 = this.moduleColor;
      Color nonActiveModuleColor2 = this.nonActiveModuleColor;
      Color slidersColor2 = this.slidersColor;
      Color nonactiveTextColor2 = this.nonactiveTextColor;
      Color textColor2 = this.textColor;
      Color outlineColor2 = this.outlineColor;
      Color widgetBlurColor2 = this.widgetBlurColor;
      Color blurColor2 = this.blurColor;
      Color secondaryColor2 = this.secondaryColor;
      Color primaryColor2 = this.primaryColor;
      String text = this.id;
      return "ThemeConfig(id="
         + text
         + ", primaryColor="
         + primaryColor2
         + ", secondaryColor="
         + secondaryColor2
         + ", blurColor="
         + blurColor2
         + ", widgetBlurColor="
         + widgetBlurColor2
         + ", outlineColor="
         + outlineColor2
         + ", textColor="
         + textColor2
         + ", nonactiveTextColor="
         + nonactiveTextColor2
         + ", slidersColor="
         + slidersColor2
         + ", nonActiveModuleColor="
         + nonActiveModuleColor2
         + ", moduleColor="
         + moduleColor2
         + ", moduleOutlineColor="
         + moduleOutlineColor2
         + ", nonActiveOutlineModuleColor="
         + nonActiveOutlineModuleColor2
         + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      hashCode = hashCode * 31 + this.primaryColor.hashCode();
      hashCode = hashCode * 31 + this.secondaryColor.hashCode();
      hashCode = hashCode * 31 + this.blurColor.hashCode();
      hashCode = hashCode * 31 + this.widgetBlurColor.hashCode();
      hashCode = hashCode * 31 + this.outlineColor.hashCode();
      hashCode = hashCode * 31 + this.textColor.hashCode();
      hashCode = hashCode * 31 + this.nonactiveTextColor.hashCode();
      hashCode = hashCode * 31 + this.slidersColor.hashCode();
      hashCode = hashCode * 31 + this.nonActiveModuleColor.hashCode();
      hashCode = hashCode * 31 + this.moduleColor.hashCode();
      hashCode = hashCode * 31 + this.moduleOutlineColor.hashCode();
      return hashCode * 31 + this.nonActiveOutlineModuleColor.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ThemeConfig themeConfig)) {
         return false;
      } else if (!Intrinsics.areEqual(this.id, themeConfig.id)) {
         return false;
      } else if (!Intrinsics.areEqual(this.primaryColor, themeConfig.primaryColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.secondaryColor, themeConfig.secondaryColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.blurColor, themeConfig.blurColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.widgetBlurColor, themeConfig.widgetBlurColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.outlineColor, themeConfig.outlineColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.textColor, themeConfig.textColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.nonactiveTextColor, themeConfig.nonactiveTextColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.slidersColor, themeConfig.slidersColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.nonActiveModuleColor, themeConfig.nonActiveModuleColor)) {
         return false;
      } else if (!Intrinsics.areEqual(this.moduleColor, themeConfig.moduleColor)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.moduleOutlineColor, themeConfig.moduleOutlineColor)
            ? false
            : Intrinsics.areEqual(this.nonActiveOutlineModuleColor, themeConfig.nonActiveOutlineModuleColor);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(ThemeConfig themeConfig, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeStringElement(serialDescriptor, 0, themeConfig.id);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)lazys2[1].getValue(), themeConfig.primaryColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 2, (SerializationStrategy)lazys2[2].getValue(), themeConfig.secondaryColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 3, (SerializationStrategy)lazys2[3].getValue(), themeConfig.blurColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 4, (SerializationStrategy)lazys2[4].getValue(), themeConfig.widgetBlurColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 5, (SerializationStrategy)lazys2[5].getValue(), themeConfig.outlineColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 6, (SerializationStrategy)lazys2[6].getValue(), themeConfig.textColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 7, (SerializationStrategy)lazys2[7].getValue(), themeConfig.nonactiveTextColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 8, (SerializationStrategy)lazys2[8].getValue(), themeConfig.slidersColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 9, (SerializationStrategy)lazys2[9].getValue(), themeConfig.nonActiveModuleColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 10, (SerializationStrategy)lazys2[10].getValue(), themeConfig.moduleColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 11, (SerializationStrategy)lazys2[11].getValue(), themeConfig.moduleOutlineColor);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 12, (SerializationStrategy)lazys2[12].getValue(), themeConfig.nonActiveOutlineModuleColor);
   }

   public ThemeConfig(
      int value,
      String text,
      Color color,
      Color color2,
      Color color3,
      Color color4,
      Color color5,
      Color color6,
      Color color7,
      Color color8,
      Color color9,
      Color color10,
      Color color11,
      Color color12,
      SerializationConstructorMarker serializationConstructorMarker
   ) {
      if (8191 != (8191)) {
         PluginExceptionsKt.throwMissingFieldException(value, 8191, ThemeConfig$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.primaryColor = color;
      this.secondaryColor = color2;
      this.blurColor = color3;
      this.widgetBlurColor = color4;
      this.outlineColor = color5;
      this.textColor = color6;
      this.nonactiveTextColor = color7;
      this.slidersColor = color8;
      this.nonActiveModuleColor = color9;
      this.moduleColor = color10;
      this.moduleOutlineColor = color11;
      this.nonActiveOutlineModuleColor = color12;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         null,
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> new ColorSerializer())
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ThemeConfig$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}