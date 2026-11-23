// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import kotlin.collections.CollectionsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.ui.panel.ThemeEditor;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class ColorComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   @NotNull
   private final Animation animation;
   @NotNull
   private final Animation animation2;
   @NotNull
   private final Animation animation3;
   @NotNull
   private final ArrayList<ColorPicker> items;

   public ColorComponent(Setting setting) {
      super(setting);
      this.setting = setting;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.animation3 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.items = new ArrayList();
      byte byteValue = 4;

      for (int i = 0; i < byteValue; i++) {
         this.items.add(new ColorPicker(this.getSetting(), this.getAmount3(), this.getAmount4(), i));
      }
   }

   @NotNull
   @Override
   public Setting getSetting() {
      return this.setting;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2, float amount) {
      Matrix3x2fStack pose;
      float amount2;
      float amount4;
      label107: {
         pose = context.pose();
         FontRenderer.defaultImpl(
            Fonts.getFontRenderer3(),
            context.pose(),
            this.getSetting().getText(),
            this.getAmount3(),
            this.getAmount4(),
            ThemeManager.INSTANCE.getColor7(),
            false,
            0,
            96,
            null
         );
         this.setAmount5(this.flag ? 65.0F : 14.0F);
         this.animation.applyDoubleValue(this.flag ? 1.0F : 0.0F);
         amount2 = 1.0F - this.animation.getAmount();
         if (this.flag) {
            float amount3 = this.getAmount3();
            float amount32 = this.getAmount3() + 25.0F;
            float value3 = value;
            if (amount3 <= value3 ? value3 <= amount32 : false) {
               amount3 = this.getAmount4() + 24.0F;
               amount32 = this.getAmount4() + 34.0F;
               value3 = value2;
               if (amount3 <= value3 ? value3 <= amount32 : false) {
                  amount4 = 1.0F;
                  break label107;
               }
            }
         }

         amount4 = 0.0F;
      }

      label99: {
         this.animation2.applyDoubleValue(amount4);
         if (this.flag) {
            float amount33 = this.getAmount3();
            float amount34 = this.getAmount3() + 25.0F;
            float value4 = value;
            if (amount33 <= value4 ? value4 <= amount34 : false) {
               amount33 = this.getAmount4() + 38.0F;
               amount34 = this.getAmount4() + 47.0F;
               value4 = value2;
               if (amount33 <= value4 ? value4 <= amount34 : false) {
                  amount4 = 1.0F;
                  break label99;
               }
            }
         }

         amount4 = 0.0F;
      }

      this.animation3.applyDoubleValue(amount4);
      float[] values = new float[3];
      Color.RGBtoHSB(
         ((Color)this.getSetting().getObject2()).getRed(),
         ((Color)this.getSetting().getObject2()).getGreen(),
         ((Color)this.getSetting().getObject2()).getBlue(),
         values
      );
      TexturedQuadPass.defaultImpl(
         pose,
         this.getAmount3() + this.getAmount5() - 18.0F,
         this.getAmount4() + 11 * this.animation.getAmount(),
         11.0F,
         11.0F * amount2,
         3.0F,
         2.5F,
         1.0F,
         1.0F,
         (Color)this.getSetting().getObject2(),
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
            this.getAmount3() + this.getAmount5() - 17.0,
            (double)this.getAmount4() - 2,
            0.0F,
            0.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), this.animation.getAmount()),
            null,
            0.0F,
            216,
            null
         );
         float amount35 = this.getAmount3() + this.getAmount5() - 48.0F * this.animation.getAmount();
         amount4 = this.getAmount4() + 10.0F * this.animation.getAmount();
         float amount5 = 40.0F * this.animation.getAmount();
         float amount6 = 39.5F * this.animation.getAmount();
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
         BoxPass.defaultImpl(amount35, amount4, amount5, amount6, value6, color8, color7, color6, color5, pose, value5, (Color)object2, shortValue, object);
         Iterable<ColorPicker> items2 = this.items;
         int value7 = 0;

         for (Object object3 : items2) {
            ColorPicker colorPicker = (ColorPicker)object3;
            colorPicker.setAmount(this.getAmount3());
            colorPicker.setAmount2(this.getAmount4());
            colorPicker.render(pose, context);
         }

         String text = "%02X%02X%02X";
         Object[] objects = new Object[]{
            ((Color)this.getSetting().getObject2()).getRed(),
            ((Color)this.getSetting().getObject2()).getGreen(),
            ((Color)this.getSetting().getObject2()).getBlue()
         };
         String text2 = String.format(text, Arrays.copyOf(objects, objects.length));
         value7 = (byte)3;

         for (int i = 0; i < value7; i++) {

            amount4 = this.getAmount3();
            amount5 = this.getAmount4() + 10.0F + i * 14.0F;
            float value8 = switch (i) {
               case 1 -> this.animation2.getAmount();
               case 2 -> this.animation3.getAmount();
               default -> 0.0F;
            };

            TexturedQuadPass.defaultImpl(pose, amount4, amount5, 27.0F, 11.0F, 2.0F, 2.0F + value8, 0.5F * switch (i) {
               case 1 -> this.animation2.getAmount();
               case 2 -> this.animation3.getAmount();
               default -> 0.0F;
            }, 1.0F, ThemeManager.INSTANCE.getColor11(), ThemeManager.INSTANCE.getColor9(), 0.0F, 0.0F, 0.0F, 14336, null);
            FontRenderer fontRenderer4 = Fonts.getFontRenderer4();
            String[] texts = new String[]{text2, "Copy", "Paste"};
            fontRenderer4.render2(
               context,
               (String)CollectionsKt.listOf(texts).get(i),
               this.getAmount3() + 13.5F,
               this.getAmount4() + 14.5F + i * 14.0F,
               ThemeManager.INSTANCE.getColor7()
            );
         }

         Render2D instance = Render2D.INSTANCE;
         Matrix3x2fStack pose2 = context.pose();
         amount5 = this.getAmount3() + 61.0F;
         amount6 = this.getAmount4() + 10.0F;
         Color[] colors = new Color[]{
            Render2D.INSTANCE.computeColor((Color)this.getSetting().getObject2(), 0.0F),
            Render2D.INSTANCE.computeColor((Color)this.getSetting().getObject2(), 0.0F),
            Render2D.INSTANCE.computeColor((Color)this.getSetting().getObject2(), 1.0F),
            Render2D.INSTANCE.computeColor((Color)this.getSetting().getObject2(), 1.0F)
         };
         float value9 = 39.0F;
         float value10 = 2.0F;
         value5 = amount6;
         float value11 = amount5;
         Matrix3x2fStack matrix3x2fStack = pose2;
         instance.render(matrix3x2fStack, value11, value5, value10, value9, colors);
         value7 = 1;

         do {
            int value12 = value7++;
            float amount7 = Math.clamp(1.0F / (107.0F / (value12 * this.animation.getAmount() * this.animation.getAmount())), 0.0F, 1.0F);
            Color hsbColor = Color.getHSBColor(amount7, 1.0F, 1.0F);
            Render2D instance2 = Render2D.INSTANCE;
            Matrix3x2fStack pose3 = context.pose();
            amount5 = this.getAmount3() + value12;
            amount6 = this.getAmount4() + 55.0F;
            Color[] colors2 = new Color[]{hsbColor, hsbColor, hsbColor, hsbColor};
            float value13 = 2.0F;
            float value14 = 1.0F;
            value5 = amount6;
            value11 = amount5;
            Matrix3x2fStack matrix3x2fStack2 = pose3;
            instance2.render(matrix3x2fStack2, value11, value5, value14, value13, colors2);
         } while (value7 <= 107);

         this.render2(pose, value);
         this.render(pose, value, value2);
         this.render(pose, value2);
      }
   }

   private final void render(Matrix3x2fStack matrix3x2fStack, int value, int value2) {
      float amount3 = this.getAmount3() + this.getAmount5() - 48.0F + 2.0F;
      float amount4 = this.getAmount4() + 10.0F + 2.0F;
      float[] values = new float[3];
      Color.RGBtoHSB(
         ((Color)this.getSetting().getObject2()).getRed(),
         ((Color)this.getSetting().getObject2()).getGreen(),
         ((Color)this.getSetting().getObject2()).getBlue(),
         values
      );
      TexturedQuadPass.defaultImpl(
         matrix3x2fStack,
         amount3 - 4.0F + 40 * values[1],
         amount4 - 4.0F + 40 * (1.0F - values[2]),
         7.0F,
         7.0F,
         2.5F,
         1.5F,
         1.0F,
         1.0F,
         (Color)this.getSetting().getObject2(),
         Color.BLACK,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      if (this.flag2) {
         float value3 = Math.clamp((value - amount3) / 40.0F, 0.0F, 1.0F);
         float value22 = Math.clamp(1.0F - (value2 - amount4) / 40.0F, 0.0F, 1.0F);
         this.getSetting()
            .setObject2(
               Render2D.INSTANCE.computeColor(new Color(Color.HSBtoRGB(values[0], value3, value22)), ((Color)this.getSetting().getObject2()).getAlpha() / 255.0F)
            );
      }
   }

   private final void render(Matrix3x2fStack matrix3x2fStack, int value) {
      float amount4 = this.getAmount4() + 11.0F;
      TexturedQuadPass.defaultImpl(
         matrix3x2fStack,
         this.getAmount3() + 58.0F,
         amount4 - 4.0F + 38 * (1.0F - ((Color)this.getSetting().getObject2()).getAlpha() / 255.0F),
         7.0F,
         7.0F,
         2.5F,
         1.0F,
         1.0F,
         1.0F,
         (Color)this.getSetting().getObject2(),
         Render2D.INSTANCE.computeColor((Color)this.getSetting().getObject2(), 1.0F),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      if (this.flag3) {
         float value2 = Math.clamp(1.0F - (value - amount4) / 39.0F, 0.0F, 1.0F);
         this.getSetting().setObject2(Render2D.INSTANCE.computeColor((Color)this.getSetting().getObject2(), value2));
      }
   }

   private final void render2(Matrix3x2fStack matrix3x2fStack, int value) {
      float amount3 = this.getAmount3();
      float[] values = new float[3];
      Color.RGBtoHSB(
         ((Color)this.getSetting().getObject2()).getRed(),
         ((Color)this.getSetting().getObject2()).getGreen(),
         ((Color)this.getSetting().getObject2()).getBlue(),
         values
      );
      TexturedQuadPass.defaultImpl(
         matrix3x2fStack,
         amount3 - 3.5F + 106 * values[0],
         this.getAmount4() + 52.0F,
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
         float value2 = Math.clamp((value - amount3) / 107.0F, 0.0F, 1.0F);
         this.getSetting()
            .setObject2(
               Render2D.INSTANCE
                  .computeColor(new Color(Color.HSBtoRGB(value2, values[1], values[2])), ((Color)this.getSetting().getObject2()).getAlpha() / 255.0F)
            );
      }
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      for (Object item : this.items) {
         ColorPicker colorPicker = (ColorPicker)item;
         colorPicker.applyGetAlpha((int)amount, (int)amount2);
      }

      float amount3 = this.getAmount3();
      if (amount <= this.getAmount3() + this.getAmount5() ? amount3 <= amount : false) {
         amount3 = this.getAmount4();
         if (amount2 <= this.getAmount4() + 8.0F ? amount3 <= amount2 : false) {
            this.flag = !this.flag;
            this.flag3 = false;
            this.flag4 = false;
            this.flag2 = false;
            return;
         }
      }

      if (this.flag) {
         amount3 = this.getAmount3() + 57.0F;
         if (amount <= this.getAmount3() + 68.0F ? amount3 <= amount : false) {
            amount3 = this.getAmount4() + 11.0F;
            if (amount2 <= this.getAmount4() + 50.0F ? amount3 <= amount2 : false) {
               this.flag3 = true;
               return;
            }
         }
      }

      if (this.flag) {
         amount3 = this.getAmount3();
         if (amount <= this.getAmount3() + 108.0F ? amount3 <= amount : false) {
            amount3 = this.getAmount4() + 53.0F;
            if (amount2 <= this.getAmount4() + 58.0F ? amount3 <= amount2 : false) {
               this.flag4 = true;
               return;
            }
         }
      }

      if (this.flag) {
         amount3 = this.getAmount3() + this.getAmount5() - 48.0F;
         if (amount <= this.getAmount3() + this.getAmount5() - 8.0F ? amount3 <= amount : false) {
            amount3 = this.getAmount4() + 11.0F;
            if (amount2 <= this.getAmount4() + 51.0F ? amount3 <= amount2 : false) {
               this.flag2 = true;
               return;
            }
         }
      }

      if (this.flag) {
         amount3 = this.getAmount3();
         if (amount <= this.getAmount3() + 25.0F ? amount3 <= amount : false) {
            amount3 = this.getAmount4() + 24.0F;
            if (amount2 <= this.getAmount4() + 34.0F ? amount3 <= amount2 : false) {
               ThemeManager.INSTANCE.updateColor((Color)this.getSetting().getObject2());
               return;
            }
         }
      }

      if (this.flag) {
         amount3 = this.getAmount3();
         if (amount <= this.getAmount3() + 25.0F ? amount3 <= amount : false) {
            amount3 = this.getAmount4() + 38.0F;
            if (amount2 <= this.getAmount4() + 47.0F ? amount3 <= amount2 : false) {
               this.getSetting().setObject2(ThemeManager.INSTANCE.getColor());
            }
         }
      }
   }

   @Override
   public void updateAmount2(double amount, double amount2, int value) {
      this.flag2 = false;
      this.flag4 = false;
      this.flag3 = false;
   }

   @Override
   public void updateValue(int value) {
      for (Object item : this.items) {
         ColorPicker colorPicker = (ColorPicker)item;
         colorPicker.applyLength(value);
      }
   }

   @Override
   public void updateSymbol(char symbol) {
      for (Object item : this.items) {
         ColorPicker colorPicker = (ColorPicker)item;
         colorPicker.applyLength(symbol);
      }
   }

   @Override
   public boolean testAmount(double amount, double amount2, double amount3) {
      if (!this.flag) {
         return false;
      } else {
         boolean flag5 = false;

         for (Object item : this.items) {
            ColorPicker colorPicker = (ColorPicker)item;
            if (colorPicker.checkIntValue(amount, amount2, amount3)) {
               flag5 = true;
            }
         }

         return flag5;
      }
   }

   @Override
   public void update() {
      this.flag2 = false;
      this.flag4 = false;
      this.flag3 = false;
   }
}