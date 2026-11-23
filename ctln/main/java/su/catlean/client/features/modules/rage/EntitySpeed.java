// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.EventPlayerTravel;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class EntitySpeed extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(EntitySpeed.class, "accelerate", "getAccelerate()Z", 0), new PropertyReference1Impl(EntitySpeed.class, "accelerateFactor", "getAccelerateFactor()F", 0), new PropertyReference1Impl(EntitySpeed.class, "stopunloaded", "getStopunloaded()Z", 0), new PropertyReference1Impl(EntitySpeed.class, "speed", "getSpeed()F", 0), new PropertyReference1Impl(EntitySpeed.class, "timer", "getTimer()F", 0), new PropertyReference1Impl(EntitySpeed.class, "jitter", "getJitter()F", 0) };
   @NotNull
   public static final EntitySpeed INSTANCE = new EntitySpeed();
   @NotNull
   private static final BooleanSetting accelerate = SettingFactory.defaultImpl(INSTANCE, "accelerate", true, null, null, 12, null);
   @NotNull
   private static final FloatSetting accelerateFactor = SettingFactory.defaultImpl(
      INSTANCE, "accelerate-factor", 9.0F, RangesKt.rangeTo(0.0F, 20.0F), null, () -> INSTANCE.getAccelerate(), 8, null
   );
   @NotNull
   private static final BooleanSetting stopUnloaded = SettingFactory.defaultImpl(INSTANCE, "stop-unloaded", true, null, null, 12, null);
   @NotNull
   private static final FloatSetting speed = SettingFactory.defaultImpl(INSTANCE, "speed", 0.77F, RangesKt.rangeTo(0.1F, 5.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting timer = SettingFactory.defaultImpl(INSTANCE, "timer", 1.0F, RangesKt.rangeTo(0.1F, 5.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting jitter = SettingFactory.defaultImpl(INSTANCE, "jitter", 0.05F, RangesKt.rangeTo(0.0F, 0.5F), null, null, 24, null);
   private static int value;
   private static float amount;

   private EntitySpeed() {
      super("entity-speed", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final boolean getAccelerate() {
      return (Boolean)accelerate.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getAccelerateFactor() {
      return ((Number)accelerateFactor.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final boolean getStopunloaded() {
      return (Boolean)stopUnloaded.computeObject(this, $$delegatedProperties[2]);
   }

   private final float getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   private final float getTimer() {
      return ((Number)timer.computeObject(this, $$delegatedProperties[4])).floatValue();
   }

   private final float getJitter() {
      return ((Number)jitter.computeObject(this, $$delegatedProperties[5])).floatValue();
   }

   @Override
   public void update2() {
      value = 0;
      amount = 0.0F;
   }

   @Flow
   public final void onPlayerTravel(EventPlayerTravel ev) {
      if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getMinecraft().level != null) {
         Entity controlledVehicle = MinecraftHolder.getLocalPlayer().getControlledVehicle();
         if (controlledVehicle != null) {
            Entity entity = controlledVehicle;
            if (MinecraftHolder.getClientLevel().hasChunk((int)this.computeVec3(entity).x() >> 4, (int)this.computeVec3(entity).z() >> 4)
                  && !(this.computeVec3(entity).y() < -60.0)
               || !this.getStopunloaded()) {
               if (entity.horizontalCollision || MinecraftHolder.getLocalPlayer().horizontalCollision) {
                  amount = 0.0F;
               }

               if (this.getTimer() != 1.0F) {
                  TickService.INSTANCE.setAmount2(this.getTimer());
               }

               double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, this.getAmount(), false, 0.0F, 0.0F, 0.0F, 30, null);
               double value2 = entity.getX() + defaultImpl[0];
               double value3 = entity.getZ() + defaultImpl[1];
               if (MinecraftHolder.getClientLevel().hasChunk((int)value2 >> 4, (int)value3 >> 4) && !(this.computeVec3(entity).y() < -60.0)
                  || !this.getStopunloaded()) {
                  if (WorldUtil.INSTANCE.isFlag4()) {
                     entity.setDeltaMovement(defaultImpl[0], entity.getDeltaMovement().y(), defaultImpl[1]);
                  } else {
                     entity.setDeltaMovement(0.0, entity.getDeltaMovement().y(), 0.0);
                  }

                  int value4 = value++;
                  if (value4 > 50) {
                     value = 0;
                  }

                  ev.cancel();
               }
            }
         }
      }
   }

   @Flow
   public final void onReceivePacket(ReceivePacket e) {
      if (e.getPacket() instanceof ClientboundPlayerPositionPacket) {
         amount = 0.0F;
      }
   }

   private final float getAmount() {
      float amount2 = amount + (20.0F - this.getAccelerateFactor()) / this.getSpeed();
      amount = amount2;
      double value2 = amount2 / 100.0F;
      double speed2 = this.getSpeed();
      float speed3 = (float)Math.min(value2, speed2);
      if (!this.getAccelerate()) {
         speed3 = this.getSpeed();
      }

      return speed3 + (value > 25 ? this.getJitter() : 0.0F);
   }

   static {
   }
}