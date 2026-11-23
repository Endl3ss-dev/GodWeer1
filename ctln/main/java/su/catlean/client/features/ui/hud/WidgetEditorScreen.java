// deobf by pusia
package su.catlean.client.features.ui.hud;

import java.awt.Color;
import java.util.ArrayList;
import kotlin.collections.ArraysKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.client.features.ui.clickgui.SearchBar;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.WidgetManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class WidgetEditorScreen extends Screen {
   @NotNull
   public static final WidgetEditorScreen INSTANCE = new WidgetEditorScreen();
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   private static float amount;
   private static float amount2;
   private static float amount3 = -1.0F;
   private static float amount4 = -1.0F;
   private static boolean flag;
   private static float amount5 = -1.0F;
   private static float amount6 = -1.0F;
   private static boolean flag2;
   private static boolean flag3;
   @NotNull
   private static ArrayList<WidgetSettingsPanel> items = new ArrayList();

   private WidgetEditorScreen() {
      super(Component.nullToEmpty("catlean-widget-editor"));
   }

   public void extractRenderState(GuiGraphicsExtractor context, int value, int value2, float amount) {
      super.extractRenderState(context, value, value2, amount);
      int guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth();
      int guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight();
      if (flag3) {
         amount3 = value - WidgetEditorScreen.amount;
         amount4 = value2 - amount2;
      }

      boolean flag22;
      label71: {
         float amount32 = amount3 - 4.0F;
         float amount33 = amount3 - 4.0F + 100.0F;
         float value3 = value;
         if (amount32 <= value3 ? value3 <= amount33 : false) {
            amount32 = amount4 - 8.0F;
            amount33 = amount4 - 5.0F;
            value3 = value2;
            if (amount32 <= value3 ? value3 <= amount33 : false) {
               flag22 = true;
               break label71;
            }
         }

         flag22 = false;
      }

      flag2 = flag22;
      animation.applyDoubleValue(flag2 ? 1.0F : 0.0F);
      if (amount3 == -1.0F && amount4 == -1.0F) {
         amount3 = guiScaledWidth / 2.0F - 58;
         amount4 = 50.0F;
      }

      Render2D.INSTANCE.render(context.pose(), 5.0F, 5.0F, guiScaledWidth - 10.0F, guiScaledHeight - 10.0F, Color.WHITE);
      if (Math.abs(value - amount5) > 1.0F && Math.abs(value2 - amount6) > 1.0F && flag && WidgetManager.INSTANCE.getWidget() == null && !flag3) {
         float amount52 = Math.min(amount5, (float)value);
         float amount62 = Math.min(amount6, (float)value2);
         float amount53 = Math.abs(amount5 - value);
         float amount63 = Math.abs(amount6 - value2);
         Render2D.INSTANCE.render(context.pose(), amount52, amount62, amount53, amount63, Color.WHITE);
      }

      TexturedQuadPass.defaultImpl(
         context.pose(),
         amount3 - 4.0F,
         amount4 - 14.0F,
         116.0F,
         items.size() * 15.0F + 15.0F,
         7.0F,
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
      Color alpha = Render2D.INSTANCE.fetchAlpha(new Color(1157627903, true), Render2D.INSTANCE.computeColor(Color.white, 0.5F), animation.getAmount());
      float amount34 = amount3 + 4.0F;
      float amount42 = amount4 - 8.0F;
      Matrix3x2fStack pose = context.pose();
      Object object = null;
      short shortValue = 3072;
      Object object2 = null;
      float value4 = 0.0F;
      Matrix3x2fStack matrix3x2fStack = pose;
      float value5 = 1.5F;
      BoxPass.defaultImpl(amount34, amount42, 100.0F, 3.0F, value5, alpha, alpha, alpha, alpha, matrix3x2fStack, value4, (Color)object2, shortValue, object);
      float value6 = 0.0F;
      Iterable<WidgetSettingsPanel> items2 = items;

      for (Object object3 : items2) {
         WidgetSettingsPanel widgetSettingsPanel = (WidgetSettingsPanel)object3;
         widgetSettingsPanel.render(context, value, value2, amount3, amount4 + value6);
         value6 += 15.0F;
      }

      SearchBar.INSTANCE.render(context, value, value2);
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean flag) {
      double value = click.x();
      double value2 = click.y();
      int button = click.button();
      SearchBar.INSTANCE.checkGetGuiScaledWidth(value, value2, button);
      WidgetEditorScreen.flag = true;
      amount5 = (float)value;
      amount6 = (float)value2;
      flag3 = flag2;
      amount = (float)value - amount3;
      amount2 = (float)value2 - amount4;

      for (Object item : items) {
         WidgetSettingsPanel widgetSettingsPanel = (WidgetSettingsPanel)item;
         widgetSettingsPanel.updateAmount(value, value2);
      }

      return super.mouseClicked(click, flag);
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      double value = click.x();
      double value2 = click.y();
      int button = click.button();
      flag = false;
      flag3 = false;
      SearchBar.INSTANCE.updateAmount(value, value2, button);
      return super.mouseReleased(click);
   }

   public boolean charTyped(CharacterEvent input) {
      Character firstOrNull = ArraysKt.firstOrNull(input.codepointAsString().toCharArray());
      if (firstOrNull != null) {
         char ch = firstOrNull;
         SearchBar.INSTANCE.applyLength(ch);
         return super.charTyped(input);
      } else {
         return super.charTyped(input);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void extractBackground(GuiGraphicsExtractor context, int value, int value2, float amount) {
   }

   static {
      for (Object item : WidgetManager.INSTANCE.getItems()) {
         Widget widget = (Widget)item;
         items.add(new WidgetSettingsPanel(widget));
      }
   }
}