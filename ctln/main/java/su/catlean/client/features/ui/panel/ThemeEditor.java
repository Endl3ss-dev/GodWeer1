// deobf by pusia
package su.catlean.client.features.ui.panel;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.Rectangle;
import su.catlean.api.data.ThemeConfig;
import su.catlean.client.features.ui.panel.components.ColorEditor;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class ThemeEditor {
   @NotNull
   public static final ThemeEditor INSTANCE = new ThemeEditor();
   private static float amount = 120.0F;
   private static float amount2 = 270.0F;
   private static float amount3 = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() - amount - 5.0F;
   private static float amount4 = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - amount2 / 2.0F;
   @Nullable
   private static ThemeConfig themeConfig;
   @Nullable
   private static ThemeConfig themeConfig2;
   @NotNull
   private static List<ColorEditor> items = new ArrayList();
   private static float amount5;
   @NotNull
   private static String text = "Enter name";
   private static boolean flag;
   private static boolean flag2;
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   @NotNull
   private static final Animation animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   @NotNull
   private static final Animation animation3 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("panels/folder", 10, 10);
   @NotNull
   private static final SvgTexture svgTexture2 = new SvgTexture("back", 10, 10);

   private ThemeEditor() {
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      ThemeEditor.amount = amount;
   }

   public final float getAmount2() {
      return amount2;
   }

   public final void setAmount2(float amount) {
      amount2 = amount;
   }

   @NotNull
   public final SvgTexture getSvgTexture2() {
      return svgTexture2;
   }

   public final void render(GuiGraphicsExtractor context, int value, int value2) {
      boolean var137 = false;
      float amount32;
      label165: {
         animation3.applyDoubleValue(flag ? 1.0F : 0.0F);
         float amount33 = amount3 + amount - 21.0F;
         float amount34 = amount3 + amount - 7.0F;
         float value3 = value;
         if (amount33 <= value3 ? value3 <= amount34 : false) {
            amount33 = amount4 + 31.0F;
            amount34 = amount4 + 45.0F;
            value3 = value2;
            if ((amount33 <= value3 ? value3 <= amount34 : false) && flag && themeConfig2 == null) {
               amount32 = 1.0F;
               break label165;
            }
         }

         amount32 = 0.0F;
      }

      Matrix3x2fStack pose;
      float amount42;
      Color brighter;
      label156: {
         animation2.applyDoubleValue(amount32);
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount3,
            amount4,
            amount,
            amount2,
            5.0F,
            0.0F,
            0.0F,
            1.0F,
            ThemeManager.INSTANCE.getColor4(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         pose = context.pose();
         amount32 = amount3 + 5.0F;
         amount42 = amount4 + 6.0F;
         float amount35 = amount3 + 5.0F;
         float amount36 = amount3 + 20.0F;
         float value4 = value;
         if (amount35 <= value4 ? value4 <= amount36 : false) {
            amount35 = amount4 + 6.0F;
            amount36 = amount4 + 21.0F;
            value4 = value2;
            if (amount35 <= value4 ? value4 <= amount36 : false) {
               brighter = ThemeManager.INSTANCE.getColor4().brighter().brighter().brighter().brighter().brighter();
               break label156;
            }
         }

         brighter = ThemeManager.INSTANCE.getColor6().brighter();
      }

      Color color = brighter;
      Color brighter2 = ThemeManager.INSTANCE.getColor6().brighter();
      Rectangle rectangle = null;
      short shortValue = 14336;
      float value5 = 0.0F;
      float value6 = 0.0F;
      float value7 = 0.0F;
      Color color2 = brighter2;
      float value8 = 1.0F;
      TexturedQuadPass.defaultImpl(pose, amount32, amount42, 15.0F, 15.0F, 3.0F, 1.0F, 0.0F, value8, color, color2, value7, value6, value5, shortValue, rectangle);
      SvgTexture.defaultImpl(svgTexture, (Matrix3x2f)context.pose(), amount3 + 7.5, amount4 + 7.5, 0.0F, 0.0F, null, null, 0.0F, 248, null);
      Fonts.getFontRenderer9().render2(context, "Theme Editor", amount3 + amount / 2.0F, amount4 + 5, 10);
      Render2D instance = Render2D.INSTANCE;
      Matrix3x2fStack pose2 = context.pose();
      amount42 = amount3 + 6;
      float amount43 = amount4 + 24;
      float amount6 = amount - 12;
      Color[] colors = new Color[]{new Color(1157627903, true)};
      float value9 = 0.5F;
      value5 = amount6;
      value6 = amount43;
      value7 = amount42;
      Matrix3x2fStack matrix3x2fStack = pose2;
      instance.render(matrix3x2fStack, value7, value6, value5, value9, colors);
      themeConfig = null;
      if (themeConfig2 == null) {
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount3 + 4.0F,
            amount4 + 28.0F,
            amount - 8.0F,
            20.0F,
            4.0F,
            0.0F,
            0.1F,
            1.0F,
            ThemeManager.INSTANCE.getColor4(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount3 + amount - 21.0F,
            amount4 + 31.0F,
            14.0F,
            14.0F,
            3.0F,
            0.0F,
            0.2F + animation2.getAmount() / 10.0F,
            1.0F,
            ThemeManager.INSTANCE.getColor4(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         Render2D instance2 = Render2D.INSTANCE;
         Matrix3x2fStack pose3 = context.pose();
         amount42 = amount3 + amount - 14.0F;
         amount43 = amount4 + 35.0F;
         Color[] colors2 = new Color[]{ThemeManager.INSTANCE.getColor8()};
         float value10 = 6.0F;
         value5 = 1.0F;
         value6 = amount43;
         value7 = amount42;
         Matrix3x2fStack matrix3x2fStack2 = pose3;
         instance2.render(matrix3x2fStack2, value7, value6, value5, value10, colors2);
         Render2D instance3 = Render2D.INSTANCE;
         Matrix3x2fStack pose4 = context.pose();
         amount42 = amount3 + amount - 16.5F;
         amount43 = amount4 + 37.5F;
         Color[] colors3 = new Color[]{ThemeManager.INSTANCE.getColor8()};
         float value11 = 1.0F;
         value5 = 6.0F;
         value6 = amount43;
         value7 = amount42;
         Matrix3x2fStack matrix3x2fStack3 = pose4;
         instance3.render(matrix3x2fStack3, value7, value6, value5, value11, colors3);
         Fonts.getFontRenderer2()
            .render(
               context,
               text + (System.currentTimeMillis() % 1000 > 500L && flag ? "_" : ""),
               amount3 + 10.0F,
               amount4 + 31.5F,
               Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor8(), ThemeManager.INSTANCE.getColor7(), animation3.getAmount())
            );
         amount2 = ProxyManager.INSTANCE.getThemeDataSource().getItems().size() * 24 + 52.0F;
         Iterable items2 = ProxyManager.INSTANCE.getThemeDataSource().getItems();
         int value12 = 0;

         for (Object object : items2) {
            int value13 = value12++;
            if (value13 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            ThemeConfig themeConfig3 = (ThemeConfig)object;
            float amount37 = amount3 + 4.0F;
            float amount38 = amount3 + 4.0F + amount - 8.0F;
            float value14 = value;
            if (amount37 <= value14 ? value14 <= amount38 : false) {
               amount37 = amount4 + 52.0F + value13 * 24;
               amount38 = amount4 + 72.0F + value13 * 24;
               value14 = value2;
               if (amount37 <= value14 ? value14 <= amount38 : false) {
                  themeConfig = themeConfig3;
               }
            }

            TexturedQuadPass.defaultImpl(
               context.pose(),
               amount3 + 4.0F,
               amount4 + 52.0F + value13 * 24,
               amount - 8.0F,
               20.0F,
               4.0F,
               2.0F,
               0.0F,
               1.0F,
               themeConfig3.getBlurColor(),
               themeConfig3.getOutlineColor(),
               0.0F,
               0.0F,
               0.0F,
               14336,
               null
            );
            amount37 = Intrinsics.areEqual(ProxyManager.INSTANCE.getThemeDataSource().getThemeConfig(), themeConfig3)
               ? 0.55F
               : (Intrinsics.areEqual(themeConfig, themeConfig3) ? 0.5F : 0.4F);
            float amount39 = amount3 + 4.0F;
            amount32 = amount4 + 52.0F + value13 * 24;
            amount42 = amount - 8.0F;
            Color color3 = Render2D.INSTANCE.computeColor(themeConfig3.getPrimaryColor(), amount37);
            Color color4 = Render2D.INSTANCE.computeColor(themeConfig3.getSecondaryColor(), amount37);
            Color color5 = Render2D.INSTANCE.computeColor(themeConfig3.getBlurColor(), amount37);
            Color color6 = Render2D.INSTANCE.computeColor(themeConfig3.getOutlineColor(), amount37);
            Matrix3x2fStack pose5 = context.pose();
            rectangle = null;
            shortValue = 3072;
            Object object2 = null;
            value6 = 0.0F;
            Matrix3x2fStack matrix3x2fStack4 = pose5;
            color2 = color6;
            Color color7 = color5;
            Color color8 = color4;
            Color color9 = color3;
            float value15 = 4.0F;
            BoxPass.defaultImpl(amount39, amount32, amount42, 20.0F, value15, color9, color8, color7, color2, matrix3x2fStack4, value6, (Color)object2, shortValue, rectangle);
            Fonts.getFontRenderer2()
               .render2(context, themeConfig3.getId(), amount3 + 4.0F + (amount / 2.0F - 4.0F), amount4 + 61.0F + value13 * 24, themeConfig3.getTextColor());
         }
      } else {
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount3 + 4.0F,
            amount4 + 28.0F,
            amount - 8.0F,
            20.0F,
            4.0F,
            0.0F,
            0.1F,
            1.0F,
            themeConfig2.getBlurColor(),
            themeConfig2.getOutlineColor(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         Fonts.getFontRenderer2()
            .render(context, themeConfig2.getId(), amount3 + 10.0F, amount4 + 31.5F, Render2D.INSTANCE.computeColor(themeConfig2.getPrimaryColor(), 0.5F));
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount3 + amount - 19.0F,
            amount4 + 33.0F,
            10.5F,
            10.5F,
            2.0F,
            2.0F,
            0.0F,
            1.0F,
            ThemeManager.INSTANCE.getColor4(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         SvgTexture.defaultImpl(
            svgTexture2,
            (Matrix3x2f)context.pose(),
            amount3 + amount - 19.0,
            amount4 + 32.0,
            0.0F,
            0.0F,
            ThemeManager.INSTANCE.getColor7(),
            null,
            0.0F,
            216,
            null
         );
         Render2D instance4 = Render2D.INSTANCE;
         rectangle = new Rectangle(amount3, amount4 + 52, amount3 + amount, amount4 + amount2);
         instance4.render(context, (Rectangle)rectangle);
         float value16 = 0.0F;
         Iterable<ColorEditor> items3 = items;

         for (Object object3 : items3) {
            ColorEditor colorEditor = (ColorEditor)object3;
            colorEditor.setAmount(amount3 + 5.0F);
            colorEditor.setAmount2(amount4 + 52.0F + value16 + amount5);
            colorEditor.render(context, value, value2);
            value16 += colorEditor.getAmount4() + 2.0F;
         }

         label131: {
            float amount310 = amount3 + 5.0F;
            float amount311 = amount3 + 115.0F;
            float value17 = value;
            if (amount310 <= value17 ? value17 <= amount311 : false) {
               float amount44 = amount4 + 52.0F + value16 + amount5;
               float amount45 = amount4 + 64.0F + value16 + amount5;
               value17 = value2;
               if (amount44 <= value17 ? value17 <= amount45 : false) {
                  var137 = true;
                  break label131;
               }
            }

            var137 = false;
         }

         flag2 = var137;
         animation.applyDoubleValue(flag2 ? 1.0F : 0.0F);
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount3 + 5.0F,
            amount4 + 52.0F + value16 + amount5,
            110.0F,
            12.0F,
            3.0F,
            2.0F,
            0.4F,
            1.0F,
            Render2D.INSTANCE.computeColor(Color.RED, 0.5F + animation.getAmount() / 5.0F),
            Color.RED,
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         Fonts.getFontRenderer3()
            .render2(
               context,
               LocalizationManager.INSTANCE.computeText("hub.theme-editor.delete", new Object[0]),
               amount3 + 5.0F + 55.0F,
               amount4 + 57.0F + value16 + amount5,
               ThemeManager.INSTANCE.getColor7()
            );
         float value18 = value16 + 16.0F;
         Render2D.INSTANCE.render(context);
         if (value18 != 0.0F) {
            amount5 = Math.clamp(amount5, -value18 + 206, 0.0F);
         }

         amount2 = 262.0F;
      }

      amount3 = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() - amount - 5.0F;
      amount4 = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - amount2 / 2.0F;
   }

   public final void applyClear(double amount, double amount2, int value) {
      if (themeConfig2 != null && flag2) {
         ProxyManager.INSTANCE.getThemeDataSource().getItems().remove(themeConfig2);
         themeConfig2 = null;
      } else {
         float amount32 = amount3 + 5.0F;
         float amount33 = amount3 + 20.0F;
         float amount6 = (float)amount;
         if (amount32 <= amount6 ? amount6 <= amount33 : false) {
            amount32 = amount4 + 6.0F;
            amount33 = amount4 + 21.0F;
            amount6 = (float)amount2;
            if (amount32 <= amount6 ? amount6 <= amount33 : false) {
               OS platform = Util.getPlatform();
               File file = ProxyManager.INSTANCE.getThemeDataSource().getFile();
               OS value2 = platform;
               if (!file.exists()) {
                  try {
                     file.createNewFile();
                  } catch (java.io.IOException exception) {
                     throw new RuntimeException(exception);
                  }
               }

               value2.openFile(file);
            }
         }

         amount32 = amount3 + ThemeEditor.amount - 21.0F;
         if (amount <= amount3 + ThemeEditor.amount - 7.0F ? amount32 <= amount : false) {
            amount32 = amount4 + 31.0F;
            if ((amount2 <= amount4 + 45.0F ? amount32 <= amount2 : false) && flag && themeConfig2 == null) {
               this.update();
               return;
            }
         }

         amount32 = amount3 + 4.0F;
         if (amount <= amount3 + 4.0F + ThemeEditor.amount - 8.0F ? amount32 <= amount : false) {
            amount32 = amount4 + 28.0F;
            if (amount2 <= amount4 + 48.0F ? amount32 <= amount2 : false) {
               ThemeConfig themeConfig22 = themeConfig2;
               if (themeConfig22 != null) {
                  themeConfig2 = null;
                  return;
               }

               if (flag) {
                  flag = false;
                  text = "Enter name";
               } else {
                  flag = true;
                  text = "";
               }

               return;
            }
         }

         Iterable<ColorEditor> items2 = items;

         for (Object object : items2) {
            ColorEditor colorEditor = (ColorEditor)object;
            colorEditor.updateAmount(amount, amount2, value);
         }

         if (value == 0) {
            if (themeConfig != null) {
               ThemeConfig themeConfig3 = themeConfig;
               ThemeManager.INSTANCE.updateThemeConfig(themeConfig3);
            }
         } else if (themeConfig != null) {
            ThemeConfig themeConfig4 = themeConfig;
            items.clear();
            themeConfig2 = themeConfig;
            int value3 = 0;
            Map map = ThemeManager.INSTANCE.computeMap(themeConfig2);

            for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)map.entrySet()) {
               items.add(new ColorEditor((String)entrySetItem.getKey(), themeConfig2, amount3 + 4.0F, amount4 + 55.0F + value3 * 16.0F, (Color)entrySetItem.getValue()));
               value3++;
            }
         }
      }
   }

   public final void updateAmount(double amount, double amount2, int value) {
      for (Object item : items) {
         ColorEditor colorEditor = (ColorEditor)item;
         colorEditor.update();
      }
   }

   public final void updateValue(int value, int value2, int value3) {
      for (Object item : items) {
         ColorEditor colorEditor = (ColorEditor)item;
         colorEditor.updateValue(value);
      }

      if (flag) {
         switch (value) {
            case 256:
               flag = false;
               text = "Enter name";
               break;
            case 257:
               this.update();
            case 258:
            default:
               break;
            case 259:
               text = StringsKt.dropLast(text, 1);
         }
      }
   }

   private final void update() {
      if (StringsKt.isBlank(text)) {
         flag = false;
         text = "Enter name";
      } else {
         ThemeManager instance = ThemeManager.INSTANCE;
         ProxyManager.INSTANCE
            .getThemeDataSource()
            .getItems()
            .add(
               new ThemeConfig(
                  text,
                  instance.getColor2(),
                  instance.getColor3(),
                  instance.getColor4(),
                  instance.getColor5(),
                  instance.getColor6(),
                  instance.getColor7(),
                  instance.getColor8(),
                  instance.getColor9(),
                  instance.getColor10(),
                  instance.getColor11(),
                  instance.getColor12(),
                  instance.getColor13()
               )
            );
         flag = false;
         text = "Enter name";
      }
   }

   public final void updateSymbol(char symbol) {
      for (Object item : items) {
         ColorEditor colorEditor = (ColorEditor)item;
         colorEditor.updateSymbol(symbol);
      }

      if (flag) {
         text = text + symbol;
      }
   }

   public final boolean checkClamp(double amount, double amount2, double amount3) {
      float amount32 = ThemeEditor.amount3;
      if (amount <= ThemeEditor.amount3 + ThemeEditor.amount ? amount32 <= amount : false) {
         amount32 = amount4;
         if (amount2 <= amount4 + ThemeEditor.amount2 ? amount32 <= amount2 : false) {
            amount5 += (float)amount3 * 10.0F;
            amount32 = 0.0F;
            amount32 = 14.0F;

            for (Object item : items) {
               ColorEditor colorEditor = (ColorEditor)item;
               amount32 += colorEditor.getAmount4() + 2.0F;
            }

            if (amount32 == 14.0F) {
               return false;
            }

            amount5 = Math.clamp(amount5, -amount32 + 206, 0.0F);
            return true;
         }
      }

      return false;
   }
}