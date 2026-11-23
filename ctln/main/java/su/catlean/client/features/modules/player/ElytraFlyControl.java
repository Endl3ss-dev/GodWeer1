// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
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
import su.catlean.utils.StopWatch;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.WorldUtil;

public final class ElytraFlyControl implements ElytraFlyStrategy {
   @NotNull
   public static final ElytraFlyControl INSTANCE = new ElytraFlyControl();
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();

   private ElytraFlyControl() {
   }

   @NotNull
   public final StopWatch getStopWatch() {
      return stopWatch;
   }

   @Override
   public void onPreSync(PreSyncEvent e) {
      if (!ElytraPlus.INSTANCE.getTwoBee() || ElytraPlus.INSTANCE.getMode() != ElytraPlus.Mode.BOOST) {
         if (MinecraftHolder.getLocalPlayer().onGround()) {
            ElytraPlus.INSTANCE.setFlag(true);
         }

         if (!ElytraPlus.INSTANCE.getCruiseControl()) {
            ElytraPlus.INSTANCE.setAmount2((float)MinecraftHolder.getLocalPlayer().getY());
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
         && MinecraftHolder.getLocalPlayer().isFallFlying()
         && !MinecraftHolder.getLocalPlayer().isInWater()
         && !MinecraftHolder.getLocalPlayer().isInLava()) {
         float value = MinecraftHolder.getLocalPlayer().input.getMoveVector().y;
         if (ElytraPlus.INSTANCE.getCruiseControl()) {
            if (MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
               float amount2 = ElytraPlus.INSTANCE.getAmount2();
               ElytraPlus.INSTANCE.setAmount2(amount2 + 1.0F);
            } else if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
               float amount22 = ElytraPlus.INSTANCE.getAmount2();
               ElytraPlus.INSTANCE.setAmount2(amount22 + -1.0F);
            }

            if (ElytraPlus.INSTANCE.getForceHeight()) {
               ElytraPlus.INSTANCE.setAmount2(ElytraPlus.INSTANCE.getManualHeight());
            }

            if (ElytraPlus.INSTANCE.getTwoBee()) {
               if (WorldUtil.INSTANCE.getAmount() >= ElytraPlus.INSTANCE.getMinUpSpeed()) {
                  MinecraftHolder.getLocalPlayer()
                     .setXRot(
                        (float)Mth.clamp(
                           Mth.wrapDegrees(
                              Math.toDegrees(Math.atan2((ElytraPlus.INSTANCE.getAmount2() - MinecraftHolder.getLocalPlayer().getY()) * -1.0, 10.0))
                           ),
                           -50.0,
                           50.0
                        )
                     );
               } else {
                  MinecraftHolder.getLocalPlayer().setXRot(0.25F);
               }
            } else {
               double amount = 1 - Math.sqrt(Mth.clamp(WorldUtil.INSTANCE.getAmount() / 1.7, 0.0, 1.0));
               if (WorldUtil.INSTANCE.getAmount() >= ElytraPlus.INSTANCE.getMinUpSpeed()
                  && ElytraPlus.INSTANCE.getStopWatch().testValue((int)(2000 * ElytraPlus.INSTANCE.getRedeployInterval()))) {
                  double value2 = -(44.4 * amount + 0.6);
                  double amount23 = (ElytraPlus.INSTANCE.getAmount2() + 1 - MinecraftHolder.getLocalPlayer().getY()) * 2;
                  double signum = -Math.toDegrees(Math.atan2(Math.abs(amount23), WorldUtil.INSTANCE.getAmount() * 30.0)) * Math.signum(amount23);
                  MinecraftHolder.getLocalPlayer().setXRot((float)(value2 + (signum - value2) * Mth.clamp(Math.abs(amount23), 0.0, 1.0)));
               } else {
                  MinecraftHolder.getLocalPlayer().setXRot(0.25F);
                  value = 1.0F;
               }
            }
         }

         if (ElytraPlus.INSTANCE.getTwoBee()) {
            if (MinecraftHolder.getMinecraft().options.keyJump.isDown() || !ElytraPlus.INSTANCE.getOnlySpace() || ElytraPlus.INSTANCE.getCruiseControl()) {
               double[] amounts = WorldUtil.INSTANCE.computeAmounts(ElytraPlus.INSTANCE.getFactor() / 10.0);
               e.setX(e.getX() + amounts[0]);
               e.setZ(e.getZ() + amounts[1]);
               e.setY(e.getY() + 0.003);
            }
         } else {
            Vec3 viewVector = MinecraftHolder.getLocalPlayer().getViewVector(Render2D.INSTANCE.getAmount2());
            double hypot = Math.hypot(viewVector.x, viewVector.z);
            double hypot2 = Math.hypot(e.getX(), e.getZ());
            float radians = (float)(Math.pow(Math.cos(Math.toRadians(MinecraftHolder.getLocalPlayer().getXRot())), 2.0) * Math.min(1.0, viewVector.length() / 0.4));
            e.setY(e.getY() + (-0.08 + radians * 0.06));
            if (e.getY() < 0.0 && hypot > 0.0) {
               double value3 = e.getY() * -0.1 * radians;
               e.setY(e.getY() + value3);
               e.setX(e.getX() + viewVector.x * value3 / hypot);
               e.setZ(e.getZ() + viewVector.z * value3 / hypot);
            }

            if (MinecraftHolder.getLocalPlayer().getXRot() < 0.0F) {
               double value4 = hypot2 * -Math.sin(Math.toRadians(MinecraftHolder.getLocalPlayer().getXRot())) * 0.04;
               e.setY(e.getY() + value4 * 3.2);
               e.setX(e.getX() - viewVector.x * value4 / hypot);
               e.setZ(e.getZ() - viewVector.z * value4 / hypot);
            }

            if (hypot > 0.0) {
               e.setX(e.getX() + (viewVector.x / hypot * hypot2 - e.getX()) * 0.1);
               e.setZ(e.getZ() + (viewVector.z / hypot * hypot2 - e.getZ()) * 0.1);
            }

            if (MinecraftHolder.getLocalPlayer().getXRot() > 0.0F && e.getY() < 0.0 && ElytraPlus.INSTANCE.isFlag3()) {
               if (value != 0.0F
                  && ElytraPlus.INSTANCE.getStopWatch().testValue((int)(2000 * ElytraPlus.INSTANCE.getRedeployInterval()))
                  && stopWatch.testValue((int)(1000 * ElytraPlus.INSTANCE.getRedeployTimeOut()))) {
                  if (ElytraPlus.INSTANCE.getStopMotion()) {
                     e.setX(0.0);
                     e.setZ(0.0);
                  }

                  ElytraPlus.INSTANCE.getStopWatch().update();
                  Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
                  PacketUtil.applySend(serverboundPlayerCommandPacket);
               } else if (!ElytraPlus.INSTANCE.getStopWatch().testValue((int)(2000 * ElytraPlus.INSTANCE.getRedeployInterval()))) {
                  e.setX(e.getX() - value * Math.sin(Math.toRadians(MinecraftHolder.getLocalPlayer().getYRot())) * ElytraPlus.INSTANCE.getFactor() / 20.0F);
                  e.setZ(e.getZ() + value * Math.cos(Math.toRadians(MinecraftHolder.getLocalPlayer().getYRot())) * ElytraPlus.INSTANCE.getFactor() / 20.0F);
                  stopWatch.update();
               }
            }
         }

         double hypot3 = Math.hypot(e.getX(), e.getZ());
         if (ElytraPlus.INSTANCE.getSpeedLimit() && hypot3 > ElytraPlus.INSTANCE.getMaxSpeed()) {
            e.setX(e.getX() * ElytraPlus.INSTANCE.getMaxSpeed() / hypot3);
            e.setZ(e.getZ() * ElytraPlus.INSTANCE.getMaxSpeed() / hypot3);
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
      if (ElytraPlus.INSTANCE.getTwoBee() && InputUtil.checkContains(ElytraPlus.INSTANCE.getLift().getValue()) && WorldUtil.INSTANCE.getAmount3() > 0.0) {
         if (!MinecraftHolder.getLocalPlayer().isFallFlying()) {
            ElytraSwap.INSTANCE.setFlag(true);
            Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
            PacketUtil.applySend(serverboundPlayerCommandPacket);
            MinecraftHolder.getLocalPlayer().startFallFlying();
         }

         double time = 0.05 * Math.min((float)(MinecraftHolder.getTime() - stopWatch2.getTime()) / 100.0F, 1.0F);
         MinecraftHolder.getLocalPlayer().setDeltaMovement(0.0, Math.min(MinecraftHolder.getLocalPlayer().getDeltaMovement().y + time, 1.0), 0.0);
      } else {
         stopWatch2.update();
      }
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
      if (e.getPacket() instanceof ClientboundSetEntityDataPacket
         && ((ClientboundSetEntityDataPacket)e.getPacket()).id() == MinecraftHolder.getLocalPlayer().getId()
         && InputUtil.checkContains(ElytraPlus.INSTANCE.getLift().getValue())
         && !((ClientboundSetEntityDataPacket)e.getPacket()).packedItems().isEmpty()) {
         e.cancel();
      }
   }
}