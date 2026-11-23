// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.CameraType;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.CameraOffsetEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;

public final class NoCameraClip extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(NoCameraClip.class, "distance", "getDistance()F", 0)};
   @NotNull
   public static final NoCameraClip INSTANCE = new NoCameraClip();
   @NotNull
   private static final FloatSetting distance = SettingFactory.defaultImpl(INSTANCE, "distance", 3.0F, RangesKt.rangeTo(1.0F, 20.0F), null, null, 24, null);
   @NotNull
   private static Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 600L);
   @NotNull
   private static CameraType cameraType = CameraType.FIRST_PERSON;

   private NoCameraClip() {
      super("no-camera-clip", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final float getDistance() {
      return ((Number)distance.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (cameraType != MinecraftHolder.getMinecraft().options.getCameraType()) {
         animation.updateAmount(0.0F);
         cameraType = MinecraftHolder.getMinecraft().options.getCameraType();
      }

      animation.applyDoubleValue(1.0F);
   }

   @Flow
   private final void onCameraOffset(CameraOffsetEvent cameraOffsetEvent) {
      cameraOffsetEvent.setOffset(1.0F + (this.getDistance() - 1.0F) * animation.getAmount());
      cameraOffsetEvent.cancel();
   }

   static {
   }
}