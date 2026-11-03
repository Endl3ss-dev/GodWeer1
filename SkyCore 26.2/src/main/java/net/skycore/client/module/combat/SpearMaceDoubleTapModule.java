// Adapted from Prestige SpearMaceDoubleTap (dev.zprestige.prestige.fz / S0422)
package net.skycore.client.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.SpearHelper;
import net.skycore.client.event.WorldChangeEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.UseItemHelper;

public final class SpearMaceDoubleTapModule extends ClientModule {
   public static final SpearMaceDoubleTapModule INSTANCE = new SpearMaceDoubleTapModule();

   private static final byte ENTITY_HURT_EVENT = 2;
   private static final double DENSITY_FALL_THRESHOLD = 7.0;
   private static final long TAP_TIMEOUT_MS = 1000L;

   private final NumberSetting triggerFallDistance = this.add(
      new NumberSetting("Trigger Fall Distance", "Swaps to the mace after falling this far (blocks).", 3.0, 0.0, 20.0, 0.5, "")
   );

   private long triggeredAtMs;
   private boolean pendingMaceTap;

   public SpearMaceDoubleTapModule() {
      super("Spear Mace Double Tap", "Double taps people with a spear then mace combination", ModuleCategory.COMBAT);
      SkycoreClient.events().subscribe(WorldChangeEvent.class, this::onWorldChange);
   }

   @Override
   protected void onEnable() {
      this.pendingMaceTap = false;
      this.triggeredAtMs = 0L;
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         SpearHelper.warmHotbarEnchantCache(player);
      }
   }

   @Override
   protected void onDisable() {
      this.pendingMaceTap = false;
      this.triggeredAtMs = 0L;
   }

   private void onWorldChange(WorldChangeEvent event) {
      this.pendingMaceTap = false;
      this.triggeredAtMs = 0L;
   }

   public void onEntityEvent(ClientboundEntityEventPacket packet) {
      if (!this.isEnabled() || packet == null || packet.getEventId() != ENTITY_HURT_EVENT) {
         return;
      }

      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null || mc.gameMode == null || !player.isAlive() || player.isSpectator()) {
         return;
      }

      Entity hurtEntity = packet.getEntity(mc.level);
      if (hurtEntity == null || hurtEntity == player) {
         return;
      }

      if (player.fallDistance <= this.triggerFallDistance.get()) {
         return;
      }

      int maceSlot = findBestMaceSlot(player);
      if (maceSlot == -1) {
         return;
      }

      if (player.isUsingItem()) {
         mc.gameMode.releaseUsingItem(player);
      }

      UseItemHelper.switchToHotbarSlot(maceSlot);
      this.triggeredAtMs = System.currentTimeMillis();
      this.pendingMaceTap = true;
   }

   @Override
   protected void onTick() {
      if (!this.pendingMaceTap) {
         return;
      }

      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null || mc.gameMode == null || !player.isAlive() || player.isSpectator()) {
         this.pendingMaceTap = false;
         return;
      }

      if (System.currentTimeMillis() - this.triggeredAtMs > TAP_TIMEOUT_MS) {
         this.pendingMaceTap = false;
         return;
      }

      if (!player.getMainHandItem().is(Items.MACE)) {
         this.pendingMaceTap = false;
         return;
      }

      if (mc.hitResult instanceof EntityHitResult entityHitResult) {
         Entity target = entityHitResult.getEntity();
         if (target != null && target != player && target.isAlive()) {
            mc.gameMode.attack(player, target);
            player.swing(InteractionHand.MAIN_HAND);
            this.pendingMaceTap = false;
         }
      }
   }

   private static int findBestMaceSlot(LocalPlayer player) {
      boolean preferDensity = player.fallDistance > DENSITY_FALL_THRESHOLD;
      int bestSlot = -1;
      int bestScore = -1;

      for (int slot = 0; slot < SpearHelper.HOTBAR_SIZE; slot++) {
         ItemStack stack = player.getInventory().getItem(slot);
         if (!stack.isEmpty() && stack.is(Items.MACE)) {
            int score = preferDensity ? SpearHelper.densityLevel(stack) : SpearHelper.breachLevel(stack);
            if (score > bestScore) {
               bestScore = score;
               bestSlot = slot;
            }
         }
      }

      return bestSlot;
   }
}
