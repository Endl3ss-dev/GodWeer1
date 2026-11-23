// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.reflect.KMutableProperty0;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.Rectangle;
import su.catlean.api.data.WayPoint;
import su.catlean.client.features.modules.esp.WaypointRenderer;
import su.catlean.client.features.ui.panel.Panel;
import su.catlean.client.features.ui.panel.components.DeleteButton;
import su.catlean.client.features.ui.panel.components.EditableLine;
import su.catlean.client.features.ui.panel.components.PanelComponent;
import su.catlean.client.features.ui.panel.components.WaypointDimensionButton;
import su.catlean.client.features.ui.panel.dialogs.Dialog;
import su.catlean.client.features.ui.panel.dialogs.NameInputDialog;
import su.catlean.client.features.ui.panel.panels.waypoint.WayPointItem;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class WaypointPanel extends Panel {
   @NotNull
   public static final WaypointPanel INSTANCE = new WaypointPanel();
   @NotNull
   private static List<WayPointItem> items = new ArrayList();
   @Nullable
   private static PanelComponent panelComponent;
   @Nullable
   private static PanelComponent panelComponent2;
   @Nullable
   private static PanelComponent panelComponent3;
   @Nullable
   private static WayPointItem wayPointItem;
   @NotNull
   private static Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   @NotNull
   private static Animation animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   @NotNull
   private static Animation animation3 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 100L);
   private static boolean flag;
   private static float amount;
   @NotNull
   private static final EditableLine editableLine = new EditableLine("Name", INSTANCE.getAmount3(), null, arg -> {
      if (wayPointItem != null) {
         WayPoint point = wayPointItem.getPoint();
         if (point != null) {
            point.updateText(((su.catlean.client.features.ui.panel.components.EditableLine)arg).getText());
         }
      }

      return Unit.INSTANCE;
   }, 4, null);
   @NotNull
   private static final EditableLine editableLine2 = new EditableLine("Server", INSTANCE.getAmount3(), null, arg -> {
      if (wayPointItem != null) {
         WayPoint point = wayPointItem.getPoint();
         if (point != null) {
            point.updateText2(((su.catlean.client.features.ui.panel.components.EditableLine)arg).getText());
         }
      }

      return Unit.INSTANCE;
   }, 4, null);
   @NotNull
   private static final EditableLine editableLine3 = new EditableLine("X", INSTANCE.getAmount3(), null, arg -> {
      if (wayPointItem != null) {
         WayPoint point = wayPointItem.getPoint();
         if (point != null) {
            Integer intOrNull = StringsKt.toIntOrNull(((su.catlean.client.features.ui.panel.components.EditableLine)arg).getText());
            point.setValue(intOrNull != null ? intOrNull : 0);
         }
      }

      return Unit.INSTANCE;
   }, 4, null);
   @NotNull
   private static final EditableLine editableLine4 = new EditableLine("Y", INSTANCE.getAmount3(), null, arg -> {
      if (wayPointItem != null) {
         WayPoint point = wayPointItem.getPoint();
         if (point != null) {
            Integer intOrNull = StringsKt.toIntOrNull(((su.catlean.client.features.ui.panel.components.EditableLine)arg).getText());
            point.setValue2(intOrNull != null ? intOrNull : 0);
         }
      }

      return Unit.INSTANCE;
   }, 4, null);
   @NotNull
   private static final EditableLine editableLine5 = new EditableLine("Z", INSTANCE.getAmount3(), null, arg -> {
      if (wayPointItem != null) {
         WayPoint point = wayPointItem.getPoint();
         if (point != null) {
            Integer intOrNull = StringsKt.toIntOrNull(((su.catlean.client.features.ui.panel.components.EditableLine)arg).getText());
            point.setValue3(intOrNull != null ? intOrNull : 0);
         }
      }

      return Unit.INSTANCE;
   }, 4, null);

   private WaypointPanel() {
      super("waypoint-panel", 0.0F, 0.0F, 0.0F, 0.0F, 30, null);
   }

   @NotNull
   public final List<WayPointItem> getItems() {
      return items;
   }

   @Nullable
   public final PanelComponent getPanelComponent() {
      return panelComponent;
   }

   public final void setPanelComponent(PanelComponent panelComponent) {
      WaypointPanel.panelComponent = panelComponent;
   }

   @Nullable
   public final PanelComponent getPanelComponent3() {
      return panelComponent3;
   }

   public final void setPanelComponent3(PanelComponent panelComponent) {
      panelComponent3 = panelComponent;
   }

   @Nullable
   public final WayPointItem getWayPointItem() {
      return wayPointItem;
   }

   public final void setWayPointItem(WayPointItem wayPointItem) {
      WaypointPanel.wayPointItem = wayPointItem;
   }

   @NotNull
   public final Animation getAnimation() {
      return animation;
   }

   @Override
   public void applyClear() {
      items.clear();

      for (Object item : WaypointRenderer.INSTANCE.getItems()) {
         WayPoint wayPoint = (WayPoint)item;
         items.add(new WayPointItem(wayPoint));
      }
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2) {
      boolean flag2;
      label60: {
         super.render(context, value, value2);
         animation.applyDoubleValue(1.0F);
         animation2.applyDoubleValue(this.getDialog() != null ? 0.5F : 0.0F);
         animation3.applyDoubleValue(amount);
         float amount2 = this.getAmount() + 3.0F;
         float amount3 = this.getAmount() + 72.0F;
         float value3 = value;
         if (amount2 <= value3 ? value3 <= amount3 : false) {
            amount2 = this.getAmount2() + 5.0F;
            amount3 = this.getAmount2() + 13.0F;
            value3 = value2;
            if (amount2 <= value3 ? value3 <= amount3 : false) {
               flag2 = true;
               break label60;
            }
         }

         flag2 = false;
      }

      flag = flag2;
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount() + 5.0F,
         this.getAmount2() + 3.0F,
         69.0F,
         8.0F,
         2.0F,
         flag ? 2.5F : 2.0F,
         flag ? 0.1F : 0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor4(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer().render(context, "+", this.getAmount() + 36.0F, this.getAmount2() + 1.0F, Color.WHITE);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount() + 76.0F,
         this.getAmount2(),
         6.0F,
         this.getAmount4(),
         1.0F,
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
      panelComponent3 = null;
      panelComponent = null;
      float amount4 = 0.0F;
      amount4 = 17.0F + animation3.getAmount();
      Render2D instance = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(this.getAmount(), this.getAmount2() + 14, this.getAmount() + 76, this.getAmount2() + this.getAmount4() - 2.0F);
      instance.render(context, rectangle);
      Iterable<WayPointItem> items2 = items;

      for (Object object : items2) {
         WayPointItem wayPointItem2 = (WayPointItem)object;
         wayPointItem2.render(context, INSTANCE.getAmount(), INSTANCE.getAmount2() + amount4, value, value2);
         amount4 += 29.0F;
      }

      Render2D.INSTANCE.render(context);
      if (wayPointItem != null) {
         float amount5 = (1.0F - animation.getAmount()) * 10.0F;
         Function0 function0 = () -> {
            WaypointRenderer.INSTANCE.checkRemove(wayPointItem.getPoint());
            Collection<WayPointItem> items3 = items;
            WayPointItem wayPointItem3 = wayPointItem;
            TypeIntrinsics.asMutableCollection(items3).remove(wayPointItem3);
            wayPointItem = null;
            return Unit.INSTANCE;
         };
         editableLine.render(context, this.getAmount() + 84.0F, this.getAmount2() + 17.0F + amount5, value, value2);
         editableLine2.render(context, this.getAmount() + 84.0F, this.getAmount2() + 32.0F + amount5, value, value2);
         editableLine3.render(context, this.getAmount() + 84.0F, this.getAmount2() + 47.0F + amount5, value, value2);
         editableLine4.render(context, this.getAmount() + 84.0F, this.getAmount2() + 62.0F + amount5, value, value2);
         editableLine5.render(context, this.getAmount() + 84.0F, this.getAmount2() + 77.0F + amount5, value, value2);
         new WaypointDimensionButton(
               this.getAmount3(),
               (KMutableProperty0)(new WaypointPanel$dimension$prop(wayPointItem.getPoint())),
               (KMutableProperty0)(new WaypointPanel$hoveringComponent$prop2(this))
            )
            .render(context, this.getAmount() + 84.0F, this.getAmount2() + 92.0F + amount5, value, value2);
         new DeleteButton(function0, this.getAmount3(), (KMutableProperty0)(new WaypointPanel$hoveringComponent$prop(this)))
            .render(context, this.getAmount() + 84.0F, this.getAmount2() + 107.0F + amount5, value, value2);
      }

      float amount6 = this.getAmount();
      float amount22 = this.getAmount2();
      float amount32 = this.getAmount3();
      float amount42 = this.getAmount4();
      Color color = Render2D.INSTANCE.computeColor(Color.BLACK, animation2.getAmount());
      Color color2 = Render2D.INSTANCE.computeColor(Color.BLACK, animation2.getAmount());
      Color color3 = Render2D.INSTANCE.computeColor(Color.BLACK, animation2.getAmount());
      Color color4 = Render2D.INSTANCE.computeColor(Color.BLACK, animation2.getAmount());
      Matrix3x2fStack pose = context.pose();
      rectangle = null;
      short shortValue = 3072;
      Object object2 = null;
      float value4 = 0.0F;
      Matrix3x2fStack matrix3x2fStack = pose;
      Color color5 = color4;
      Color color6 = color3;
      Color color7 = color2;
      Color color8 = color;
      float value5 = 5.0F;
      BoxPass.defaultImpl(amount6, amount22, amount32, amount42, value5, color8, color7, color6, color5, matrix3x2fStack, value4, (Color)object2, shortValue, rectangle);
      Dialog dialog = this.getDialog();
      if (dialog != null) {
         dialog.render(context, this.getAmount() + this.getAmount3() / 2.0F - 60.0F, this.getAmount2() + this.getAmount4() / 2 - 35.0F, value, value2);
      }
   }

   private final void applyGetServerData(String text) {
      label19: {
         if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getMinecraft().level != null) {
            ClientPacketListener connection = MinecraftHolder.getMinecraft().getConnection();
            if ((connection != null ? connection.getServerData() : null) != null) {
               WaypointRenderer instance = WaypointRenderer.INSTANCE;
               int value = (int)MinecraftHolder.getLocalPlayer().getX();
               int value2 = (int)MinecraftHolder.getLocalPlayer().getY();
               int value3 = (int)MinecraftHolder.getLocalPlayer().getZ();
               String text2 = MinecraftHolder.getClientPacketListener().getServerData().ip;
               int value4 = WaypointRenderer.INSTANCE.computeValue(MinecraftHolder.getClientLevel().dimension().identifier().getPath());
               Object object = null;
               byte byteValue = 64;
               boolean flag2 = false;
               int value5 = value4;
               String text3 = text2;
               instance.testWayPoint(new WayPoint(text, value, value2, value3, text3, value5, flag2, byteValue, (DefaultConstructorMarker)object));
               break label19;
            }
         }

         WaypointRenderer.INSTANCE.testWayPoint(new WayPoint(text, 0, 0, 0, null, 0, false, 112, null));
      }

      this.applyClear();
      this.setDialog(null);
   }

   @Override
   public void updateAmount(float amount, float amount2, int value) {
      super.updateAmount(amount, amount2, value);
      if (flag) {
         EditableLine.itemSearchCompanion.updateEditableLine(null);
         this.setDialog(new NameInputDialog(arg -> {
            INSTANCE.applyGetServerData((String)arg);
            return Unit.INSTANCE;
         }));
      } else if (this.getDialog() == null || !this.getDialog().testAmount(amount, amount2, value)) {
         this.setDialog(null);
         if (panelComponent != null) {
            panelComponent2 = panelComponent;
            panelComponent.checkIntValue(amount, amount2, value);
         } else {
            panelComponent2 = null;
            if (panelComponent3 != null) {
               switch (value) {
                  case 0:
                     ((WayPointItem)panelComponent3).getPoint().setFlag(!((WayPointItem)panelComponent3).getPoint().isFlag());
                     break;
                  case 1:
                     wayPointItem = (WayPointItem)panelComponent3;
                     editableLine.updateText(wayPointItem.getPoint().getId());
                     editableLine2.updateText(wayPointItem.getPoint().getServer());
                     editableLine3.updateText(String.valueOf(wayPointItem.getPoint().getValue()));
                     editableLine4.updateText(String.valueOf(wayPointItem.getPoint().getValue2()));
                     editableLine5.updateText(String.valueOf(wayPointItem.getPoint().getValue3()));
                     animation.updateAmount(0.0F);
               }
            }
         }
      }
   }

   @Override
   public void applyClamp(double amount, double amount2, double amount3) {
      float amount4 = this.getAmount();
      if (amount <= this.getAmount() + 76.0F ? amount4 <= amount : false) {
         amount4 = this.getAmount2();
         if (amount2 <= this.getAmount2() + this.getAmount4() - 2.0F ? amount4 <= amount2 : false) {
            WaypointPanel.amount += (float)amount3 * 7.0F;
            WaypointPanel.amount = Math.clamp(WaypointPanel.amount, items.size() * -29.0F, 0.0F);
         }
      }
   }

   static {
      INSTANCE.applyClear();
      INSTANCE.setAmount(290.0F);
   }
}