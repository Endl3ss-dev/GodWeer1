// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalBreakChecker;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class ReverseStep extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ReverseStep.class, "mode", "getMode()Lsu/catlean/ns;", 0), new PropertyReference1Impl(ReverseStep.class, "timer", "getTimer()F", 0), new PropertyReference1Impl(ReverseStep.class, "motion", "getMotion()F", 0), new PropertyReference1Impl(ReverseStep.class, "pauseIfShift", "getPauseIfShift()Z", 0) };
   @NotNull
   public static final ReverseStep INSTANCE = new ReverseStep();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", ReverseStep.Mode.Motion, null, null, 12, null);
   @NotNull
   private static final FloatSetting timer = SettingFactory.defaultImpl(
      INSTANCE, "timer", 3.0F, RangesKt.rangeTo(1.0F, 5.0F), null, () -> INSTANCE.getMode() == ReverseStep.Mode.Timer, 8, null
   );
   @NotNull
   private static final FloatSetting motion = SettingFactory.defaultImpl(
      INSTANCE, "motion", 1.0F, RangesKt.rangeTo(0.1F, 2.0F), null, () -> INSTANCE.getMode() == ReverseStep.Mode.Motion, 8, null
   );
   @NotNull
   private static final BooleanSetting pauseIfShift = SettingFactory.defaultImpl(INSTANCE, "pause-if-shift", false, null, null, 12, null);

   private ReverseStep() {
      super("reverse-step", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final ReverseStep.Mode getMode() {
      return (ReverseStep.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getTimer() {
      return ((Number)timer.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final float getMotion() {
      return ((Number)motion.computeObject(this, $$delegatedProperties[2])).floatValue();
   }

   private final boolean getPauseIfShift() {
      return (Boolean)pauseIfShift.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   public final void onPreSync(PreSyncEvent e) {
      if ((!this.getPauseIfShift() || !MinecraftHolder.getMinecraft().options.keyShift.isDown())
         && !MinecraftHolder.getLocalPlayer().isInWater()
         && !MinecraftHolder.getLocalPlayer().isUnderWater()
         && !MinecraftHolder.getLocalPlayer().isInLava()
         && !MinecraftHolder.getLocalPlayer().isFallFlying()
         && !MinecraftHolder.getLocalPlayer().getAbilities().flying
         && !CrystalBreakChecker.INSTANCE.checkGetX((Player)MinecraftHolder.getLocalPlayer())) {
         if (this.getMode() == ReverseStep.Mode.Timer
            && !MinecraftHolder.getMinecraft().options.keyJump.isDown()
            && !MinecraftHolder.getLocalPlayer().onGround()
            && !(MinecraftHolder.getLocalPlayer().getDeltaMovement().y() >= -0.1)) {
            TickService.INSTANCE.setAmount2(this.getTimer());
         }

         if (MinecraftHolder.getLocalPlayer().onGround() && this.getMode() == ReverseStep.Mode.Motion) {
            MinecraftHolder.getLocalPlayer().setDeltaMovement(MinecraftHolder.getLocalPlayer().getDeltaMovement().add(0.0, -this.getMotion(), 0.0));
         }
      }
   }

   static {
   }

   public static enum Mode {
      Timer,
      Motion;

      private static final ReverseStep.Mode[] modes = $values();
      private static final EnumEntries<ReverseStep.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<ReverseStep.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final ReverseStep.Mode[] $values() {
         return new ReverseStep.Mode[]{Timer, Motion};
      }
   }
}