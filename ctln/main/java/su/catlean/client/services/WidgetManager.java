// deobf by pusia
package su.catlean.client.services;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.slf4j.Logger;
import su.catlean.api.data.DividerRenderState;
import su.catlean.api.data.IconRenderState;
import su.catlean.api.data.ModuleValue;
import su.catlean.api.data.SettingValue;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.CrosshairRenderEvent;
import su.catlean.api.event.events.render.FrameBufferEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.ScreenMouseCoordsEvent;
import su.catlean.api.service.Service;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.widgets.ArmorWidget;
import su.catlean.client.features.ui.hud.widgets.ChestCounterWidget;
import su.catlean.client.features.ui.hud.widgets.CooldownsWidget;
import su.catlean.client.features.ui.hud.widgets.CoordWidget;
import su.catlean.client.features.ui.hud.widgets.CrosshairWidget;
import su.catlean.client.features.ui.hud.widgets.CrystalSpeedWidget;
import su.catlean.client.features.ui.hud.widgets.FlightWidget;
import su.catlean.client.features.ui.hud.widgets.FpsWidget;
import su.catlean.client.features.ui.hud.widgets.InventoryWidget;
import su.catlean.client.features.ui.hud.widgets.KeybindsWidget;
import su.catlean.client.features.ui.hud.widgets.MacroWidget;
import su.catlean.client.features.ui.hud.widgets.MediaPlayerWidget;
import su.catlean.client.features.ui.hud.widgets.NotificationsWidget;
import su.catlean.client.features.ui.hud.widgets.PhaseWidget;
import su.catlean.client.features.ui.hud.widgets.PingWidget;
import su.catlean.client.features.ui.hud.widgets.PlayerListWidget;
import su.catlean.client.features.ui.hud.widgets.PotionsWidget;
import su.catlean.client.features.ui.hud.widgets.PvpResourcesWidget;
import su.catlean.client.features.ui.hud.widgets.SpeedWidget;
import su.catlean.client.features.ui.hud.widgets.StaffWidget;
import su.catlean.client.features.ui.hud.widgets.TargetInfoWidget;
import su.catlean.client.features.ui.hud.widgets.TpsWidget;
import su.catlean.client.features.ui.hud.widgets.WatermarkWidget;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;

public final class WidgetManager implements Service {
   @NotNull
   public static final WidgetManager INSTANCE = new WidgetManager();
   @NotNull
   private static List<Widget> items = new ArrayList();
   @NotNull
   private static final TexturedQuadRenderer texturedQuadRenderer = new TexturedQuadRenderer();
   @NotNull
   private static List items2 = new ArrayList();
   @NotNull
   private static List items3 = new ArrayList();
   @Nullable
   private static Widget widget;
   @Nullable
   private static Widget widget2;
   private static float amount;
   private static float amount2;
   public static final float amount3 = 5.0F;

   private WidgetManager() {
   }

   @NotNull
   public final List<Widget> getItems() {
      return items;
   }

   @NotNull
   public final TexturedQuadRenderer getTexturedQuadRenderer() {
      return texturedQuadRenderer;
   }

   @NotNull
   public final List getItems2() {
      return items2;
   }

   @NotNull
   public final List getItems3() {
      return items3;
   }

   @Nullable
   public final Widget getWidget() {
      return widget;
   }

   public final void setWidget(Widget widget) {
      WidgetManager.widget = widget;
   }

   @Nullable
   public final Widget getWidget2() {
      return widget2;
   }

   public final void setWidget2(Widget widget) {
      widget2 = widget;
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      WidgetManager.amount = amount;
   }

   public final float getAmount2() {
      return amount2;
   }

   public final void setAmount2(float amount) {
      amount2 = amount;
   }

   @Flow
   private final void onScreenMouseCoords(ScreenMouseCoordsEvent screenMouseCoordsEvent) {
      amount = screenMouseCoordsEvent.getX();
      amount2 = screenMouseCoordsEvent.getY();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      for (Object item : items) {
         Widget widget3 = (Widget)item;
         if (widget3.getEnabled()) {
            widget3.update();
         }
      }
   }

