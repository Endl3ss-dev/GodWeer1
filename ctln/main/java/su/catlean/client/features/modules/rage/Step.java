// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;

public final class Step extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Step.class, "mode", "getMode()Lsu/catlean/h;", 0), new MutablePropertyReference1Impl(Step.class, "height", "getHeight()F", 0), new MutablePropertyReference1Impl(Step.class, "useTimer", "getUseTimer()Z", 0), new MutablePropertyReference1Impl(Step.class, "pauseIfShift", "getPauseIfShift()Z", 0) };
   @NotNull
   public static final Step INSTANCE = new Step();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", Step.Mode.NCP, null, null, 12, null);
   @NotNull
   private static final FloatSetting height = SettingFactory.defaultImpl(
      INSTANCE,
      "height",
      2.0F,
      RangesKt.rangeTo(1.0F, 2.5F),
      null,
      () -> INSTANCE.getMode() != Step.Mode.STRICT_NCP && INSTANCE.getMode() != Step.Mode.NCP_2,
      8,
      null
   );
   @NotNull
   private static final BooleanSetting timer = SettingFactory.defaultImpl(
      INSTANCE, "timer", true, null, () -> INSTANCE.getMode() != Step.Mode.VANILLA && INSTANCE.getMode() != Step.Mode.NCP_2, 4, null
   );
   @NotNull
   private static final BooleanSetting pauseIfShift = SettingFactory.defaultImpl(INSTANCE, "pause-if-shift", false, null, null, 12, null);

   private Step() {
      super("step", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final Step.Mode getMode() {
      return (Step.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getHeight() {
      return ((Number)height.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final boolean getUseTimer() {
      return (Boolean)timer.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getPauseIfShift() {
      return (Boolean)pauseIfShift.computeObject(this, $$delegatedProperties[3]);
   }

   @Override
   public void update4() {
      this.applyGetAttribute(0.6F);
   }

   @Flow
   public final void onPreSync(PreSyncEvent e) {
      if (this.getMode() != Step.Mode.NCP_2) {
         if ((!this.getPauseIfShift() || !MinecraftHolder.getMinecraft().options.keyShift.isDown())
            && !MinecraftHolder.getLocalPlayer().getAbilities().flying
            && !MinecraftHolder.getLocalPlayer().isHandsBusy()
            && !MinecraftHolder.getLocalPlayer().isInWater()
            && !MinecraftHolder.getLocalPlayer().isFallFlying()) {
            if (MinecraftHolder.getLocalPlayer().onGround()) {
               this.applyGetAttribute(this.getMode() == Step.Mode.STRICT_NCP ? 1.0F : this.getHeight());
            } else {
               this.applyGetAttribute(0.6F);
            }

            if (this.getMode() == Step.Mode.NCP || this.getMode() == Step.Mode.STRICT_NCP) {
               double value = MinecraftHolder.getLocalPlayer().getY() - MinecraftHolder.getLocalPlayer().yo;
               if (value <= 0.75 || value > this.getHeight()) {
                  return;
               }

               double[] amounts = this.computeAmounts(value);
               if (amounts != null && amounts.length > 1) {
                  if (this.getUseTimer()) {
                     TickService.INSTANCE.setAmount2(1.0F / amounts.length);
                  }

                  double[] values = amounts;
                  int value2 = 0;

                  for (int i = amounts.length; value2 < i; value2++) {
                     double value3 = values[value2];
                     Packet pos = (Packet)(
                        new Pos(
                           MinecraftHolder.getLocalPlayer().xo,
                           MinecraftHolder.getLocalPlayer().yo + value3,
                           MinecraftHolder.getLocalPlayer().zo,
                           false,
                           MinecraftHolder.getLocalPlayer().horizontalCollision
                        )
                     );
                     PacketUtil.applySend(pos);
                  }

                  if (this.getMode() == Step.Mode.STRICT_NCP) {
                     Packet pos2 = (Packet)(
                        new Pos(
                           MinecraftHolder.getLocalPlayer().xo,
                           MinecraftHolder.getLocalPlayer().yo + value,
                           MinecraftHolder.getLocalPlayer().zo,
                           false,
                           MinecraftHolder.getLocalPlayer().horizontalCollision
                        )
                     );
                     PacketUtil.applySend(pos2);
                  }
               }
            }
         } else {
            this.applyGetAttribute(0.6F);
         }
      }
   }

   @Flow
   private final void onMove(MoveEvent moveEvent) {
      if (MinecraftHolder.getLocalPlayer().horizontalCollision
         && this.getMode() == Step.Mode.NCP_2
         && (!this.getPauseIfShift() || !MinecraftHolder.getMinecraft().options.keyShift.isDown())) {
         if (MinecraftHolder.getLocalPlayer().onGround()) {
            moveEvent.setY(0.42);
         } else if (moveEvent.getY() == -0.1552320045166016) {
            moveEvent.setY(0.333);
         } else {
            if (moveEvent.getY() != -0.230527368912964) {
               return;
            }

            moveEvent.setY(0.25);
         }

         moveEvent.cancel();
      }
   }

   private final double[] computeAmounts(double amount) {
      return switch ((int)(amount * 10000)) {
         case 7500, 10000 -> {
            double[] values = new double[]{0.42, 0.753};
            yield values;
         }
         case 8125, 8750 -> {
            double[] values2 = new double[]{0.39, 0.7};
            yield values2;
         }
         case 15000 -> {
            double[] values3 = new double[]{0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
            yield values3;
         }
         case 20000 -> {
            double[] values4 = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
            yield values4;
         }
         case 25000 -> {
            double[] values5 = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
            yield values5;
         }
         default -> null;
      };
   }

   private final void applyGetAttribute(float amount) {
      MinecraftHolder.getLocalPlayer().getAttribute(Attributes.STEP_HEIGHT).setBaseValue(amount);
   }

   static {
   }

   public static enum Mode {
      NCP,
      VANILLA,
      STRICT_NCP,
      NCP_2;

      private static final Step.Mode[] modes = $values();
      private static final EnumEntries<Step.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<Step.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final Step.Mode[] $values() {
         return new Step.Mode[]{NCP, VANILLA, STRICT_NCP, NCP_2};
      }
   }
}