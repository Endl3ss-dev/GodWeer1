// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import su.catlean.utils.KotlinCompat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.data.Rectangle;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class EnumComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   private boolean flag;
   @NotNull
   private final Animation animation;
   @NotNull
   private List<EnumComponent.Selector> items;
   private float amount;
   private boolean flag2;

   public EnumComponent(Setting setting) {
      super(setting);
      this.setting = setting;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.items = new ArrayList();
      if (this.flag2) {
         Object[] enumConstants = this.getSetting().getObject2().getClass().getEnumConstants();
         int value = 0;

         for (int i = enumConstants.length; value < i; value++) {
            Object object = enumConstants[value];
            Enum enumValue = (Enum)object;
            this.items.add(new EnumComponent.Selector(1.0F, this.amount, this.getAmount5() - 11.0F, enumValue, null, 16, null));
            this.amount += 15.0F;
         }
      } else {
         float value2 = 0.0F;
         value2 = 2.0F;
         Object[] enumConstants2 = this.getSetting().getObject2().getClass().getEnumConstants();
         boolean flag4 = false;
         Iterable sortedWith = ArraysKt.sortedWith(enumConstants2, new EnumComponent.Ordering());
         flag4 = false;

         for (Object object2 : sortedWith) {
            Enum enumValue2 = (Enum)object2;
            String name = enumValue2.name();
            float amount2 = Fonts.getFontRenderer4().computeAmount(name) + 4.0F;
            if (value2 + amount2 > this.getAmount5() - 10.0F) {
               this.amount += 12.0F;
               value2 = 2.0F;
            }

            this.items.add(new EnumComponent.Selector(value2, this.amount, amount2, enumValue2, null, 16, null));
            value2 += amount2 + 2.0F;
         }
      }
   }

   @NotNull
   @Override
   public Setting getSetting() {
      return this.setting;
   }

   @NotNull
   public final List<EnumComponent.Selector> getItems() {
      return this.items;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2, float amount) {
      this.setAmount5(this.flag2 ? (this.flag ? this.amount + 15.0F : 14.0F) : (this.flag ? this.amount + 28.0F : 14.0F));
      this.animation.applyDoubleValue(this.flag ? 1.0F : 0.0F);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount3(),
         this.getAmount4() - 1.0F,
         this.getAmount5() - 7.0F,
         this.animation.getAmount() * (this.getAmount6() - 14.0F) + 14.0F - 1.0F,
         3.0F,
         2.5F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor11(),
         ThemeManager.INSTANCE.getColor12(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Render2D instance = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(
         this.getAmount3(),
         this.getAmount4() - 1.0F,
         this.getAmount3() + this.getAmount5() - 7.0F,
         this.getAmount4() - 1.0F + (this.animation.getAmount() * (this.getAmount6() - 14.0F) + 14.0F - 1.0F)
      );
      instance.render(context, rectangle);
      float amount2 = Fonts.getFontRenderer3().computeAmount(this.getSetting().getText()) / 2.0F;
      float amount5 = ((this.getAmount5() - 16.0F) / 2.0F - amount2) * this.animation.getAmount();
      FontRenderer.defaultImpl(
         Fonts.getFontRenderer3(),
         context.pose(),
         this.getSetting().getText(),
         this.getAmount3() + 3.0F + amount5,
         this.getAmount4(),
         ThemeManager.INSTANCE.getColor7(),
         false,
         0,
         96,
         null
      );
      CharSequence lowerCase = ((Enum)this.getSetting().getObject2()).name().toLowerCase(Locale.ROOT);
      String[] texts = new String[]{"_"};
      String text2 = StringsKt.trim(
            KotlinCompat.joinToString(KotlinCompat.split(lowerCase, texts, false, 0, 6, null), " ", null, null, 0, null, arg -> {
               String text;
               if (((String)arg).length() > 0) {
                  StringBuilder stringBuilder = new StringBuilder();
                  char charAt = ((String)arg).charAt(0);
                  StringBuilder stringBuilder2 = stringBuilder;
                  byte byteValue = 0;
                  StringBuilder upperCase = stringBuilder2.append(String.valueOf(charAt).toUpperCase(Locale.ROOT));
                  byteValue = 1;
                  text = upperCase.append(((String)arg).substring(byteValue)).toString();
               } else {
                  text = (String)arg;
               }

               return text;
            }, 30, null)
         )
         .toString();
      float amount3 = Fonts.getFontRenderer3().computeAmount(text2);
      FontRenderer fontRenderer3 = Fonts.getFontRenderer3();
      Matrix3x2fStack pose = context.pose();
      float amount32 = this.getAmount3() + this.getAmount5() - 11.0F - amount3 + amount5;
      float amount4 = this.getAmount4();
      Color color = Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F - this.animation.getAmount());
      rectangle = null;
      byte byteValue2 = 96;
      byte byteValue3 = 0;
      boolean flag3 = false;
      Color color2 = color;
      float value3 = amount4;
      float value4 = amount32;
      Matrix3x2fStack matrix3x2fStack = pose;
      FontRenderer.defaultImpl2(fontRenderer3, matrix3x2fStack, text2, value4, value3, color2, flag3, byteValue3, byteValue2, rectangle);
      if (this.flag) {
         for (Object item : this.items) {
            EnumComponent.Selector selector = (EnumComponent.Selector)item;
            selector.getAnimation().applyDoubleValue(Intrinsics.areEqual(selector.getValue(), this.getSetting().getObject2()) ? 1.0F : 0.0F);
            Color brighter = Render2D.INSTANCE
               .fetchAlpha(ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor11(), selector.getAnimation().getAmount())
               .brighter();
            Color brighter2 = Render2D.INSTANCE
               .fetchAlpha(ThemeManager.INSTANCE.getColor13(), ThemeManager.INSTANCE.getColor12(), selector.getAnimation().getAmount())
               .brighter();
            if (this.flag2) {
               TexturedQuadPass.defaultImpl(
                  context.pose(),
                  this.getAmount3() + selector.getAmount() + 1.0F,
                  this.getAmount4() + selector.getAmount2() + 13.0F,
                  selector.getAmount3(),
                  13.0F,
                  2.0F,
                  2.5F,
                  0.8F,
                  1.0F,
                  brighter,
                  brighter2,
                  0.0F,
                  0.0F,
                  0.0F,
                  14336,
                  null
               );
               FontRenderer fontRenderer32 = Fonts.getFontRenderer3();
               CharSequence lowerCase2 = selector.getValue().name().toLowerCase(Locale.ROOT);
               String[] texts2 = new String[]{"_"};
               fontRenderer32.render2(
                  context,
                  StringsKt.trim(
                        KotlinCompat.joinToString(KotlinCompat.split(lowerCase2, texts2, false, 0, 6, null), " ", null, null, 0, null, arg2 -> {
                           String text3;
                           if (((String)arg2).length() > 0) {
                              StringBuilder stringBuilder3 = new StringBuilder();
                              char charAt2 = ((String)arg2).charAt(0);
                              StringBuilder stringBuilder4 = stringBuilder3;
                              byte byteValue4 = 0;
                              StringBuilder upperCase2 = stringBuilder4.append(String.valueOf(charAt2).toUpperCase(Locale.ROOT));
                              byteValue4 = 1;
                              text3 = upperCase2.append(((String)arg2).substring(byteValue4)).toString();
                           } else {
                              text3 = (String)arg2;
                           }

                           return text3;
                        }, 30, null)
                     )
                     .toString(),
                  this.getAmount3() + selector.getAmount() + 1.0F + selector.getAmount3() / 2.0F,
                  this.getAmount4() + selector.getAmount2() + 18.0F,
                  ThemeManager.INSTANCE.getColor7()
               );
            } else {
               TexturedQuadPass.defaultImpl(
                  context.pose(),
                  this.getAmount3() + selector.getAmount() + 1.0F,
                  this.getAmount4() + selector.getAmount2() + 13.0F,
                  selector.getAmount3(),
                  10.0F,
                  2.0F,
                  2.5F,
                  0.8F,
                  1.0F,
                  brighter,
                  brighter2,
                  0.0F,
                  0.0F,
                  0.0F,
                  14336,
                  null
               );
               FontRenderer fontRenderer4 = Fonts.getFontRenderer4();
               CharSequence lowerCase3 = selector.getValue().name().toLowerCase(Locale.ROOT);
               String[] texts3 = new String[]{"_"};
               fontRenderer4.render2(
                  context,
                  StringsKt.trim(
                        KotlinCompat.joinToString(KotlinCompat.split(lowerCase3, texts3, false, 0, 6, null), " ", null, null, 0, null, arg3 -> {
                           String text4;
                           if (((String)arg3).length() > 0) {
                              StringBuilder stringBuilder5 = new StringBuilder();
                              char charAt3 = ((String)arg3).charAt(0);
                              StringBuilder stringBuilder6 = stringBuilder5;
                              byte byteValue5 = 0;
                              StringBuilder upperCase3 = stringBuilder6.append(String.valueOf(charAt3).toUpperCase(Locale.ROOT));
                              byteValue5 = 1;
                              text4 = upperCase3.append(((String)arg3).substring(byteValue5)).toString();
                           } else {
                              text4 = (String)arg3;
                           }

                           return text4;
                        }, 30, null)
                     )
                     .toString(),
                  this.getAmount3() + selector.getAmount() + 1.0F + selector.getAmount3() / 2.0F,
                  this.getAmount4() - 1.0F + selector.getAmount2() + 18.0F,
                  ThemeManager.INSTANCE.getColor7()
               );
            }

            float amount33 = this.getAmount3() + selector.getAmount() + 1.0F;
            float amount34 = this.getAmount3() + selector.getAmount() + 1.0F + selector.getAmount3();
            float value5 = value;
            if (amount33 <= value5 ? value5 <= amount34 : false) {
               amount33 = this.getAmount4() - 1.0F + selector.getAmount2() + 14.0F;
               amount34 = this.getAmount4() + selector.getAmount2() + 23.0F;
               value5 = value2;
               if ((amount33 <= value5 ? value5 <= amount34 : false) && GLFW.glfwGetPlatform() != 393219) {
                  GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221188));
                  ClickGuiScreen.INSTANCE.setFlag(false);
               }
            }
         }
      }

      Render2D.INSTANCE.render(context);
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      float amount3 = this.getAmount3();
      if (amount <= this.getAmount3() + this.getAmount5() - 7.0F ? amount3 <= amount : false) {
         amount3 = this.getAmount4();
         if (amount2 <= this.getAmount4() + 12 ? amount3 <= amount2 : false) {
            this.flag = !this.flag;
         }
      }

      if (this.flag) {
         for (Object item : this.items) {
            EnumComponent.Selector selector = (EnumComponent.Selector)item;
            float amount32 = this.getAmount3() + selector.getAmount() + 1.0F;
            if (amount <= this.getAmount3() + selector.getAmount() + 1.0F + selector.getAmount3() ? amount32 <= amount : false) {
               amount32 = this.getAmount4() - 1.0F + selector.getAmount2() + 14.0F;
               if (amount2 <= this.getAmount4() + selector.getAmount2() + 23.0F ? amount32 <= amount2 : false) {
                  this.getSetting().setObject2(selector.getValue());
               }
            }
         }
      }
   }

   @Override
   public void updateAmount2(double amount, double amount2, int value) {
   }

   @Override
   public void updateValue(int value) {
   }

   @Override
   public void updateSymbol(char symbol) {
   }

   @Override
   public boolean testAmount(double amount, double amount2, double amount3) {
      return false;
   }

   @Override
   public void update() {
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         Enum object3 = (Enum)object;
         Comparable length = object3.name().length();
         object3 = (Enum)object2;
         Comparable comparable = length;
         return ComparisonsKt.compareValues(comparable, object3.name().length());
      }
   }

   public static final class Selector {
      private final float amount;
      private final float amount2;
      private final float amount3;
      @NotNull
      private final Enum value;
      @NotNull
      private final Animation animation;

      public Selector(float amount, float amount2, float amount3, Enum value, Animation animation) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.value = value;
         this.animation = animation;
      }

      public Selector(
         float amount, float amount2, float amount3, Enum enumValue, Animation animation, int value, DefaultConstructorMarker defaultConstructorMarker
      ) {
         if ((value & 16) != 0) {
            animation = new Animation(NotificationManager.Notification.Type.OUT_EXPO, 600L);
         }

         this(amount, amount2, amount3, enumValue, animation);
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

      @NotNull
      public final Enum getValue() {
         return this.value;
      }

      @NotNull
      public final Animation getAnimation() {
         return this.animation;
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
      public final Enum getValue2() {
         return this.value;
      }

      @NotNull
      public final Animation getAnimation2() {
         return this.animation;
      }

      @NotNull
      @Override
      public String toString() {
         Animation animation2 = this.animation;
         Enum value2 = this.value;
         float amount32 = this.amount3;
         float amount22 = this.amount2;
         float amount4 = this.amount;
         return "Selector(x=" + amount4 + ", y=" + amount22 + ", w=" + amount32 + ", value=" + value2 + ", animation=" + animation2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + Float.hashCode(this.amount3);
         hashCode = hashCode * 31 + this.value.hashCode();
         return hashCode * 31 + this.animation.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof EnumComponent.Selector selector)) {
            return false;
         } else if (Float.compare(this.amount, selector.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, selector.amount2) != 0) {
            return false;
         } else if (Float.compare(this.amount3, selector.amount3) != 0) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.value, selector.value) ? false : Intrinsics.areEqual(this.animation, selector.animation);
         }
      }
   }
}