   @Flow(
      priority = -20
   )
   private final void onRender2D(Render2DEvent render2DEvent) {
      widget2 = null;
      Iterable<Widget> items4 = items;
      boolean flag = false;

      for (Object object : items4) {
         Widget widget3 = (Widget)object;
         if (widget3.getEnabled()) {
            widget3.render4(render2DEvent.getContext());
         }
      }

      texturedQuadRenderer.render(
         render2DEvent.getContext().pose(),
         Render2D.INSTANCE
            .computeColor(ThemeManager.INSTANCE.getColor5(), Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor5()) * Render2D.INSTANCE.getAmount()),
         Render2D.INSTANCE
            .computeColor(ThemeManager.INSTANCE.getColor6(), Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor6()) * Render2D.INSTANCE.getAmount()),
         false
      );
      if (!items2.isEmpty()) {
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, items2.size(), false, 4, null);
         Color color = new Color(1157627903, true);
         items2.removeIf(
            arg -> {
               meshBuilder.computeMeshBuilder(
                     ((DividerRenderState)arg).getM(),
                     ((DividerRenderState)arg).getAmount(),
                     ((DividerRenderState)arg).getAmount2() + ((DividerRenderState)arg).getAmount4(),
                     0.0F
                  )
                  .fetchAlpha(color)
                  .computeMeshBuilder(
                     ((DividerRenderState)arg).getM(),
                     ((DividerRenderState)arg).getAmount() + ((DividerRenderState)arg).getAmount3(),
                     ((DividerRenderState)arg).getAmount2() + ((DividerRenderState)arg).getAmount4(),
                     0.0F
                  )
                  .fetchAlpha(color)
                  .computeMeshBuilder(
                     ((DividerRenderState)arg).getM(),
                     ((DividerRenderState)arg).getAmount() + ((DividerRenderState)arg).getAmount3(),
                     ((DividerRenderState)arg).getAmount2(),
                     0.0F
                  )
                  .fetchAlpha(color)
                  .computeMeshBuilder(((DividerRenderState)arg).getM(), ((DividerRenderState)arg).getAmount(), ((DividerRenderState)arg).getAmount2(), 0.0F)
                  .fetchAlpha(color);
               return true;
            }
         );
         MeshBuilder.defaultImpl(
            meshBuilder,
            Pipelines.INSTANCE.getRenderPipeline22(),
            null,
            null,
            null,
            new Matrix3x2f((Matrix3x2fc)render2DEvent.getContext().pose()),
            null,
            null,
            110,
            null
         );
      }

      if (!items3.isEmpty()) {
         items3.removeIf(
            arg2 -> {
               SvgTexture.defaultImpl(
                  ((IconRenderState)arg2).getImage(),
                  ((IconRenderState)arg2).getM(),
                  ((IconRenderState)arg2).getAmount(),
                  ((IconRenderState)arg2).getAmount2(),
                  ((IconRenderState)arg2).getAmount3(),
                  ((IconRenderState)arg2).getAmount4(),
                  null,
                  null,
                  ((IconRenderState)arg2).getAmount5(),
                  96,
                  null
               );
               return true;
            }
         );
      }

      items4 = items;
      flag = false;

      for (Object object2 : items4) {
         Widget widget4 = (Widget)object2;
         if (widget4.getEnabled()) {
            widget4.render2(render2DEvent.getContext());
         }
      }

      Fonts.getFontRenderer().update();
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if (inputEvent.getDevice() == InputEvent.Device.Mouse && MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         for (Object item : items) {
            Widget widget3 = (Widget)item;
            if (widget3.getEnabled()) {
               widget3.onInput$Action(inputEvent.getKey(), inputEvent.getAction());
            }
         }
      }
   }

   @Flow
   private final void onFrameBuffer(FrameBufferEvent frameBufferEvent) {
      if (InventoryWidget.INSTANCE.getEnabled() && InventoryWidget.INSTANCE.isFlag()) {
         frameBufferEvent.setFrameBuffer((RenderTarget)InventoryWidget.INSTANCE.getMainTarget());
         frameBufferEvent.cancel();
      }
   }

   @Flow
   private final void onCrosshairRender(CrosshairRenderEvent crosshairRenderEvent) {
      if (CrosshairWidget.INSTANCE.getEnabled()) {
         crosshairRenderEvent.cancel();
      }
   }

   private final void applyAddAll() {
      List<Widget> items4 = items;
      Widget[] widgets = new Widget[]{
         CoordWidget.INSTANCE,
         FpsWidget.INSTANCE,
         KeybindsWidget.INSTANCE,
         PingWidget.INSTANCE,
         PotionsWidget.INSTANCE,
         StaffWidget.INSTANCE,
         TargetInfoWidget.INSTANCE,
         TpsWidget.INSTANCE,
         WatermarkWidget.INSTANCE,
         NotificationsWidget.INSTANCE,
         SpeedWidget.INSTANCE,
         InventoryWidget.INSTANCE,
         CrystalSpeedWidget.INSTANCE,
         MediaPlayerWidget.INSTANCE,
         ArmorWidget.INSTANCE,
         ChestCounterWidget.INSTANCE,
         PvpResourcesWidget.INSTANCE,
         PlayerListWidget.INSTANCE,
         CrosshairWidget.INSTANCE,
         FlightWidget.INSTANCE,
         PhaseWidget.INSTANCE,
         CooldownsWidget.INSTANCE,
         MacroWidget.INSTANCE
      };
      items4.addAll(CollectionsKt.listOf(widgets));
   }

   public final void applyIsEmpty(Widget widget) {
      var items4 = items;
      boolean flag;
      if (items4 instanceof Collection && ((Collection)items4).isEmpty()) {
         flag = false;
      } else {
         Iterator<Widget> iterator = items4.iterator();

         while (true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Widget object = iterator.next();
            Widget widget3 = (Widget)object;
            if (Intrinsics.areEqual(widget3.getId(), widget.getId())) {
               flag = true;
               break;
            }
         }
      }

      if (!flag) {
         items.add(widget);
         this.applyGetMessage(widget);
      }
   }

   public final void update() {
      for (Object item : items) {
         Widget widget3 = (Widget)item;
         this.applyGetMessage(widget3);
      }
   }

   private final void applyGetMessage(Widget widget) {
      for (Object items5Item : widget.getItems5()) {
         Setting setting = (Setting)items5Item;
         Iterable widgets = ProxyManager.INSTANCE.getWidgetDataSource().getWidgetConfig().getWidgets();
         Iterator iterator = widgets.iterator();

         Object catlean;
         while (true) {
            if (!iterator.hasNext()) {
               catlean = null;
               break;
            }

            Object object = iterator.next();
            ModuleValue moduleValue = (ModuleValue)object;
            if (Intrinsics.areEqual(moduleValue.getId(), widget.getId())) {
               catlean = object;
               break;
            }
         }

         catlean = (ModuleValue)catlean;
         if ((ModuleValue)catlean != null) {
            ModuleValue moduleValue2 = (ModuleValue)catlean;
            Iterable settings = moduleValue2.getSettings();
            Iterator iterator2 = settings.iterator();

            while (true) {
               if (iterator2.hasNext()) {
                  Object object2 = iterator2.next();
                  SettingValue settingValue = (SettingValue)object2;
                  if (!Intrinsics.areEqual(settingValue.getId(), setting.getId())) {
                     continue;
                  }

                  catlean = object2;
               } else {
                  catlean = null;
               }

               catlean = (SettingValue)catlean;
               if ((SettingValue)catlean != null) {
                  SettingValue settingValue2 = (SettingValue)catlean;

                  try {
                     setting.applyDecodeFromString(settingValue2.getValue());
                  } catch (Exception exception) {
                     setting.update();
                     catlean = MinecraftHolder.getCatlean();
                     String text = setting.getText();
                     String text2 = widget.getText();
                     String message = exception.getMessage();
                     String text3 = text2;
                     String text4 = text;
                     ((org.slf4j.Logger)catlean).warn("Could not parse setting: " + text4 + " in " + text3 + ": " + message);
                  }
               }
               break;
            }
         }
      }
   }

   static {
      INSTANCE.applyAddAll();
      INSTANCE.update();
   }
}