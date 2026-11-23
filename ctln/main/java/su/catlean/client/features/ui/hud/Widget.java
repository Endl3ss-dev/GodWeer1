// deobf by pusia
package su.catlean.client.features.ui.hud;

import su.catlean.utils.KotlinCompat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.data.DividerRenderState;
import su.catlean.api.data.IconRenderState;
import su.catlean.api.data.ScreenPosition;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.setting.Keybindable;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.SettingHolder;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ScreenPositionSetting;
import su.catlean.client.features.ui.hud.widgets.CoordWidget;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.WidgetManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.client.features.ui.hud.Widget;

public abstract class Widget implements SettingHolder, Keybindable {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Widget.class, "enabled", "getEnabled()Z", 0), new MutablePropertyReference1Impl(Widget.class, "pos", "getPos()Lsu/catlean/i4;", 0) };
   @NotNull
   private final String id;
   @NotNull
   private final List items;
   @NotNull
   private final List items2;
   @NotNull
   private final SvgTexture svgTexture;
   @NotNull
   private final BooleanSetting enabled;
   @NotNull
   private final ScreenPositionSetting position;
   @NotNull
   private SvgTexture svgTexture2;
   private boolean flag;
   private float amount;
   private float amount2;
   private float amount3;
   private float amount4;
   private boolean flag2;
   private float amount5;
   private float amount6;
   @NotNull
   private final String text;

   public Widget(String id) {
      this.id = id;
      this.items = new ArrayList();
      this.items2 = new ArrayList();
      this.svgTexture = new SvgTexture("check", 12, 12);
      this.enabled = SettingFactory.defaultImpl(this, "enabled", false, null, null, 12, null);
      this.position = SettingFactory.defaultImpl(this, "position", new ScreenPosition(0.5F, 0.5F), null, null, 12, null);
      this.svgTexture2 = new SvgTexture(this.id, 10, 10);
      StringBuilder stringBuilder = new StringBuilder();
      StringBuilder stringBuilder2 = stringBuilder;
      CharSequence charSequence = this.id;
      String[] texts = new String[]{"-"};

      for (Object splitdefaultItem : KotlinCompat.split(charSequence, texts, false, 0, 6, null)) {
         String text2 = (String)splitdefaultItem;
         StringBuilder stringBuilder3 = stringBuilder2;
         String text3;
         if (text2.length() > 0) {
            StringBuilder stringBuilder4 = new StringBuilder();
            char charAt = text2.charAt(0);
            StringBuilder stringBuilder5 = stringBuilder4;
            byte byteValue = 0;
            CharSequence upperCase = String.valueOf(charAt).toUpperCase(Locale.ROOT);
            stringBuilder3 = stringBuilder2;
            StringBuilder stringBuilder6 = stringBuilder5.append((Object)upperCase);
            byteValue = 1;
            text3 = stringBuilder6.append(text2.substring(byteValue)).toString();
         } else {
            text3 = text2;
         }

         stringBuilder3.append(text3);
         stringBuilder2.append(' ');
         StringsKt.trim(stringBuilder2);
      }

      this.text = stringBuilder.toString();
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   @Override
   public List getItems5() {
      return this.items;
   }

   @NotNull
   @Override
   public List getItems6() {
      return this.items2;
   }

   public final boolean getEnabled() {
      return (Boolean)this.enabled.computeObject(this, $$delegatedProperties[0]);
   }

   public final void updateFlag(boolean flag) {
      this.enabled.updateObject(this, $$delegatedProperties[0], flag);
   }

   @NotNull
   public final ScreenPosition getPos() {
      return (ScreenPosition)this.position.computeObject(this, $$delegatedProperties[1]);
   }

   @NotNull
   public final SvgTexture getSvgTexture2() {
      return this.svgTexture2;
   }

   public final float getAmount5() {
      return this.amount5;
   }

   public final void setAmount5(float amount) {
      this.amount5 = amount;
   }

   public final float getAmount6() {
      return this.amount6;
   }

   public final void setAmount6(float amount) {
      this.amount6 = amount;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Override
   public void update5() {
      if (this.getEnabled()) {
         this.applyUnplug();
      } else {
         this.applyPlug();
      }
   }

   @Override
   public void applyPlug() {
      this.updateFlag(true);
   }

   @Override
   public void applyUnplug() {
      this.updateFlag(false);
   }

   public abstract void render(GuiGraphicsExtractor guiGraphicsExtractor);

   public void render2(GuiGraphicsExtractor context) {
   }

   public void update() {
   }

   public final void render4(GuiGraphicsExtractor context) {
      this.render(context);

      for (Widget item : (Iterable<Widget>)WidgetManager.INSTANCE.getItems()) {
         if (!Intrinsics.areEqual(item, this) && item.getEnabled()) {
            float amount32 = item.getAmount3();
            float amount62 = item.amount6 + item.getAmount3();
            float amount33 = this.getAmount3();
            if (amount32 <= amount33 ? amount33 <= amount62 : false) {
               amount32 = item.getAmount4();
               amount62 = item.getAmount4() + item.amount5;
               amount33 = this.getAmount4();
               if (amount32 <= amount33 ? amount33 <= amount62 : false) {
                  amount32 = (item.getAmount3() + item.amount6 + 2.0F) / this.getValue2();
                  amount62 = (item.getAmount4() + item.amount5 + 2.0F) / this.getValue();
                  amount33 = Math.min(5.0F / this.getValue(), 1.0F - this.amount5 / this.getValue());
                  float value = Math.max(5.0F / this.getValue(), 1.0F - this.amount5 / this.getValue());
                  if (item.getAmount3() != this.getAmount3() && item.getAmount4() == this.getAmount4()) {
                     this.getPos().setAmount(RangesKt.coerceIn(amount32, 5.0F / this.getValue2(), 1.0F));
                  }

                  if (item.getAmount4() != this.getAmount4()
                     && (item.getAmount3() == this.getAmount3() || item.getAmount3() + item.amount6 == this.getAmount3() + this.amount6)) {
                     this.getPos().setAmount2(RangesKt.coerceIn(amount62, amount33, value));
                  }
               }
            }

            if (item.getAmount4() == this.getAmount4() && Math.abs(item.getAmount3() + item.amount6 - this.getAmount3()) < 10.0F) {
               amount32 = (item.getAmount3() + item.amount6 + 2.0F) / this.getValue2();
               this.getPos().setAmount(RangesKt.coerceIn(amount32, 5.0F / this.getValue2(), 1.0F));
            }

            if ((item.getAmount3() == this.getAmount3() || item.getAmount3() + item.amount6 == this.getAmount3() + this.amount6)
               && Math.abs(item.getAmount4() + item.amount5 - this.getAmount4()) < 10.0F) {
               amount32 = (item.getAmount4() + item.amount5 + 2.0F) / this.getValue();
               amount62 = Math.min(5.0F / this.getValue(), 1.0F - this.amount5 / this.getValue());
               amount33 = Math.max(5.0F / this.getValue(), 1.0F - this.amount5 / this.getValue());
               this.getPos().setAmount2(RangesKt.coerceIn(amount32, amount62, amount33));
            }
         }
      }

      if (this.getAmount3() + this.amount6 > MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() - 5.0F
         || Math.abs(this.getAmount3() + this.amount6 - MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() - 5.0F) < 60.0F) {
         float guiScaledWidth = (MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() - this.amount6 - 5.0F) / this.getValue2();
         this.getPos().setAmount(RangesKt.coerceIn(guiScaledWidth, 5.0F / this.getValue2(), 1.0F));
      }

      if (MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen || MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen) {
         if (this.flag) {
            float amount7 = (this.getAmount7() - this.amount) / this.getValue2();
            float amount8 = (this.getAmount8() - this.amount2) / this.getValue();
            this.getPos().setAmount(RangesKt.coerceIn(amount7, 5.0F / this.getValue2(), 1.0F - (this.amount6 + 5.0F) / this.getValue2()));
            float value2 = Math.min(5.0F / this.getValue(), 1.0F - this.amount5 / this.getValue());
            float value3 = Math.max(5.0F / this.getValue(), 1.0F - this.amount5 / this.getValue());
            this.getPos().setAmount2(RangesKt.coerceIn(amount8, value2, value3));
            float amount34 = 0.0F;
            float amount35 = 0.0F;
            float amount42 = 0.0F;

            for (Widget item2 : (Iterable<Widget>)WidgetManager.INSTANCE.getItems()) {
               if (!Intrinsics.areEqual(item2, this) && item2.getEnabled()) {
                  if (this.checkAbs(item2.getAmount3() + item2.amount6, this.getAmount3() + this.amount6)) {
                     amount34 = item2.getAmount3() + item2.amount6 - this.amount6;
                     amount35 = item2.getAmount3() + item2.amount6;
                  } else if (this.checkAbs(item2.getAmount3(), this.getAmount3())) {
                     amount34 = item2.getAmount3();
                     amount35 = amount34;
                  }

                  if (this.checkAbs(item2.getAmount4(), this.getAmount4())) {
                     amount42 = item2.getAmount4();
                  }

                  if (amount35 != 0.0F) {
                     Render2D.defaultImpl(Render2D.INSTANCE, context.pose(), amount35, 5.0F, amount35, this.getValue() - 5.0F, Color.WHITE, null, 64, null);
                     this.getPos().setAmount(amount34 / this.getValue2());
                  }

                  if (amount42 != 0.0F) {
                     Render2D.defaultImpl(Render2D.INSTANCE, context.pose(), 5.0F, amount42, this.getValue2() - 5.0F, amount42, Color.WHITE, null, 64, null);
                     this.getPos().setAmount2(amount42 / this.getValue());
                  }
               }
            }
         }

         if (this.flag2) {
            float value4 = 0.0F;
            Iterable items5 = this.getItems5();
            var arrayList = new ArrayList();

            for (Object object : items5) {
               Setting setting = (Setting)object;
               if (!Intrinsics.areEqual(setting.getId(), "enabled") && !Intrinsics.areEqual(setting.getId(), "position")) {
                  arrayList.add(object);
               }
            }

            List list = arrayList;
            Iterator iterator = list.iterator();
            Float floatValue;
            if (!iterator.hasNext()) {
               floatValue = null;
            } else {
               Setting setting2 = (Setting)iterator.next();
               float amount9 = Fonts.getFontRenderer3().computeAmount(setting2.getText());

               while (iterator.hasNext()) {
                  Setting setting3 = (Setting)iterator.next();
                  float amount10 = Fonts.getFontRenderer3().computeAmount(setting3.getText());
                  amount9 = Math.max(amount9, amount10);
               }

               floatValue = amount9;
            }

            float value5 = (floatValue != null ? floatValue : 0.0F) + 23.0F;
            TexturedQuadPass.defaultImpl(
               context.pose(),
               this.amount3,
               this.amount4,
               value5,
               14.0F * list.size() + 6.0F,
               3.0F,
               1.5F,
               0.0F,
               1.0F,
               ThemeManager.INSTANCE.getColor5(),
               ThemeManager.INSTANCE.getColor6(),
               0.0F,
               0.0F,
               0.0F,
               14336,
               null
            );
            Iterable iterable = list;

            for (Object object2 : iterable) {
               Setting setting4 = (Setting)object2;
               Fonts.getFontRenderer3().render(context, setting4.getText(), this.amount3 + 3.0F, this.amount4 + value4 + 3.0F, ThemeManager.INSTANCE.getColor7());
               TexturedQuadPass.defaultImpl(
                  context.pose(),
                  this.amount3 + value5 - 14.0F,
                  this.amount4 + 3.5F + value4,
                  11.0F,
                  11.0F,
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
               if (Intrinsics.areEqual(setting4.getObject2(), true)) {
                  SvgTexture.defaultImpl(
                     this.svgTexture,
                     (Matrix3x2f)context.pose(),
                     this.amount3 + value5 - 14.5,
                     this.amount4 + value4 + 2.0,
                     0.0F,
                     0.0F,
                     ThemeManager.INSTANCE.getColor2(),
                     null,
                     0.0F,
                     216,
                     null
                  );
               }

               value4 += 14.0F;
            }
         }
      }

      if (this.isFlag2() && MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen
         || MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen) {
         this.applyGetWindow3();
         WidgetManager.INSTANCE.setWidget2(this);
      }
   }

   public final void render(GuiGraphicsExtractor context, String text) {
      float amount7 = Fonts.getFontRenderer().computeAmount(text);
      defaultImpl(this, context.pose(), this.getAmount3(), this.getAmount4(), amount7 + 19.0F, 13.0F, 0.0F, false, 96, null);
      defaultImpl(this, context.pose(), this.getAmount3() + 14, this.getAmount4() + 3.0F, 0.0F, 8, null);
      defaultImpl(this, this.svgTexture2, context.pose(), this.getAmount3() + 2.0F, this.getAmount4(), 10.0F, 10.0F, 0.0F, 64, null);
      this.render(context, text, this.getAmount3() + 16.0F, this.getAmount4() + 1.0F, ThemeManager.INSTANCE.getColor7());
      this.amount6 = amount7 + 20.0F;
      this.amount5 = 13.0F;
   }

   public final void render(GuiGraphicsExtractor context, String header, float amount, boolean flag) {
      float amount7 = Fonts.getFontRenderer().computeAmount(header);
      float value = amount7 + 24.0F;
      float amount32 = this.getAmount3() + (flag ? this.amount6 - value : 0.0F);
      context.pose().pushMatrix();
      context.pose().translate(amount32 + value / 2.0F, this.getAmount4() + 8.0F);
      context.pose()
         .scale(
            ((Number)RangesKt.coerceIn(amount, RangesKt.rangeTo(0.0F, 2.0F))).floatValue(),
            ((Number)RangesKt.coerceIn(amount, RangesKt.rangeTo(0.0F, 2.0F))).floatValue()
         );
      context.pose().translate(-(amount32 + value / 2.0F), -(this.getAmount4() + 8.0F));
      defaultImpl(this, context.pose(), amount32, this.getAmount4(), value, 16.0F, 0.0F, false, 96, null);
      defaultImpl(this, this.svgTexture2, context.pose(), amount32 + 2.0F, this.getAmount4() + 1.5F, 10.0F, 10.0F, 0.0F, 64, null);
      defaultImpl(this, context.pose(), amount32 + 14, this.getAmount4() + 4.0F, 0.0F, 8, null);
      this.render(context, header, amount32 + 18.0F, this.getAmount4() + 2.0F, ThemeManager.INSTANCE.getColor7());
      context.pose().popMatrix();
   }

   public void onInput$Action(int value, InputEvent.Action action) {
      if (value == 0) {
         if (this.flag2 && action == InputEvent.Action.Press) {
            Iterable items5 = this.getItems5();
            var arrayList = new ArrayList();

            for (Object object : items5) {
               Setting setting = (Setting)object;
               if (!Intrinsics.areEqual(setting.getId(), "enabled") && !Intrinsics.areEqual(setting.getId(), "position")) {
                  arrayList.add(object);
               }
            }

            List list = arrayList;
            Iterator iterator = list.iterator();
            Float floatValue;
            if (!iterator.hasNext()) {
               floatValue = null;
            } else {
               Setting setting2 = (Setting)iterator.next();
               float amount7 = Fonts.getFontRenderer3().computeAmount(setting2.getText());

               while (iterator.hasNext()) {
                  Setting setting3 = (Setting)iterator.next();
                  float amount8 = Fonts.getFontRenderer3().computeAmount(setting3.getText());
                  amount7 = Math.max(amount7, amount8);
               }

               floatValue = amount7;
            }

            float value2 = (floatValue != null ? floatValue : 0.0F) + 23.0F;
            float value3 = 0.0F;
            Iterable iterable = list;

            for (Object object2 : iterable) {
               Setting setting4 = (Setting)object2;
               float amount32 = this.amount3 + value2 - 14.0F;
               float amount33 = this.amount3 + value2 - 3.0F;
               float amount9 = WidgetManager.INSTANCE.getAmount();
               if (amount32 <= amount9 ? amount9 <= amount33 : false) {
                  amount32 = this.amount4 + 3.5F + value3;
                  amount33 = this.amount4 + 14.5F + value3;
                  amount9 = WidgetManager.INSTANCE.getAmount2();
                  if ((amount32 <= amount9 ? amount9 <= amount33 : false) && setting4.getObject2() instanceof Boolean) {
                     String text2 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<kotlin.Boolean>";
                     setting4.setObject2(!(Boolean)setting4.getObject2());
                  }
               }

               value3 += 14.0F;
            }

            return;
         }

         if (action == InputEvent.Action.Release) {
            this.flag = false;
            WidgetManager.INSTANCE.setWidget(null);
         } else if (this.isFlag2() && action == InputEvent.Action.Press && WidgetManager.INSTANCE.getWidget() == null) {
            this.applyGetWindow2();
         }
      } else if (action == InputEvent.Action.Press && this.isFlag2()) {
         var items52 = this.getItems5();
         boolean flag11;
         if (items52 instanceof Collection && ((Collection)items52).isEmpty()) {
            flag11 = false;
         } else {
            Iterator iterator2 = items52.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag11 = false;
                  break;
               }

               Object object3 = iterator2.next();
               Setting setting5 = (Setting)object3;
               if (!Intrinsics.areEqual(setting5.getId(), "enabled") && !Intrinsics.areEqual(setting5.getId(), "position")) {
                  flag11 = true;
                  break;
               }
            }
         }

         if (flag11) {
            this.flag2 = !this.flag2;
            this.amount3 = WidgetManager.INSTANCE.getAmount();
            this.amount4 = WidgetManager.INSTANCE.getAmount2();
         }
      }
   }

   public final void applyGetWindow2() {
      if (this instanceof CoordWidget) {
         GLFW.glfwSetClipboardString(
            MinecraftHolder.getMinecraft().getWindow().handle(),
            (int)MinecraftHolder.getLocalPlayer().getX()
               + " "
               + (int)MinecraftHolder.getLocalPlayer().getY()
               + " "
               + (int)MinecraftHolder.getLocalPlayer().getZ()
         );
      }

      this.amount = WidgetManager.INSTANCE.getAmount() - this.getAmount3();
      this.amount2 = WidgetManager.INSTANCE.getAmount2() - this.getAmount4();
      this.flag = true;
      WidgetManager.INSTANCE.setWidget(this);
   }

   public final float getAmount3() {
      return this.getPos().getAmount() * this.getValue2();
   }

   public final float getAmount4() {
      return this.getPos().getAmount2() * this.getValue();
   }

   public final float getAmount7() {
      return WidgetManager.INSTANCE.getAmount();
   }

   public final float getAmount8() {
      return WidgetManager.INSTANCE.getAmount2();
   }

   public final boolean isFlag2() {
      float amount32 = this.getAmount3();
      float amount33 = this.getAmount3() + this.amount6;
      float amount7 = this.getAmount7();
      if (amount32 <= amount7 ? amount7 <= amount33 : false) {
         amount32 = this.getAmount4();
         amount33 = this.getAmount4() + this.amount5;
         amount7 = this.getAmount8();
         if (amount32 <= amount7 ? amount7 <= amount33 : false) {
            return true;
         }
      }

      return false;
   }

   public final void applyGetWindow3() {
      int value = WidgetManager.INSTANCE.getWidget() != null ? 221193 : 221188;
      GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(value));
   }

   public final void updateAmount(float amount, float amount2) {
      this.amount6 = amount;
      this.amount5 = amount2;
   }

   public final int getValue() {
      return MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight();
   }

   public final int getValue2() {
      return MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth();
   }

   public final boolean checkAbs(float amount, float amount2) {
      return Math.abs(amount - amount2) < 2.0F;
   }

   public final void render(Matrix3x2fStack m, float amount, float amount2, float amount3, float amount4, float amount5, boolean flag) {
      if (flag) {
         WidgetManager.INSTANCE.getTexturedQuadRenderer().render(m, amount, amount2, amount3, amount4, amount5);
      } else {
         TexturedQuadPass.defaultImpl(
            m,
            amount,
            amount2,
            amount3,
            amount4,
            3.0F,
            1.5F,
            0.0F,
            amount5,
            ThemeManager.INSTANCE.getColor5(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
      }
   }

   public static void defaultImpl(
      Widget widget,
      Matrix3x2fStack matrix3x2fStack,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      boolean flag,
      int value,
      Object object
   ) {
      if (object != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: plate");
      } else {
         if ((value & 32) != 0) {
            amount5 = 1.0F;
         }

         if ((value & 64) != 0) {
            flag = true;
         }

         widget.render(matrix3x2fStack, amount, amount2, amount3, amount4, amount5, flag);
      }
   }

   public final void render(Matrix3x2fStack m, float amount, float amount2, float amount3) {
      Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)m);
      WidgetManager.INSTANCE.getItems2().add(new DividerRenderState(matrix3x2f, amount, amount2, 0.5F, 8.5F, amount3));
   }

   public static void defaultImpl(Widget widget, Matrix3x2fStack matrix3x2fStack, float amount, float amount2, float amount3, int value, Object object) {
      if (object != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: divider");
      } else {
         if ((value & 8) != 0) {
            amount3 = 1.0F;
         }

         widget.render(matrix3x2fStack, amount, amount2, amount3);
      }
   }

   public final void render(SvgTexture image, Matrix3x2fStack m, float amount, float amount2, float amount3, float amount4, float amount5) {
      Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)m);
      WidgetManager.INSTANCE.getItems3().add(new IconRenderState(image, matrix3x2f, amount, amount2, amount3, amount4, amount5));
   }

   public static void defaultImpl(
      Widget widget,
      SvgTexture svgTexture,
      Matrix3x2fStack matrix3x2fStack,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      int value,
      Object object
   ) {
      if (object != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: icon");
      } else {
         if ((value & 64) != 0) {
            amount5 = 1.0F;
         }

         widget.render(svgTexture, matrix3x2fStack, amount, amount2, amount3, amount4, amount5);
      }
   }

   public final void render(GuiGraphicsExtractor context, String s, float amount, float amount2, Color color) {
      FontRenderer.defaultImpl(Fonts.getFontRenderer(), context.pose(), s, amount, amount2, color, false, 0, 96, null);
   }

   static {
   }
}