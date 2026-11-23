// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class LongJump extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(LongJump.class, "useTimer", "getUseTimer()Z", 0), new PropertyReference1Impl(LongJump.class, "timerValue", "getTimerValue()F", 0), new PropertyReference1Impl(LongJump.class, "jumpDisable", "getJumpDisable()Z", 0), new PropertyReference1Impl(LongJump.class, "speed", "getSpeed()F", 0), new PropertyReference1Impl(LongJump.class, "maxDistance", "getMaxDistance()F", 0) };
   @NotNull
   public static final LongJump INSTANCE = new LongJump();
   @NotNull
   private static final BooleanSetting timer = SettingFactory.defaultImpl(INSTANCE, "timer", false, null, null, 12, null);
   @NotNull
   private static final FloatSetting timerSpeed = SettingFactory.defaultImpl(
      INSTANCE, "timer-speed", 1.0F, RangesKt.rangeTo(0.5F, 3.0F), null, () -> INSTANCE.getUseTimer(), 8, null
   );
   @NotNull
   private static final BooleanSetting jumpDisable = SettingFactory.defaultImpl(INSTANCE, "jump-disable", true, null, null, 12, null);
   @NotNull
   private static final FloatSetting speed = SettingFactory.defaultImpl(INSTANCE, "speed", 1.35F, RangesKt.rangeTo(0.1F, 10.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting maxDistance = SettingFactory.defaultImpl(
      INSTANCE, "max-distance", 10.0F, RangesKt.rangeTo(5.0F, 40.0F), null, null, 24, null
   );
   private static float amount;
   private static float amount2;
   private static int value;
   @Nullable
   private static Vec3 vec3;

   private LongJump() {
      super("long-jump", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final boolean getUseTimer() {
      return (Boolean)timer.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getTimerValue() {
      return ((Number)timerSpeed.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final boolean getJumpDisable() {
      return (Boolean)jumpDisable.computeObject(this, $$delegatedProperties[2]);
   }

   private final float getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   private final float getMaxDistance() {
      return ((Number)maxDistance.computeObject(this, $$delegatedProperties[4])).floatValue();
   }

   @Flow
   public final void onMove(MoveEvent e) {
      if (vec3 != null && this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceToSqr(vec3) > this.getMaxDistance() * this.getMaxDistance()) {
         this.applyUnplug();
      }

      if (WorldUtil.INSTANCE.isFlag4()) {
         if (this.getUseTimer()) {
            TickService.INSTANCE.setAmount2(this.getTimerValue());
         }

         switch (value) {
            case 0:
               amount = (float)(this.getSpeed() * WorldUtil.INSTANCE.getAmount7());
               amount2 = 0.0F;
               value++;
               break;
            case 1:
               MinecraftHolder.getLocalPlayer()
                  .setDeltaMovement(
                     MinecraftHolder.getLocalPlayer().getDeltaMovement().x(), 0.42 + this.getAmount(), MinecraftHolder.getLocalPlayer().getDeltaMovement().z()
                  );
               e.setY(0.42 + this.getAmount());
               amount *= 2.149F;
               value++;
               break;
            case 2:
               double amount22 = 0.66F * (amount2 - WorldUtil.INSTANCE.getAmount7());
               amount = (float)(amount2 - amount22);
               value++;
               break;
            default:
               if (MinecraftHolder.getLocalPlayer().verticalCollision
                  || MinecraftHolder.getClientLevel()
                     .getBlockCollisions(
                        (Entity)MinecraftHolder.getLocalPlayer(),
                        MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(-0.2, MinecraftHolder.getLocalPlayer().getDeltaMovement().y(), -0.2)
                     )
                     .iterator()
                     .hasNext()) {
                  if (this.getJumpDisable()) {
                     this.applyUnplug();
                  }

                  value = 0;
                  amount2 = 0.0F;
               }

               amount = amount2 - amount2 / 159.0F;
         }
      }

      amount = Math.max((float)WorldUtil.INSTANCE.getAmount7(), amount);
      WorldUtil.INSTANCE.onMove(e, amount);
      e.cancel();
   }

   @Flow
   public final void onReceivePacket(ReceivePacket e) {
      if (e.getPacket() instanceof ClientboundPlayerPositionPacket) {
         this.applyUnplug();
      }
   }

   @Override
   public void update2() {
      this.update();
   }

   private final void update() {
      if (MinecraftHolder.getMinecraft().player != null) {
         vec3 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
         amount = 0.0F;
         amount2 = 0.0F;
         value = 0;
      }
   }

   private final float getAmount() {
      return MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.JUMP_BOOST) ? 0.2F : 0.0F;
   }

   @Flow
   public final void onPreSync(PreSyncEvent eventSync) {
      if (WorldUtil.INSTANCE.isFlag4()) {
         amount2 = (float)Math.hypot(
            MinecraftHolder.getLocalPlayer().getX() - MinecraftHolder.getLocalPlayer().xo,
            MinecraftHolder.getLocalPlayer().getZ() - MinecraftHolder.getLocalPlayer().zo
         );
      } else {
         this.update();
      }
   }

   static {
   }
}