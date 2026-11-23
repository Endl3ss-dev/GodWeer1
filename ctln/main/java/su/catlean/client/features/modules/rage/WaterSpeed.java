// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.LiquidBlock;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.SprintEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class WaterSpeed extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(WaterSpeed.class, "mode", "getMode()Lsu/catlean/n;", 0), new PropertyReference1Impl(WaterSpeed.class, "speed", "getSpeed()F", 0) };
   @NotNull
   public static final WaterSpeed INSTANCE = new WaterSpeed();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", WaterSpeed.Mode.DolphinGrace, null, null, 12, null);
   @NotNull
   private static final FloatSetting speed = SettingFactory.defaultImpl(INSTANCE, "speed", 2.0F, RangesKt.rangeTo(1.0F, 10.0F), null, null, 24, null);

   private WaterSpeed() {
      super("water-speed", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final WaterSpeed.Mode getMode() {
      return (WaterSpeed.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   @Override
   public void update4() {
      if (this.getMode() == WaterSpeed.Mode.DolphinGrace) {
         MinecraftHolder.getLocalPlayer().removeEffect(MobEffects.DOLPHINS_GRACE);
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getMode() == WaterSpeed.Mode.DolphinGrace) {
         if (MinecraftHolder.getLocalPlayer().isSwimming()) {
            MinecraftHolder.getLocalPlayer().addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 2, 2));
         } else {
            MinecraftHolder.getLocalPlayer().removeEffect(MobEffects.DOLPHINS_GRACE);
         }
      }
   }

   @Flow
   private final void onMove(MoveEvent moveEvent) {
      if (this.getMode() == WaterSpeed.Mode.Vanilla && MinecraftHolder.getLocalPlayer().isSwimming()) {
         WorldUtil.INSTANCE.onMove(moveEvent, this.getSpeed());
         moveEvent.cancel();
      }
   }

   @Flow
   private final void onSprint(SprintEvent sprintEvent) {
      if (MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().level != null
         && this.getEnabled()
         && this.getMode() == WaterSpeed.Mode.Legit
         && (
            MinecraftHolder.getLocalPlayer().isInWater()
               || MinecraftHolder.getClientLevel()
                  .getBlockState(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(0.0, -0.5, 0.0)))
                  .getBlock() instanceof LiquidBlock
         )) {
         sprintEvent.setSprinting(true);
      }
   }

   static {
   }

   public static enum Mode {
      DolphinGrace,
      Vanilla,
      Legit;

      private static final WaterSpeed.Mode[] modes = $values();
      private static final EnumEntries<WaterSpeed.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<WaterSpeed.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final WaterSpeed.Mode[] $values() {
         return new WaterSpeed.Mode[]{DolphinGrace, Vanilla, Legit};
      }
   }
}