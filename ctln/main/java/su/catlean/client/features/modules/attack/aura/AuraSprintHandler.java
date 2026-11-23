// deobf by pusia
package su.catlean.client.features.modules.attack.aura;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.basic.Sprint;
import su.catlean.mixins.accessors.KeyMappingAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.world.WorldUtil;

public final class AuraSprintHandler implements AuraHandler {
   @NotNull
   public static final AuraSprintHandler INSTANCE = new AuraSprintHandler();
   private static boolean flag;
   private static boolean flag2;

   private AuraSprintHandler() {
   }

   public final boolean isFlag2() {
      return flag2;
   }

   public final void setFlag2(boolean flag) {
      flag2 = flag;
   }

   @Override
   public void update() {
      boolean flag3 = WorldUtil.INSTANCE.isFlag() && !MinecraftHolder.getLocalPlayer().isSwimming();
      if (flag3
         && !Aura.INSTANCE.getOnHitSwap()
         && (Aura.INSTANCE.getSprintMode() == Aura.Sprint.NORMAL || Aura.INSTANCE.getSprintMode() == Aura.Sprint.HVH)
         && !MinecraftHolder.getLocalPlayer().onGround()) {
         flag = true;
         MinecraftHolder.getLocalPlayer().setSprinting(false);
         MinecraftHolder.getClientPacketListener()
            .send((Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.STOP_SPRINTING)));
      }
   }

   @Override
   public void applyMax(Rotation rotation) {
      if (flag) {
         MinecraftHolder.getLocalPlayer().setSprinting(true);
         MinecraftHolder.getClientPacketListener()
            .send((Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_SPRINTING)));
         flag = false;
      }

      flag2 = false;
      if (Aura.INSTANCE.getSprintMode() == Aura.Sprint.LEGIT_EXTRA || Aura.INSTANCE.getSprintMode() == Aura.Sprint.LEGIT || this.isFlag()) {
         if (Aura.INSTANCE.getSprintMode() == Aura.Sprint.LEGIT_EXTRA) {
            WorldUtil.INSTANCE.applyGetKey();
         }

         MinecraftHolder.getLocalPlayer()
            .setSprinting(
               InputConstants.isKeyDown(
                  MinecraftHolder.getMinecraft().getWindow(), ((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyUp).getKey().getValue()
               )
            );
      }
   }

   @Override
   public void update2() {
   }

   public final boolean isFlag() {
      return Aura.INSTANCE.getOnHitSwap() && AuraDamageHandler.INSTANCE.getValue() != -1;
   }

   public final boolean checkGetDeltaMovement(boolean flag) {
      boolean flag3 = flag;
      boolean flag4 = Aura.INSTANCE.getSprintMode() == Aura.Sprint.LEGIT_EXTRA;
      if (AuraMovementHandler.INSTANCE.testValue(1)
         && MinecraftHolder.getLocalPlayer().getDeltaMovement().y < -0.07
         && (MinecraftHolder.getLocalPlayer().isSprinting() || this.isFlag() && MinecraftHolder.getMinecraft().options.keyJump.isDown())
         && (Aura.INSTANCE.getSprintMode() == Aura.Sprint.LEGIT || Aura.INSTANCE.getSprintMode() == Aura.Sprint.LEGIT_EXTRA || this.isFlag())) {
         if (flag4) {
            WorldUtil.INSTANCE.applySetDown();
         }

         MinecraftHolder.getLocalPlayer().setSprinting(false);
         Sprint.INSTANCE.setFlag(true);
         flag3 = false;
         MinecraftHolder.getMinecraft().options.keySprint.setDown(false);
         flag2 = true;
         AuraDamageHandler.INSTANCE.applyScreen();
         TaskScheduler.defaultImpl(
            TaskScheduler.INSTANCE,
            3,
            (byte)0,
            false,
            () -> {
               flag2 = false;
               MinecraftHolder.getMinecraft()
                  .options
                  .keySprint
                  .setDown(
                     InputConstants.isKeyDown(
                        MinecraftHolder.getMinecraft().getWindow(), ((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keySprint).getKey().getValue()
                     )
                  );
               if (flag4) {
                  WorldUtil.INSTANCE.applyGetKey();
               }
            },
            6,
            null
         );
      }

      return flag3;
   }
}