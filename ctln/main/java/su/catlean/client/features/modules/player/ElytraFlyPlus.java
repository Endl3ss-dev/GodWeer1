// deobf by pusia
package su.catlean.client.features.modules.player;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
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
import su.catlean.client.features.modules.rage.ElytraPlus;
import su.catlean.client.services.NotificationManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.MessageType;
import su.catlean.utils.world.WorldUtil;

public final class ElytraFlyPlus implements ElytraFlyStrategy {
   @NotNull
   public static final ElytraFlyPlus INSTANCE = new ElytraFlyPlus();
   private static boolean flag;
   private static float amount;
   private static float amount2;

   private ElytraFlyPlus() {
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      ElytraFlyPlus.flag = flag;
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      ElytraFlyPlus.amount = amount;
   }

   public final float getAmount2() {
      return amount2;
   }

   public final void setAmount2(float amount) {
      amount2 = amount;
   }

   @Override
   public void onPreSync(PreSyncEvent e) {
      ItemStack itemBySlot = MinecraftHolder.getLocalPlayer().getItemBySlot(EquipmentSlot.CHEST);
      if (itemBySlot.is(Items.ELYTRA)) {
         MinecraftHolder.getLocalPlayer().setXRot(amount2);
         if (itemBySlot.getDamageValue() > 380 && MinecraftHolder.getLocalPlayer().tickCount % 100 == 0) {
            NotificationManager.defaultImpl(
               NotificationManager.INSTANCE,
               "Elytra+",
               ElytraPlus.INSTANCE.computeText("elytra-durability", new Object[0]),
               2,
               MessageType.WARNING,
               null,
               16,
               null
            );
            LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
            MinecraftHolder.getClientLevel()
               .playSeededSound(
                  (Entity)localPlayer, localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(), (Holder)SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 10.0F, 1.0F, 0L
               );
         }
      }
   }

   @Override
   public void onPreElytra(PreElytraEvent e) {
      ElytraPlus.INSTANCE.setAmount3(MinecraftHolder.getLocalPlayer().getXRot());
      MinecraftHolder.getLocalPlayer().setXRot(amount2);
   }

   @Override
   public void onAfterElytra(AfterElytraEvent e) {
      MinecraftHolder.getLocalPlayer().setXRot(ElytraPlus.INSTANCE.getAmount3());
   }

   @Override
   public void onMove(MoveEvent e) {
   }

   @Override
   public void onAfterSendPacket(AfterSendPacket event) {
   }

   @Override
   public void onReceivePacket(ReceivePacket e) {
   }

   @Override
   public void onPlayerUpdate(PlayerUpdateEvent e) {
      amount2 = this.getAmount3();
   }

   @Override
   public void update() {
      if (MinecraftHolder.getLocalPlayer().getY() < ElytraPlus.INSTANCE.getInfiniteMaxHeight()) {
         ElytraPlus.INSTANCE.update();
      }

      flag = false;
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

   private final float getAmount3() {
      if (MinecraftHolder.getLocalPlayer().getY() < ElytraPlus.INSTANCE.getInfiniteMaxHeight()) {
         if (WorldUtil.INSTANCE.getAmount() * 72.0F < 25.0F && !flag) {
            flag = true;
         }

         if (WorldUtil.INSTANCE.getAmount() * 72.0F > 150.0F && flag) {
            flag = false;
         }
      } else {
         flag = true;
      }

      if (flag) {
         amount += 3.0F;
      } else {
         amount -= 3.0F;
      }

      BlockHitResult clip = MinecraftHolder.getClientLevel()
         .clip(
            new ClipContext(
               MinecraftHolder.getLocalPlayer().getEyePosition(),
               MinecraftHolder.getLocalPlayer()
                  .getEyePosition()
                  .add(
                     MinecraftHolder.getLocalPlayer().getDeltaMovement().x * 10.0,
                     MinecraftHolder.getLocalPlayer().getDeltaMovement().y * 10.0,
                     MinecraftHolder.getLocalPlayer().getDeltaMovement().z * 10.0
                  ),
               Block.COLLIDER,
               Fluid.NONE,
               (Entity)MinecraftHolder.getLocalPlayer()
            )
         );
      if (clip.getType() == Type.BLOCK) {
         MinecraftHolder.getLocalPlayer().setXRot(-33.0F);
         ElytraPlus.defaultImpl(ElytraPlus.INSTANCE, null, 1, null);
      }

      amount = Math.clamp(amount, -40.0F, 40.0F);
      return amount;
   }
}