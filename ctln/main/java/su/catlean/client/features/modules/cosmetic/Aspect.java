// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.BasicProjectionMatrixEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;

public final class Aspect extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(Aspect.class, "ratio", "getRatio()F", 0)};
   @NotNull
   public static final Aspect INSTANCE = new Aspect();
   @NotNull
   private static final FloatSetting ratio = SettingFactory.defaultImpl(INSTANCE, "ratio", 1.0F, RangesKt.rangeTo(0.1F, 10.0F), null, null, 24, null);

   private Aspect() {
      super("aspect", SubCategories.getSubCategory6(), null, 4, null);
   }

   public final float getRatio() {
      return ((Number)ratio.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   @Flow
   private final void onBasicProjectionMatrix(BasicProjectionMatrixEvent basicProjectionMatrixEvent) {
      basicProjectionMatrixEvent.setRatio(this.getRatio());
      basicProjectionMatrixEvent.cancel();
   }

   static {
   }
}