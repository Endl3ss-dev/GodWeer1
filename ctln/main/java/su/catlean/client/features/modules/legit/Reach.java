// deobf by pusia
package su.catlean.client.features.modules.legit;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.ReachStateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;

public final class Reach extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Reach.class, "blocksRange", "getBlocksRange()F", 0), new PropertyReference1Impl(Reach.class, "entity", "getEntity()F", 0) };
   @NotNull
   public static final Reach INSTANCE = new Reach();
   @NotNull
   private static final FloatSetting blocksRange = SettingFactory.defaultImpl(
      INSTANCE, "blocks-range", 3.0F, RangesKt.rangeTo(0.1F, 10.0F), null, null, 24, null
   );
   @NotNull
   private static final FloatSetting entityRange = SettingFactory.defaultImpl(
      INSTANCE, "entity-range", 3.0F, RangesKt.rangeTo(0.1F, 10.0F), null, null, 24, null
   );

   private Reach() {
      super("reach", SubCategories.getSubCategory3(), null, 4, null);
   }

   private final float getBlocksRange() {
      return ((Number)blocksRange.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   private final float getEntity() {
      return ((Number)entityRange.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   @Flow
   private final void onReachState(ReachStateEvent reachStateEvent) {
      reachStateEvent.setBlockRange(this.getBlocksRange());
      reachStateEvent.setEntityRange(this.getEntity());
      reachStateEvent.cancel();
   }

   static {
   }
}