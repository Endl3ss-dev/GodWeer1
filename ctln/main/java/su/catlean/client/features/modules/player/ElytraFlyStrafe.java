// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.AfterElytraEvent;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreElytraEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;
import su.catlean.client.features.modules.equip.ElytraSwap;
import su.catlean.client.features.modules.rage.ElytraPlus;
import su.catlean.client.services.TickService;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class ElytraFlyStrafe implements ElytraFlyStrategy {
   @NotNull
   public static final ElytraFlyStrafe INSTANCE = new ElytraFlyStrafe();

   private ElytraFlyStrafe() {
   }

   @Override
   public void onPreSync(PreSyncEvent e) {
      if (MinecraftHolder.getLocalPlayer().onGround()) {
         ElytraPlus.INSTANCE.setFlag(true);
      }

      if (!MinecraftHolder.getLocalPlayer().isFallFlying() && ElytraPlus.INSTANCE.isFlag3()) {
         if (ElytraPlus.INSTANCE.isFlag()
            && !MinecraftHolder.getLocalPlayer().onGround()
            && WorldUtil.INSTANCE.getAmount3() > 0.0
            && ElytraPlus.INSTANCE.getInstantFly()) {
            TickService.INSTANCE.setAmount2(0.3F);
         }

         if (!MinecraftHolder.getLocalPlayer().onGround()
            && ElytraPlus.INSTANCE.getInstantFly()
            && MinecraftHolder.getLocalPlayer().getDeltaMovement().y() < 0.0) {
            if (!ElytraPlus.INSTANCE.getStopWatch().testValue((int)(1000 * ElytraPlus.INSTANCE.getRedeployDelay()))) {
               return;
            }

            ElytraPlus.INSTANCE.getStopWatch().update();
            ElytraSwap.INSTANCE.setFlag(true);
            Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
            PacketUtil.applySend(serverboundPlayerCommandPacket);
            ElytraPlus.INSTANCE.setFlag(false);
            ElytraPlus.INSTANCE.getStopWatch2().update();
         }
      }
   }

   @Override
   public void onPreElytra(PreElytraEvent e) {
   }

   @Override
   public void onAfterElytra(AfterElytraEvent e) {
   }

   @Override
   public void onMove(MoveEvent e) {
      if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(38).getItem(), Items.ELYTRA)
         && MinecraftHolder.getLocalPlayer().isFallFlying()) {
         WorldUtil instance = WorldUtil.INSTANCE;
         float xzSpeed = ElytraPlus.INSTANCE.getXzSpeed();
         double value;
         if (ElytraPlus.INSTANCE.getAccelerate()) {
            float accelerateFactor = ElytraPlus.INSTANCE.getAccelerateFactor();
            float value2 = xzSpeed;
            WorldUtil worldUtil = instance;
            ElytraPlus.INSTANCE.setAmount(ElytraPlus.INSTANCE.getAmount() + accelerateFactor);
            float amount = ElytraPlus.INSTANCE.getAmount();
            instance = worldUtil;
            xzSpeed = value2;
            double value3 = amount / 100.0F;
            double value4 = 1.0;
            value = Math.min(value3, value4);
         } else {
            value = 1.0;
         }

         double[] defaultImpl = WorldUtil.defaultImpl(instance, xzSpeed * (float)value, false, 0.0F, 0.0F, 0.0F, 30, null);
         e.setX(defaultImpl[0]);
         e.setY(
            MinecraftHolder.getMinecraft().options.keyJump.isDown()
               ? ElytraPlus.INSTANCE.getUpSpeed()
               : (
                  MinecraftHolder.getMinecraft().options.keyShift.isDown()
                     ? -ElytraPlus.INSTANCE.getSneakDownSpeed()
                     : (
                           ElytraPlus.INSTANCE.getPitchControl() && WorldUtil.INSTANCE.isFlag4()
                              ? MinecraftHolder.getLocalPlayer().getXRot()
                                 / 90.0
                                 * -ElytraPlus.INSTANCE.getUpSpeed()
                                 * Math.signum(MinecraftHolder.getLocalPlayer().input.getMoveVector().y)
                              : 0.0
                        )
                        - 0.08 * ElytraPlus.INSTANCE.getDownFactor()
               )
         );
         e.setZ(defaultImpl[1]);
         if (!WorldUtil.INSTANCE.isFlag4()) {
            ElytraPlus.INSTANCE.setAmount(0.0F);
         }

         MinecraftHolder.getLocalPlayer().setDeltaMovement(e.getX(), e.getY(), e.getZ());
         e.cancel();
      }
   }

   @Override
   public void onAfterSendPacket(AfterSendPacket event) {
   }

   @Override
   public void onReceivePacket(ReceivePacket e) {
   }

   @Override
   public void onPlayerUpdate(PlayerUpdateEvent e) {
   }

   @Override
   public void update() {
   }

   @Override
   public void update2() {
   }

   @Override
   public void onFireWorkVelocity(FireWorkVelocityEvent e) {
   }

   @Override
   public void onSetPose(SetPoseEvent e) {
   }

   @Override
   public void onReceivePacket2(ReceivePacket e) {
   }
}