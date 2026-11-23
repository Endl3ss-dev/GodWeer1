// deobf by pusia
package su.catlean.client.services;

import java.awt.Color;
import java.util.Map;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.ThemeConfig;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Colors;
import su.catlean.utils.render.Render2D;

public final class ThemeManager {
   @NotNull
   public static final ThemeManager INSTANCE = new ThemeManager();
   @NotNull
   private static ThemeConfig themeConfig = Colors.getThemeConfig();
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_EXPO, 1000L);
   @NotNull
   private static Color color = Color.WHITE;
   @NotNull
   private static Color color2 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getPrimaryColor();
   @NotNull
   private static Color color3 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getSecondaryColor();
   @NotNull
   private static Color color4 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getBlurColor();
   @NotNull
   private static Color color5 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getWidgetBlurColor();
   @NotNull
   private static Color color6 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getOutlineColor();
   @NotNull
   private static Color color7 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getTextColor();
   @NotNull
   private static Color color8 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getNonactiveTextColor();
   @NotNull
   private static Color color9 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getSlidersColor();
   @NotNull
   private static Color color10 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getNonActiveModuleColor();
   @NotNull
   private static Color color11 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getModuleColor();
   @NotNull
   private static Color color12 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getModuleOutlineColor();
   @NotNull
   private static Color color13 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig().getNonActiveOutlineModuleColor();

   private ThemeManager() {
   }

   @NotNull
   public final Color getColor() {
      return color;
   }

   public final void updateColor(Color color) {
      ThemeManager.color = color;
   }

   @NotNull
   public final Color getColor2() {
      return color2;
   }

   @NotNull
   public final Color getColor3() {
      return color3;
   }

   @NotNull
   public final Color getColor4() {
      return color4;
   }

   @NotNull
   public final Color getColor5() {
      return color5;
   }

   @NotNull
   public final Color getColor6() {
      return color6;
   }

   @NotNull
   public final Color getColor7() {
      return color7;
   }

   @NotNull
   public final Color getColor8() {
      return color8;
   }

   @NotNull
   public final Color getColor9() {
      return color9;
   }

   @NotNull
   public final Color getColor10() {
      return color10;
   }

   @NotNull
   public final Color getColor11() {
      return color11;
   }

   @NotNull
   public final Color getColor12() {
      return color12;
   }

   @NotNull
   public final Color getColor13() {
      return color13;
   }

   public final void updateThemeConfig(ThemeConfig newTheme) {
      themeConfig = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig();
      ProxyManager.INSTANCE.getThemeDataSource().updateThemeConfig(newTheme);
      ProxyManager.INSTANCE.getSharedDataSource().getSharedConfig().updateText3(newTheme.getId());
      animation.updateAmount(0.0F);
   }

   public final void update() {
      animation.applyDoubleValue(1.0F);
      ThemeConfig themeConfig2 = ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig();
      float amount = animation.getAmount();
      color2 = Render2D.INSTANCE.fetchAlpha(themeConfig.getPrimaryColor(), themeConfig2.getPrimaryColor(), amount);
      color3 = Render2D.INSTANCE.fetchAlpha(themeConfig.getSecondaryColor(), themeConfig2.getSecondaryColor(), amount);
      color4 = Render2D.INSTANCE.fetchAlpha(themeConfig.getBlurColor(), themeConfig2.getBlurColor(), amount);
      color5 = Render2D.INSTANCE.fetchAlpha(themeConfig.getWidgetBlurColor(), themeConfig2.getWidgetBlurColor(), amount);
      color6 = Render2D.INSTANCE.fetchAlpha(themeConfig.getOutlineColor(), themeConfig2.getOutlineColor(), amount);
      color7 = Render2D.INSTANCE.fetchAlpha(themeConfig.getTextColor(), themeConfig2.getTextColor(), amount);
      color8 = Render2D.INSTANCE.fetchAlpha(themeConfig.getNonactiveTextColor(), themeConfig2.getNonactiveTextColor(), amount);
      color9 = Render2D.INSTANCE.fetchAlpha(themeConfig.getSlidersColor(), themeConfig2.getSlidersColor(), amount);
      color10 = Render2D.INSTANCE.fetchAlpha(themeConfig.getNonActiveModuleColor(), themeConfig2.getNonActiveModuleColor(), amount);
      color11 = Render2D.INSTANCE.fetchAlpha(themeConfig.getModuleColor(), themeConfig2.getModuleColor(), amount);
      color12 = Render2D.INSTANCE.fetchAlpha(themeConfig.getModuleOutlineColor(), themeConfig2.getModuleOutlineColor(), amount);
      color13 = Render2D.INSTANCE.fetchAlpha(themeConfig.getNonActiveOutlineModuleColor(), themeConfig2.getNonActiveOutlineModuleColor(), amount);
   }

   @NotNull
   public final Color computeColor(int value) {
      int time = (int)((MinecraftHolder.getTime() / 14.0 + value) % 360);
      time = time >= 180 ? 360 - time : time;
      return Render2D.INSTANCE.fetchAlpha(color2, color3, time / 360.0F);
   }

   @NotNull
   public final Map computeMap(ThemeConfig theme) {
      Pair[] pairs = new Pair[]{
         TuplesKt.to("primary", theme.getPrimaryColor()),
         TuplesKt.to("secondary", theme.getSecondaryColor()),
         TuplesKt.to("blur", theme.getBlurColor()),
         TuplesKt.to("widget-blur", theme.getWidgetBlurColor()),
         TuplesKt.to("outline", theme.getOutlineColor()),
         TuplesKt.to("text", theme.getTextColor()),
         TuplesKt.to("inactive-text", theme.getNonactiveTextColor()),
         TuplesKt.to("sliders", theme.getSlidersColor()),
         TuplesKt.to("inactive-module", theme.getNonActiveModuleColor()),
         TuplesKt.to("module", theme.getModuleColor()),
         TuplesKt.to("module-outline", theme.getModuleOutlineColor()),
         TuplesKt.to("inactive-module-outline", theme.getNonActiveOutlineModuleColor())
      };
      return MapsKt.mapOf(pairs);
   }
}