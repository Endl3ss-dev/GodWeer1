// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
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
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class ElytraFlyGlide implements ElytraFlyStrategy {
   @NotNull
   public static final ElytraFlyGlide INSTANCE = new ElytraFlyGlide();

   private ElytraFlyGlide() {
   }

   @Override
   public void onPreSync(PreSyncEvent e) {
      if ((!this.isFlag() || !ElytraPlus.INSTANCE.getStopOnGround()) && ElytraPlus.INSTANCE.isFlag3()) {
         if (ElytraPlus.INSTANCE.getInfDurability() || !MinecraftHolder.getLocalPlayer().isFallFlying()) {
            ElytraSwap.INSTANCE.setFlag(true);
            Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
            PacketUtil.applySend(serverboundPlayerCommandPacket);
         }

         if (MinecraftHolder.getLocalPlayer().tickCount % 3 != 0 && ElytraPlus.INSTANCE.getNcpStrictMode() == ElytraPlus.NcpStrict.Motion) {
            e.cancel();
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
      MinecraftHolder.getLocalPlayer().getAbilities().flying = false;
      MinecraftHolder.getLocalPlayer().getAbilities().setFlyingSpeed(0.05F);
      if ((!this.isFlag() || !ElytraPlus.INSTANCE.getStopOnGround())
         && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(38).getItem(), Items.ELYTRA)) {
         MinecraftHolder.getLocalPlayer().getAbilities().flying = true;
         Abilities abilities = MinecraftHolder.getLocalPlayer().getAbilities();
         double xzSpeed = ElytraPlus.INSTANCE.getXzSpeed() / 15.0F;
         double value;
         if (ElytraPlus.INSTANCE.getAccelerate()) {
            float accelerateFactor = ElytraPlus.INSTANCE.getAccelerateFactor();
            double value2 = xzSpeed;
            Abilities abilities2 = abilities;
            ElytraPlus.INSTANCE.setAmount(ElytraPlus.INSTANCE.getAmount() + accelerateFactor);
            float amount = ElytraPlus.INSTANCE.getAmount();
            abilities = abilities2;
            xzSpeed = value2;
            double value3 = amount / 100.0;
            double value4 = 1.0;
            value = Math.min(value3, value4);
         } else {
            value = 1.0;
         }

         abilities.setFlyingSpeed((float)(xzSpeed * value));
         e.cancel();
         if (MinecraftHolder.getLocalPlayer().tickCount % 3 == 0 && ElytraPlus.INSTANCE.getNcpStrictMode() == ElytraPlus.NcpStrict.Motion) {
            e.setX(0.0);
            e.setY(0.0);
            e.setZ(0.0);
         } else {
            if (Math.abs(e.getX()) < 0.05) {
               e.setX(0.0);
            }

            if (Math.abs(e.getZ()) < 0.05) {
               e.setZ(0.0);
            }

            e.setY(
               ElytraPlus.INSTANCE.getVertical()
                  ? (
                     MinecraftHolder.getMinecraft().options.keyJump.isDown()
                        ? ElytraPlus.INSTANCE.getYSpeed()
                        : (MinecraftHolder.getMinecraft().options.keyShift.isDown() ? -ElytraPlus.INSTANCE.getYSpeed() : 0.0)
                  )
                  : 0.0
            );
            switch (ElytraFlyGlide.WhenMappings.values[ElytraPlus.INSTANCE.getNcpStrictMode().ordinal()]) {
               case 1:
                  e.setY(-1.0000889E-12F);
                  break;
               case 2:
                  e.setY(-4.0003556E-12F);
                  break;
               case 3:
                  e.setY(2.0E-4 - (MinecraftHolder.getLocalPlayer().tickCount % 2 == 0 ? 0.0 : 1.0E-6) + Math.random() * 9.0E-7);
            }

            if (MinecraftHolder.getLocalPlayer().horizontalCollision
               && (ElytraPlus.INSTANCE.getNcpStrictMode() == ElytraPlus.NcpStrict.New || ElytraPlus.INSTANCE.getNcpStrictMode() == ElytraPlus.NcpStrict.Motion)
               && MinecraftHolder.getLocalPlayer().tickCount % 2 == 0) {
               e.setY(-0.07840000152587923);
            }

            if ((ElytraPlus.INSTANCE.getInfDurability() || ElytraPlus.INSTANCE.getNcpStrictMode() == ElytraPlus.NcpStrict.Motion)
               && !WorldUtil.INSTANCE.isFlag4()
               && Math.abs(e.getX()) < 0.121
               && Math.abs(e.getZ()) < 0.121) {
               float radians = (float)Math.toRadians(4.5 * (MinecraftHolder.getLocalPlayer().tickCount % 80));
               e.setX(Math.sin(radians) * 0.12);
               e.setZ(Math.cos(radians) * 0.12);
            }
         }
      }
   }

   @Override
   public void onAfterSendPacket(AfterSendPacket event) {
   }

   @Override
   public void onReceivePacket(ReceivePacket e) {
      if (e.getPacket() instanceof ClientboundSetEntityDataPacket) {
         ClientboundSetEntityDataPacket packet = (ClientboundSetEntityDataPacket)e.getPacket();
         if (packet.id() == MinecraftHolder.getLocalPlayer().getId()) {
            if (packet.packedItems().isEmpty()) {
               return;
            }

            e.cancel();
         }
      }
   }

   @Override
   public void onPlayerUpdate(PlayerUpdateEvent e) {
   }

   @Override
   public void update() {
   }

   @Override
   public void update2() {
      MinecraftHolder.getLocalPlayer().getAbilities().flying = false;
      MinecraftHolder.getLocalPlayer().getAbilities().setFlyingSpeed(0.05F);
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

   public final boolean isFlag() {
      return MinecraftHolder.getClientLevel()
         .getBlockCollisions(
            (Entity)MinecraftHolder.getLocalPlayer(), MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(-0.25, 0.0, -0.25).move(0.0, -0.3, 0.0)
         )
         .iterator()
         .hasNext();
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[ElytraPlus.NcpStrict.values().length];

         try {
            values2[ElytraPlus.NcpStrict.New.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[ElytraPlus.NcpStrict.Motion.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[ElytraPlus.NcpStrict.Old.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}