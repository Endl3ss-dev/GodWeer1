// deobf by pusia
package su.catlean.client.features.modules.world;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.BasicProjectionMatrixEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.input.InputUtil;

public final class Zoom extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Zoom.class, "teleBind", "getTeleBind()Lsu/catlean/dd;", 0), new PropertyReference1Impl(Zoom.class, "teleScale", "getTeleScale()F", 0), new PropertyReference1Impl(Zoom.class, "wideBind", "getWideBind()Lsu/catlean/dd;", 0), new PropertyReference1Impl(Zoom.class, "wideScale", "getWideScale()F", 0) };
   @NotNull
   public static final Zoom INSTANCE = new Zoom();
   @NotNull
   private static final BindSetting telephoto = SettingFactory.defaultImpl(INSTANCE, "telephoto", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static final FloatSetting crop = SettingFactory.defaultImpl(INSTANCE, "crop", 8.0F, RangesKt.rangeTo(1.0F, 25.0F), null, null, 24, null);
   @NotNull
   private static final BindSetting ultraWide = SettingFactory.defaultImpl(INSTANCE, "ultra-wide", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static final FloatSetting reversedCrop = SettingFactory.defaultImpl(
      INSTANCE, "reversed-crop", 0.75F, RangesKt.rangeTo(0.1F, 1.0F), null, null, 24, null
   );
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);

   private Zoom() {
      super("zoom", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final Bind getTeleBind() {
      return (Bind)telephoto.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getTeleScale() {
      return ((Number)crop.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final Bind getWideBind() {
      return (Bind)ultraWide.computeObject(this, $$delegatedProperties[2]);
   }

   private final float getWideScale() {
      return ((Number)reversedCrop.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   @Flow
   private final void onBasicProjectionMatrix(BasicProjectionMatrixEvent basicProjectionMatrixEvent) {
      animation.applyDoubleValue(
         InputUtil.checkContains(this.getTeleBind().getValue())
            ? this.getTeleScale()
            : (InputUtil.checkContains(this.getWideBind().getValue()) ? this.getWideScale() : 1.0F)
      );
      if (InputUtil.checkContains(this.getWideBind().getValue()) || InputUtil.checkContains(this.getTeleBind().getValue()) || animation.getAmount() != 1.0F) {
         basicProjectionMatrixEvent.setFov(basicProjectionMatrixEvent.getFov() / animation.getAmount());
         basicProjectionMatrixEvent.setRatio(
            (float)MinecraftHolder.getMinecraft().getWindow().getWidth() / MinecraftHolder.getMinecraft().getWindow().getHeight()
         );
         basicProjectionMatrixEvent.cancel();
      }
   }

   static {
   }
}