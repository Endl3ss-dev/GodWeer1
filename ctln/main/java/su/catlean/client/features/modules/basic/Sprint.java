// deobf by pusia
package su.catlean.client.features.modules.basic;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class Sprint extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(Sprint.class, "pauseInWater", "getPauseInWater()Z", 0)};
   @NotNull
   public static final Sprint INSTANCE = new Sprint();
   @NotNull
   private static final BooleanSetting pauseInWater = SettingFactory.defaultImpl(INSTANCE, "pause-in-water", false, null, null, 12, null);
   private static boolean flag;

   private Sprint() {
      super("sprint", SubCategories.getSubCategory4(), null, 4, null);
   }

   private final boolean getPauseInWater() {
      return (Boolean)pauseInWater.computeObject(this, $$delegatedProperties[0]);
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      Sprint.flag = flag;
   }

   @Flow(
      priority = 10
   )
   private final void onTick(TickEvent tickEvent) {
      if (this.isFlag6()) {
         if (flag) {
            flag = false;
         } else {
            if (WorldUtil.INSTANCE.isFlag4()
               && (!this.getPauseInWater() || !MinecraftHolder.getLocalPlayer().isUnderWater())
               && !MinecraftHolder.getLocalPlayer().isFallFlying()) {
               MinecraftHolder.getLocalPlayer().setSprinting(true);
            }
         }
      }
   }

   static {
   }
}