// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import kotlin.collections.CollectionsKt;
import kotlin.reflect.KMutableProperty0;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.ThemeConfig;
import su.catlean.client.features.ui.panel.ThemeEditor;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class ColorEditor {
   @NotNull
   private String id;
   @NotNull
   private final ThemeConfig theme;
   private float amount;
   private float amount2;
   @NotNull
   private Color setting;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private float amount3;
   private float amount4;
   @NotNull
   private final Animation animation;
   @NotNull
   private final Animation animation2;
   @NotNull
   private final Animation animation3;
   @NotNull
   private final ArrayList<GradientColorPicker> items;

   public ColorEditor(String id, ThemeConfig theme, float amount, float amount2, Color setting) {
      this.id = id;
      this.theme = theme;
      this.amount = amount;
      this.amount2 = amount2;
      this.setting = setting;
      this.amount3 = 116.0F;
      this.amount4 = 14.0F;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.animation3 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.items = new ArrayList();
      byte byteValue = 4;

      for (int i = 0; i < byteValue; i++) {
         this.items.add(new GradientColorPicker((KMutableProperty0)(new ColorEditor$setting$prop(this)), this.amount, this.amount2, i));
      }
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final ThemeConfig getTheme() {
      return this.theme;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   @NotNull
   public final Color getSetting() {
      return this.setting;
   }

   public final void updateColor(Color color) {
      this.setting = color;
   }

   public final float getAmount3() {
      return this.amount3;
   }

   public final void setAmount3(float amount) {
      this.amount3 = amount;
   }

   public final float getAmount4() {
      return this.amount4;
   }

   public final void setAmount4(float amount) {
      this.amount4 = amount;
   }

   public final void render(GuiGraphicsExtractor context, int value, int value2) {
      Matrix3x2fStack pose;
      float amount5;
      float amount22;
      label109: {
         pose = context.pose();
         Fonts.getFontRenderer3()
            .render(
               context,
               LocalizationManager.INSTANCE.computeText("hub.theme-editor." + this.id, new Object[0]),
               this.amount - 1.0F,
               this.amount2 - 1.0F,
               ThemeManager.INSTANCE.getColor7()
            );
         this.amount4 = this.flag ? 65.0F : 14.0F;
         this.animation.applyDoubleValue(this.flag ? 1.0F : 0.0F);
         amount5 = 1.0F - this.animation.getAmount();
         if (this.flag) {
            float amount6 = this.amount;
            float amount7 = this.amount + 25.0F;
            float value3 = value;
            if (amount6 <= value3 ? value3 <= amount7 : false) {
               amount6 = this.amount2 + 24.0F;
               amount7 = this.amount2 + 34.0F;
               value3 = value2;
               if (amount6 <= value3 ? value3 <= amount7 : false) {
                  amount22 = 1.0F;
                  break label109;
               }
            }
         }

         amount22 = 0.0F;
      }

      label101: {
         this.animation2.applyDoubleValue(amount22);
         if (this.flag) {
            float amount8 = this.amount;
            float amount9 = this.amount + 25.0F;
            float value4 = value;
            if (amount8 <= value4 ? value4 <= amount9 : false) {
               amount8 = this.amount2 + 38.0F;
               amount9 = this.amount2 + 47.0F;
               value4 = value2;
               if (amount8 <= value4 ? value4 <= amount9 : false) {
                  amount22 = 1.0F;
                  break label101;
               }
            }
         }

         amount22 = 0.0F;
      }

      this.animation3.applyDoubleValue(amount22);
      float[] values = new float[3];
      Color.RGBtoHSB(this.setting.getRed(), this.setting.getGreen(), this.setting.getBlue(), values);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount + this.amount3 - 18.0F,
         this.amount2 + 11 * this.animation.getAmount(),
         11.0F,
         11.0F * amount5,
         3.0F,
         2.5F,
         1.0F,
         1.0F,
         this.setting,
         ThemeManager.INSTANCE.getColor12(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      if (this.flag) {
         SvgTexture.defaultImpl(
            ThemeEditor.INSTANCE.getSvgTexture2(),
            (Matrix3x2f)context.pose(),
            this.amount + this.amount3 - 17.0,
            this.amount2 - 2.0,
            0.0F,
            0.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), this.animation.getAmount()),
            null,
            0.0F,
            216,
            null
         );
         float amount10 = this.amount + this.amount3 - 48.0F * this.animation.getAmount();
         amount22 = this.amount2 + 10.0F * this.animation.getAmount();
         float amount11 = 40.0F * this.animation.getAmount();
         float amount12 = 39.5F * this.animation.getAmount();
         Color color = new Color(0);
         Color color2 = new Color(-1);
         Color color3 = new Color(0);
         Color color4 = new Color(Color.HSBtoRGB(values[0], 1.0F, 1.0F));
         Object object = null;
         short shortValue = 3072;
         Object object2 = null;
         float value5 = 0.0F;
         Color color5 = color4;
         Color color6 = color3;
         Color color7 = color2;
         Color color8 = color;
         float value6 = 3.0F;
         BoxPass.defaultImpl(amount10, amount22, amount11, amount12, value6, color8, color7, color6, color5, pose, value5, (Color)object2, shortValue, object);
         Iterable<GradientColorPicker> items2 = this.items;
         byte byteValue = 0;

         for (Object object3 : items2) {
            GradientColorPicker gradientColorPicker = (GradientColorPicker)object3;
            gradientColorPicker.setAmount(this.amount);
            gradientColorPicker.setAmount2(this.amount2);
            gradientColorPicker.render(pose, context);
         }

         String text = "%02X%02X%02X";
         Object[] objects = new Object[]{this.setting.getRed(), this.setting.getGreen(), this.setting.getBlue()};
         String text2 = String.format(text, Arrays.copyOf(objects, objects.length));
         byteValue = 3;

         for (int i = 0; i < byteValue; i++) {

            amount11 = this.amount2 + 10.0F + i * 14.0F;
            float value7 = switch (i) {
               case 1 -> this.animation2.getAmount();
               case 2 -> this.animation3.getAmount();
               default -> 0.0F;
            };

            TexturedQuadPass.defaultImpl(pose, this.amount, amount11, 27.0F, 11.0F, 2.0F, 2.0F + value7, 0.5F * switch (i) {
               case 1 -> this.animation2.getAmount();
               case 2 -> this.animation3.getAmount();
               default -> 0.0F;
            }, 1.0F, ThemeManager.INSTANCE.getColor11(), ThemeManager.INSTANCE.getColor9(), 0.0F, 0.0F, 0.0F, 14336, null);
            FontRenderer fontRenderer4 = Fonts.getFontRenderer4();
            String[] texts = new String[]{text2, "Copy", "Paste"};
            fontRenderer4.render2(
               context,
               (String)CollectionsKt.listOf(texts).get(i),
               this.amount + 13.5F,
               this.amount2 + 14.5F + i * 14.0F,
               ThemeManager.INSTANCE.getColor7()
            );
         }

         Render2D instance = Render2D.INSTANCE;
         Matrix3x2fStack pose2 = context.pose();
         amount11 = this.amount + 61.0F;
         amount12 = this.amount2 + 10.0F;
         Color[] colors = new Color[]{
            Render2D.INSTANCE.computeColor(this.setting, 0.0F),
            Render2D.INSTANCE.computeColor(this.setting, 0.0F),
            Render2D.INSTANCE.computeColor(this.setting, 1.0F),
            Render2D.INSTANCE.computeColor(this.setting, 1.0F)
         };
         float value8 = 39.0F;
         float value9 = 2.0F;
         value5 = amount12;
         float value10 = amount11;
         Matrix3x2fStack matrix3x2fStack = pose2;
         instance.render(matrix3x2fStack, value10, value5, value9, value8, colors);

         for (int i2 = 1; i2 < 108; i2++) {
            float amount13 = Math.clamp(1.0F / (107.0F / (i2 * this.animation.getAmount() * this.animation.getAmount())), 0.0F, 1.0F);
            Color hsbColor = Color.getHSBColor(amount13, 1.0F, 1.0F);
            Render2D instance2 = Render2D.INSTANCE;
            Matrix3x2fStack pose3 = context.pose();
            amount11 = this.amount + i2;
            amount12 = this.amount2 + 55.0F;
            Color[] colors2 = new Color[]{hsbColor, hsbColor, hsbColor, hsbColor};
            float value11 = 2.0F;
            float value12 = 1.0F;
            value5 = amount12;
            value10 = amount11;
            Matrix3x2fStack matrix3x2fStack2 = pose3;
            instance2.render(matrix3x2fStack2, value10, value5, value12, value11, colors2);
         }

         this.render2(pose, value);
         this.render(pose, value, value2);
         this.render(pose, value2);
      }

      this.applyEquals(this.theme);
   }

   private final void render(Matrix3x2fStack matrix3x2fStack, int value, int value2) {
      float amount5 = this.amount + this.amount3 - 48.0F + 2.0F;
      float amount22 = this.amount2 + 10.0F + 2.0F;
      float[] values = new float[3];
      Color.RGBtoHSB(this.setting.getRed(), this.setting.getGreen(), this.setting.getBlue(), values);
      TexturedQuadPass.defaultImpl(
         matrix3x2fStack,
         amount5 - 4.0F + 40 * values[1],
         amount22 - 4.0F + 40 * (1.0F - values[2]),
         7.0F,
         7.0F,
         2.5F,
         1.5F,
         1.0F,
         1.0F,
         this.setting,
         Color.BLACK,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      if (this.flag2) {
         float value3 = Math.clamp((value - amount5) / 40.0F, 0.0F, 1.0F);
         float value22 = Math.clamp(1.0F - (value2 - amount22) / 40.0F, 0.0F, 1.0F);
         this.setting = Render2D.INSTANCE.computeColor(new Color(Color.HSBtoRGB(values[0], value3, value22)), this.setting.getAlpha() / 255.0F);
      }
   }

   private final void render(Matrix3x2fStack matrix3x2fStack, int value) {
      float amount22 = this.amount2 + 11.0F;
      TexturedQuadPass.defaultImpl(
         matrix3x2fStack,
         this.amount + 58.0F,
         amount22 - 4.0F + 38 * (1.0F - this.setting.getAlpha() / 255.0F),
         7.0F,
         7.0F,
         2.5F,
         1.0F,
         1.0F,
         1.0F,
         this.setting,
         Render2D.INSTANCE.computeColor(this.setting, 1.0F),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      if (this.flag3) {
         this.setting = Render2D.INSTANCE.computeColor(this.setting, Math.clamp(1.0F - (value - amount22) / 39.0F, 0.0F, 1.0F));
      }
   }

   private final void render2(Matrix3x2fStack matrix3x2fStack, int value) {
      float amount5 = this.amount;
      float[] values = new float[3];
      Color.RGBtoHSB(this.setting.getRed(), this.setting.getGreen(), this.setting.getBlue(), values);
      TexturedQuadPass.defaultImpl(
         matrix3x2fStack,
         amount5 - 3.5F + 106 * values[0],
         this.amount2 + 52.0F,
         7.0F,
         7.0F,
         2.5F,
         1.0F,
         1.0F,
         1.0F,
         new Color(Color.HSBtoRGB(values[0], 1.0F, 1.0F)),
         new Color(Color.HSBtoRGB(values[0], 1.0F, 1.0F)),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      if (this.flag4) {
         float value2 = Math.clamp((value - amount5) / 107.0F, 0.0F, 1.0F);
         this.setting = Render2D.INSTANCE.computeColor(new Color(Color.HSBtoRGB(value2, values[1], values[2])), this.setting.getAlpha() / 255.0F);
      }
   }

   public final void updateAmount(double amount, double amount2, int value) {
      float amount5 = this.amount;
      if (amount <= this.amount + this.amount3 ? amount5 <= amount : false) {
         amount5 = this.amount2;
         if (amount2 <= this.amount2 + 8.0F ? amount5 <= amount2 : false) {
            this.flag = !this.flag;
            this.flag3 = false;
            this.flag4 = false;
            this.flag2 = false;
            return;
         }
      }

      if (this.flag) {
         for (Object item : this.items) {
            GradientColorPicker gradientColorPicker = (GradientColorPicker)item;
            gradientColorPicker.applyGetAlpha((int)amount, (int)amount2);
         }

         if (this.flag) {
            amount5 = this.amount + 57.0F;
            if (amount <= this.amount + 68.0F ? amount5 <= amount : false) {
               amount5 = this.amount2 + 11.0F;
               if (amount2 <= this.amount2 + 50.0F ? amount5 <= amount2 : false) {
                  this.flag3 = true;
                  return;
               }
            }
         }

         if (this.flag) {
            amount5 = this.amount;
            if (amount <= this.amount + 108.0F ? amount5 <= amount : false) {
               amount5 = this.amount2 + 53.0F;
               if (amount2 <= this.amount2 + 58.0F ? amount5 <= amount2 : false) {
                  this.flag4 = true;
                  return;
               }
            }
         }

         if (this.flag) {
            amount5 = this.amount + this.amount3 - 48.0F;
            if (amount <= this.amount + this.amount3 - 8.0F ? amount5 <= amount : false) {
               amount5 = this.amount2 + 11.0F;
               if (amount2 <= this.amount2 + 51.0F ? amount5 <= amount2 : false) {
                  this.flag2 = true;
                  return;
               }
            }
         }

         if (this.flag) {
            amount5 = this.amount;
            if (amount <= this.amount + 25.0F ? amount5 <= amount : false) {
               amount5 = this.amount2 + 24.0F;
               if (amount2 <= this.amount2 + 34.0F ? amount5 <= amount2 : false) {
                  ThemeManager.INSTANCE.updateColor(this.setting);
                  return;
               }
            }
         }

         if (this.flag) {
            amount5 = this.amount;
            if (amount <= this.amount + 25.0F ? amount5 <= amount : false) {
               amount5 = this.amount2 + 38.0F;
               if (amount2 <= this.amount2 + 47.0F ? amount5 <= amount2 : false) {
                  this.setting = ThemeManager.INSTANCE.getColor();
               }
            }
         }
      }
   }

   public final void update() {
      this.flag2 = false;
      this.flag4 = false;
      this.flag3 = false;
   }

   public final void updateValue(int value) {
      if (this.flag) {
         for (Object item : this.items) {
            GradientColorPicker gradientColorPicker = (GradientColorPicker)item;
            gradientColorPicker.applyLength(value);
         }
      }
   }

   public final void updateSymbol(char symbol) {
      if (this.flag) {
         for (Object item : this.items) {
            GradientColorPicker gradientColorPicker = (GradientColorPicker)item;
            gradientColorPicker.applyLength(symbol);
         }
      }
   }

   private final void applyEquals(ThemeConfig themeConfig) {
      String text = this.id;
      switch (text.hashCode()) {
         case -2119261262:
            if (text.equals("sliders")) {
               themeConfig.updateColor8(this.setting);
            }
            break;
         case -1852378237:
            if (text.equals("inactive-module-outline")) {
               themeConfig.updateColor12(this.setting);
            }
            break;
         case -1106245566:
            if (text.equals("outline")) {
               themeConfig.updateColor5(this.setting);
            }
            break;
         case -1068784020:
            if (text.equals("module")) {
               themeConfig.updateColor10(this.setting);
            }
            break;
         case -969751984:
            if (text.equals("widget-blur")) {
               themeConfig.updateColor4(this.setting);
            }
            break;
         case -817598092:
            if (text.equals("secondary")) {
               themeConfig.updateColor2(this.setting);
            }
            break;
         case -525855409:
            if (text.equals("inactive-text")) {
               themeConfig.updateColor7(this.setting);
            }
            break;
         case -314765822:
            if (text.equals("primary")) {
               themeConfig.updateColor(this.setting);
            }
            break;
         case 3027047:
            if (text.equals("blur")) {
               themeConfig.updateColor3(this.setting);
            }
            break;
         case 3556653:
            if (text.equals("text")) {
               themeConfig.updateColor6(this.setting);
            }
            break;
         case 852059617:
            if (text.equals("module-outline")) {
               themeConfig.updateColor11(this.setting);
            }
            break;
         case 1267332622:
            if (text.equals("inactive-module")) {
               themeConfig.updateColor9(this.setting);
            }
      }
   }
}